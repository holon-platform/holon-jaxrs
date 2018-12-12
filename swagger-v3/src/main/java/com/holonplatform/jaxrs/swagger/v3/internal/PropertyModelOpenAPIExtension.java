/*
 * Copyright 2016-2018 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.jaxrs.swagger.v3.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import javax.ws.rs.core.Response;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.property.PropertySetRefIntrospector;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.jaxrs.swagger.SwaggerExtensions;
import com.holonplatform.jaxrs.swagger.annotations.ApiPropertySetModel;
import com.holonplatform.jaxrs.swagger.v3.internal.types.SwaggerTypeUtils;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.jaxrs2.ext.AbstractOpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

/**
 * A {@link OpenAPIExtension} to handle the {@link PropertyBox} type for method result types.
 * 
 * @since 5.2.0
 */
public class PropertyModelOpenAPIExtension extends AbstractOpenAPIExtension {

	private static final Logger LOGGER = SwaggerLogger.create();

	public PropertyModelOpenAPIExtension() {
		super();
		// register model converters
		ModelConverters.getInstance().addConverter(new PropertyBoxModelConverter());
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.jaxrs2.ext.AbstractOpenAPIExtension#decorateOperation(io.swagger.v3.oas.models.Operation,
	 * java.lang.reflect.Method, java.util.Iterator)
	 */
	@Override
	public void decorateOperation(Operation operation, Method method, Iterator<OpenAPIExtension> chain) {
		// check responses
		final ApiResponses responses = operation.getResponses();
		if (responses != null) {
			if (responses.getDefault() != null || !responses.isEmpty()) {
				// check type
				if (isPropertyBoxResponseType(method)) {
					// get the property set
					final PropertySet<?> propertySet = getResponsePropertySet(method)
							.map(ref -> PropertySetRefIntrospector.get().getPropertySet(ref)).orElse(null);
					if (propertySet != null) {
						final Optional<ApiPropertySetModel> apiModel = getResponsePropertySetModel(method);
						if (responses.getDefault() != null) {
							parseResponse(propertySet, apiModel, responses.getDefault());
						}
						responses.values().forEach(response -> {
							if (response != null) {
								parseResponse(propertySet, apiModel, response);
							}
						});
					} else {
						LOGGER.warn("Failed to obtain a PropertySet to build the PropertyBox Schema for method ["
								+ method.getName() + "] response in class [" + method.getDeclaringClass().getName()
								+ "]. Please check the @PropertySetRef annotation.");
					}
				}
			}
		}

		// default behaviour
		super.decorateOperation(operation, method, chain);
	}

	/**
	 * Parse given response, providing {@link PropertyBox} type schema definition when a temporary
	 * {@link SwaggerExtensions#MODEL_TYPE} named extension is found.
	 * @param propertySet The property set to use
	 * @param apiModel The Optional API model
	 * @param response The response to parse
	 */
	private static void parseResponse(PropertySet<?> propertySet, Optional<ApiPropertySetModel> apiModel,
			ApiResponse response) {
		if (response.getContent() != null) {
			for (Entry<String, MediaType> entry : response.getContent().entrySet()) {
				final MediaType mediaType = entry.getValue();
				if (mediaType != null) {
					ArraySchema array = isPropertyBoxArraySchemaType(mediaType.getSchema());
					if (array != null) {
						array.setItems(getPropertyBoxSchema(propertySet, apiModel));
					} else if (isPropertyBoxSchemaType(mediaType.getSchema())) {
						mediaType.setSchema(getPropertyBoxSchema(propertySet, apiModel));
					}
				}
			}
		}
	}

