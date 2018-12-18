/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.jaxrs.swagger.v2.internal.extensions;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.property.PropertySetRefIntrospector;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.jaxrs.swagger.annotations.ApiPropertySetModel;
import com.holonplatform.jaxrs.swagger.internal.SwaggerExtensions;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.internal.types.PropertyBoxTypeInfo;
import com.holonplatform.jaxrs.swagger.internal.types.PropertyBoxTypeResolver;
import com.holonplatform.jaxrs.swagger.v2.internal.context.SwaggerContext;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.ext.AbstractSwaggerExtension;
import io.swagger.jaxrs.ext.SwaggerExtension;
import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import io.swagger.util.ParameterProcessor;

/**
 * A {@link SwaggerExtension} to handle {@link PropertyBox} type model properties in operations parameters and response
 * types.
 * <p>
 * The {@link PropertySetRef} annotation can be used to declare the {@link PropertySet} of each operation
 * {@link PropertyBox} to generate a detailed API documentation, including the property definitions of the
 * {@link PropertyBox}.
 * </p>
 * 
 * <p>
 * This extension is automatically loaded and registered in Swagger using Java service extensions.
 * </p>
 * 
 * @since 5.0.0
 */
public class SwaggerV2ApiExtension extends AbstractSwaggerExtension {

	private static final Logger LOGGER = SwaggerLogger.create();

	/*
	 * (non-Javadoc)
	 * @see io.swagger.jaxrs.ext.AbstractSwaggerExtension#decorateOperation(io.swagger.models.Operation,
	 * java.lang.reflect.Method, java.util.Iterator)
	 */
	@Override
	public void decorateOperation(Operation operation, Method method, Iterator<SwaggerExtension> chain) {
		super.decorateOperation(operation, method, chain);

		// responses property set
		final Map<String, Response> responses = operation.getResponses();
		if (responses != null) {
			getResponsePropertySet(method).ifPresent(aps -> {
				PropertySet<?> propertySet = PropertySetRefIntrospector.get().getPropertySet(aps);
				if (propertySet != null) {
					ApiPropertySetModel psm = getResponsePropertySetModel(method).orElse(null);

					// responses
					for (Response response : responses.values()) {
						ArrayModel ap = isPropertyBoxArrayModelType(response.getResponseSchema());
						if (ap != null) {
							final Property propertyBoxProperty = SwaggerV2PropertyBoxModelConverter.modelToProperty(buildPropertyBoxModel(propertySet, true, false,
									(psm != null) ? AnnotationUtils.getStringValue(psm.value()) : null,
									(psm != null) ? AnnotationUtils.getStringValue(psm.description()) : null,
									(psm != null) ? AnnotationUtils.getStringValue(psm.reference()) : null));
							ap.items(propertyBoxProperty);
							response.setResponseSchema(ap);
						} else if (isPropertyBoxModelType(response.getResponseSchema())) {
							final Model propertyBoxModel = buildPropertyBoxModel(propertySet, true, false,
									(psm != null) ? AnnotationUtils.getStringValue(psm.value()) : null,
									(psm != null) ? AnnotationUtils.getStringValue(psm.description()) : null,
									(psm != null) ? AnnotationUtils.getStringValue(psm.reference()) : null);
							response.responseSchema(propertyBoxModel);
						}
					}
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.jaxrs.ext.AbstractSwaggerExtension#extractParameters(java.util.List, java.lang.reflect.Type,
	 * java.util.Set, java.util.Iterator)
	 */
	@Override
	public List<Parameter> extractParameters(List<Annotation> annotations, Type type, Set<Type> typesToSkip,
			Iterator<SwaggerExtension> chain) {

		// check PropertyBox type and body parameter case
		if (isBodyParameter(annotations)) {
			final PropertyBoxTypeInfo pbType = PropertyBoxTypeResolver.resolvePropertyBoxType(type).orElse(null);
			if (pbType != null) {
				// check property set
				final PropertySetRef aps = hasApiPropertySet(annotations);
				if (aps != null) {
					PropertySet<?> propertySet = PropertySetRefIntrospector.get().getPropertySet(aps);
					if (propertySet != null) {

						// Skip PropertyBox type
						Set<Type> skip = new HashSet<>();
						if (typesToSkip != null) {
							skip.addAll(typesToSkip);
						}
						skip.add(PropertyBox.class);
						skip.add(PropertyBox[].class);

						// load other parameters, if any
						List<Parameter> parameters = new LinkedList<>();
						if (chain.hasNext()) {
							List<Parameter> ps = chain.next().extractParameters(annotations, type, skip, chain);
							if (ps != null) {
								parameters.addAll(ps);
							}
						}

						ApiPropertySetModel psm = hasApiPropertySetModel(annotations);

						final Model model = buildPropertyBoxModel(propertySet, false,
								(pbType.isContainerType() && !pbType.isMap()),
								(psm != null) ? AnnotationUtils.getStringValue(psm.value()) : null,
								(psm != null) ? AnnotationUtils.getStringValue(psm.description()) : null,
								(psm != null) ? AnnotationUtils.getStringValue(psm.reference()) : null);
						BodyParameter bp = new BodyParameter();
						bp.setRequired(isParameterRequired(annotations));
						bp.schema(model);
						Parameter parameter = ParameterProcessor.applyAnnotations(new Swagger(), bp, type, annotations);
						if (parameter != null) {
							if (parameter instanceof BodyParameter) {
								((BodyParameter) parameter).schema(model);
							}
							parameters.add(parameter);
						}

						return parameters;
					}
				}
			}
		}

		return super.extractParameters(annotations, type, typesToSkip, chain);
	}

	/**
	 * Check whether a parameter is a <em>body</em> parameter, i.e. it is not annotated with standard JAX-RS parameters
	 * annotations.
	 * @param annotations Annotations to scan
	 * @return <code>true</code> if the parameter is a <em>body</em> parameter
	 */
	private static boolean isBodyParameter(List<Annotation> annotations) {
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				if (annotation instanceof PathParam || annotation instanceof QueryParam
						|| annotation instanceof HeaderParam || annotation instanceof CookieParam
						|| annotation instanceof FormParam) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Check whether the {@link PropertySetRef} annotation is present in given annotations list.
	 * @param annotations Annotations to scan
	 * @return If the {@link PropertySetRef} annotation is present in given annotations list, returns it
	 */
	private static PropertySetRef hasApiPropertySet(List<Annotation> annotations) {
		List<PropertySetRef> as = AnnotationUtils.getAnnotations(annotations, PropertySetRef.class);
		if (!as.isEmpty()) {
			return as.get(0);
		}
		return null;
	}

	/**
	 * Check whether the {@link ApiPropertySetModel} annotation is present in given annotations list.
	 * @param annotations Annotations to scan
	 * @return If the {@link ApiPropertySetModel} annotation is present in given annotations list, returns it
	 */
	private static ApiPropertySetModel hasApiPropertySetModel(List<Annotation> annotations) {
		List<ApiPropertySetModel> as = AnnotationUtils.getAnnotations(annotations, ApiPropertySetModel.class);
		if (!as.isEmpty()) {
			return as.get(0);
		}
		return null;
	}

	/**
	 * Check whether a parameter is required using either the {@link ApiParam} or the {@link ApiImplicitParam}
	 * annotation, if available from given annotations list.
	 * @param annotations Annotations to scan
	 * @return <code>true</code> if the parameter is declared as required
	 */
	private static boolean isParameterRequired(List<Annotation> annotations) {
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				if (annotation instanceof ApiParam) {
					if (((ApiParam) annotation).required()) {
						return true;
					}
				}
				if (annotation instanceof ApiImplicitParam) {
					if (((ApiImplicitParam) annotation).required()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Check whether the given <code>method</code> return type is annotated with the {@link PropertySetRef} annotation.
	 * @param method Method to inspect
	 * @return Optional {@link PropertySetRef} annotation, if available
	 */
	private static Optional<PropertySetRef> getResponsePropertySet(Method method) {
		final AnnotatedType rt = method.getAnnotatedReturnType();
		if (rt != null) {
			if (rt.isAnnotationPresent(PropertySetRef.class)) {
				return Optional.of(rt.getAnnotation(PropertySetRef.class));
			}
			// check meta-annotations
			List<PropertySetRef> annotations = AnnotationUtils.getAnnotations(rt, PropertySetRef.class);
			if (!annotations.isEmpty()) {
				return Optional.ofNullable(annotations.get(0));
			}
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
		final AnnotatedType rt = method.getAnnotatedReturnType();
		if (rt != null) {
			if (rt.isAnnotationPresent(ApiPropertySetModel.class)) {
				return Optional.of(rt.getAnnotation(ApiPropertySetModel.class));
			}
			// check meta-annotations
			List<ApiPropertySetModel> annotations = AnnotationUtils.getAnnotations(rt, ApiPropertySetModel.class);
			if (!annotations.isEmpty()) {
				return Optional.ofNullable(annotations.get(0));
			}
		}
		return Optional.empty();
	}

	/**
	 * Build a {@link PropertyBox} type Swagger model using given <code>propertySet</code>.
	 * @param propertySet Property set
	 * @param includeReadOnly Whether to include {@link PropertySet} read-only properties
	 * @param array <code>true</code> to create an array model type
	 * @param modelName If not null, define a Model with given name and use a {@link RefModel} to reference it
	 * @param modelDescription Model description
	 * @param modelReference Model reference
	 * @return Swagger model
	 */
	private static Model buildPropertyBoxModel(PropertySet<?> propertySet, boolean includeReadOnly, boolean array,
			String modelName, String modelDescription, String modelReference) {
		Function<Type, io.swagger.models.properties.Property> resolver = t -> io.swagger.converter.ModelConverters.getInstance().readAsProperty(t);
		Model resolvedModel = SwaggerV2PropertyBoxModelConverter.buildPropertyBoxSchema(propertySet, resolver, includeReadOnly);
		
		if (array) {
			// Array model
			ArrayModel model = new ArrayModel();
			model.items(SwaggerV2PropertyBoxModelConverter.modelToProperty(resolvedModel));
			return model;
		}

		// Check ref model
		if (modelName != null && !modelName.trim().equals("")) {
			if (!definePropertySetModel(resolvedModel, modelName, modelDescription)) {
				LOGGER.warn("Failed to define PropertySet Model named [" + modelName
						+ "]: no Swagger instance available from resolution context.");
			}
			return new RefModel(modelName);
		}

		// Simple model
		return resolvedModel;
	}

	private static boolean definePropertySetModel(Model model, String name, String description) {
		return SwaggerContext.getSwagger().map(swagger -> {
			// check already defined
			if (!hasSchema(swagger, name)) {
				// build and define
				model.setTitle(name);
				if (description != null) {
					model.setDescription(description);
				}
				// add schema to include
				SwaggerContext.getModels().ifPresent(schemas -> {
					if (!schemas.containsKey(name)) {
						schemas.put(name, model);
					}
				});
			}
			return true;
		}).orElse(false);
	}
	
	/**
	 * Checks if given schema name is already defined in the provided Swagger instance.
	 * @param swagger The Swagger instance
	 * @param name The schema name
	 * @return <code>true</code> if the schema name is already defined, <code>false</code> otherwise
	 */
	private static boolean hasSchema(Swagger swagger, String name) {
		if (swagger != null && name != null) {
			if (swagger.getDefinitions() != null) {
				return swagger.getDefinitions().containsKey(name);
			}
		}
		return false;
	}
	
	/**
	 * Check whether the given property is of {@link PropertyBox} type using the {@link SwaggerExtensions#MODEL_TYPE}
	 * extension name.
	 * @param property Property to check
	 * @return <code>true</code> if given property is of {@link PropertyBox} type
	 */
	private static boolean isPropertyBoxPropertyType(Property property) {
		if (property != null && property.getVendorExtensions() != null
				&& property.getVendorExtensions().containsKey(SwaggerExtensions.MODEL_TYPE.getExtensionName())
				&& PropertyBox.class.getName()
						.equals(property.getVendorExtensions().get(SwaggerExtensions.MODEL_TYPE.getExtensionName()))) {
			return true;
		}
		return false;
	}

	/**
	 * Check whether the given model is of {@link PropertyBox} type using the {@link SwaggerExtensions#MODEL_TYPE}
	 * extension name.
	 * @param model Model to check
	 * @return <code>true</code> if given model is of {@link PropertyBox} type
	 */
	private static boolean isPropertyBoxModelType(Model model) {
		if (model != null && model.getVendorExtensions() != null
				&& model.getVendorExtensions().containsKey(SwaggerExtensions.MODEL_TYPE.getExtensionName())
				&& PropertyBox.class.getName()
						.equals(model.getVendorExtensions().get(SwaggerExtensions.MODEL_TYPE.getExtensionName()))) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the given model is an {@link ArrayModel} of {@link PropertyBox} type using the
	 * {@link SwaggerExtensions#MODEL_TYPE} extension name.
	 * @param model Model to check
	 * @return if the model is of {@link PropertyBox} type, return such model casted to {@link ArrayModel},
	 *         <code>null</code> otherwise
	 */
	private static ArrayModel isPropertyBoxArrayModelType(Model model) {
		if (model != null && ArrayModel.class.isAssignableFrom(model.getClass())) {
			final Property items = ((ArrayModel) model).getItems();
			if (isPropertyBoxPropertyType(items)) {
				return (ArrayModel) model;
			}
		}
		return null;
	}

}