	private static Schema<Object> getPropertyBoxSchema(PropertySet<?> propertySet,
			Optional<ApiPropertySetModel> apiPropertySetModel) {
		final Function<AnnotatedType, Schema<?>> resolver = t -> ModelConverters.getInstance()
				.resolveAsResolvedSchema(t).schema;
		// check API model
		return apiPropertySetModel.map(apiModel -> {
			final String name = apiModel.value().trim();
			// check defined
			if (!definePropertySetModel(name, AnnotationUtils.getStringValue(apiModel.description()), propertySet,
					resolver, true)) {
				LOGGER.warn("Failed to define PropertySet Schema named [" + name
						+ "]: no OpenAPI available from resolution context. Ensure a proper OpenAPIContextListener is bound to the OpenApiReader.");
			}
			// return as ref
			Schema<Object> refModel = new Schema<>();
			refModel.setType("object");
			refModel.set$ref(name);
			return refModel;
		}).orElseGet(() -> {
			return PropertyBoxModelConverter.buildPropertyBoxSchema(propertySet, resolver, true);
		});
	}

	/**
	 * If a current {@link OpenAPI} instance is available from context, build the schema for given property set and add
	 * it to the {@link OpenAPI} instance, if not already present.
	 * @param name Schema name
	 * @param description Optional schema description
	 * @param propertySet The property set
	 * @param resolver The property type resolver
	 * @param includeReadOnly Whether to include read-only properties in schema
	 * @return <code>true</code> if the {@link OpenAPI} instance is available from context and the schema was defined,
	 *         <code>false</code> otherwise
	 */
	private static boolean definePropertySetModel(String name, String description, PropertySet<?> propertySet,
			Function<AnnotatedType, Schema<?>> resolver, boolean includeReadOnly) {
		return OpenAPIResolutionContext.getOpenAPI().map(openAPI -> {
			// check already defined
			if (!hasSchema(openAPI, name)) {
				// build and define
				Schema<Object> schema = PropertyBoxModelConverter.buildPropertyBoxSchema(propertySet, resolver,
						includeReadOnly);
				schema.setTitle(name);
				if (description != null) {
					schema.setDescription(description);
				}
				// add schema to include
				OpenAPIResolutionContext.getSchemas().ifPresent(schemas -> {
					if (!schemas.containsKey(name)) {
						schemas.put(name, schema);
					}
				});
			}
			return true;
		}).orElse(false);
	}

	/**
	 * Checks if given schema name is already defined in the provided OpenAPI instance.
	 * @param openAPI The OpenAPI instance
	 * @param name The schema name
	 * @return <code>true</code> if the schema name is already defined, <code>false</code> otherwise
	 */
	private static boolean hasSchema(OpenAPI openAPI, String name) {
		if (openAPI != null && name != null) {
			if (openAPI.getComponents() != null && openAPI.getComponents().getSchemas() != null) {
				return openAPI.getComponents().getSchemas().containsKey(name);
			}
		}
		return false;
	}

	/**
	 * Checks whether to method return type should be parsed as a {@link PropertyBox} type.
	 * @param method The method
	 * @return <code>true</code> if is a {@link PropertyBox} response type
	 */
	private static boolean isPropertyBoxResponseType(Method method) {
		if (PropertyBox.class.isAssignableFrom(method.getReturnType())) {
			return true;
		}
		if (PropertyBox[].class.isAssignableFrom(method.getReturnType())) {
			return true;
		}
		if (Collection.class.isAssignableFrom(method.getReturnType())) {
			return getReturnTypeArgument(method).flatMap(t -> SwaggerTypeUtils.getClassFromType(t))
					.map(t -> PropertyBox.class.isAssignableFrom(t)).orElse(false);
		}
		if (Response.class.isAssignableFrom(method.getReturnType())) {
			return getAnnotation(method.getAnnotatedReturnType(), PropertySetRef.class).isPresent();
		}
		return false;
	}

	/**
	 * Get the first type argument of the method return type, if present.
	 * @param method The method
	 * @return Optional first type argument of the method return type
	 */
	private static Optional<Type> getReturnTypeArgument(Method method) {
		Type returnType = method.getGenericReturnType();
		if (returnType != null && returnType instanceof ParameterizedType) {
			Type[] typeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
			if (typeArguments != null && typeArguments.length > 0) {
				return Optional.ofNullable(typeArguments[0]);
			}
		}
		return Optional.empty();
	}

	/**
	 * Check whether the given <code>method</code> return type is annotated with the {@link PropertySetRef} annotation.
	 * @param method Method to inspect
	 * @return Optional {@link PropertySetRef} annotation, if available
	 */
	private static Optional<PropertySetRef> getResponsePropertySet(Method method) {
		Optional<PropertySetRef> annotation = getAnnotation(method.getAnnotatedReturnType(), PropertySetRef.class);
		if (annotation.isPresent()) {
			return annotation;
		}
		// check array
		if (method.getAnnotatedReturnType() instanceof AnnotatedArrayType) {
			return getAnnotation(
					((AnnotatedArrayType) method.getAnnotatedReturnType()).getAnnotatedGenericComponentType(),
					PropertySetRef.class);
		}
		return Optional.empty();
	}

	/**
	 * Check whether the given <code>method</code> return type is annotated with the {@link ApiPropertySetModel}
	 * annotation.
	 * @param method Method to inspect
	 * @return Optional {@link ApiPropertySetModel} annotation, if available
	 */
	private static Optional<ApiPropertySetModel> getResponsePropertySetModel(Method method) {
		Optional<ApiPropertySetModel> annotation = getAnnotation(method.getAnnotatedReturnType(),
				ApiPropertySetModel.class);
		if (annotation.isPresent()) {
			return annotation;
		}
		// check array
		if (method.getAnnotatedReturnType() instanceof AnnotatedArrayType) {
			return getAnnotation(
					((AnnotatedArrayType) method.getAnnotatedReturnType()).getAnnotatedGenericComponentType(),
					ApiPropertySetModel.class);
		}
		return Optional.empty();
	}

	private static <A extends Annotation> Optional<A> getAnnotation(java.lang.reflect.AnnotatedType type,
			Class<A> annotationType) {
		if (type != null) {
			// type
			if (type.isAnnotationPresent(annotationType)) {
				return Optional.of(type.getAnnotation(annotationType));
			}
			// check meta-annotations
			List<A> annotations = AnnotationUtils.getAnnotations(type, annotationType);
			if (!annotations.isEmpty()) {
				return Optional.ofNullable(annotations.get(0));
			}
		}
		return Optional.empty();
	}

	/**
	 * Check whether the given Schema is of {@link PropertyBox} type using the {@link SwaggerExtensions#MODEL_TYPE}
	 * extension name.
	 * @param schema The Schema to check
	 * @return <code>true</code> if given model is of {@link PropertyBox} type
	 */
	private static boolean isPropertyBoxSchemaType(Schema<?> schema) {
		if (schema != null && schema.getExtensions() != null
				&& schema.getExtensions().containsKey(SwaggerExtensions.MODEL_TYPE.getExtensionName())
				&& PropertyBox.class.getName()
						.equals(schema.getExtensions().get(SwaggerExtensions.MODEL_TYPE.getExtensionName()))) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the given model is an {@link ArraySchema} of {@link PropertyBox} type using the
	 * {@link SwaggerExtensions#MODEL_TYPE} extension name.
	 * @@param schema The Schema to check
	 * @return if the model is of {@link PropertyBox} type, return such model casted to {@link ArraySchema},
	 *         <code>null</code> otherwise
	 */
	private static ArraySchema isPropertyBoxArraySchemaType(Schema<?> schema) {
		if (schema != null && ArraySchema.class.isAssignableFrom(schema.getClass())) {
			final Schema<?> items = ((ArraySchema) schema).getItems();
			if (isPropertyBoxSchemaType(items)) {
				return (ArraySchema) schema;
			}
		}
		return null;
	}

}
