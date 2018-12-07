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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.property.PropertySetRefIntrospector;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.jaxrs.swagger.SwaggerExtensions;
import com.holonplatform.jaxrs.swagger.annotations.ApiPropertySetModel;
import com.holonplatform.jaxrs.swagger.v3.internal.types.PropertyBoxTypeInfo;
import com.holonplatform.jaxrs.swagger.v3.internal.types.PropertyBoxTypeResolver;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

/**
 * A {@link ModelConverter} to handle the {@link PropertyBox} type.
 * <p>
 * The {@link PropertySetRef} annotation is used to resolve the {@link PropertySet} to use to build the
 * {@link PropertyBox} schema.
 * </p>
 * 
 * @since 5.2.0
 */
public class PropertyBoxModelConverter implements ModelConverter {

	private static final Logger LOGGER = SwaggerLogger.create();

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.core.converter.ModelConverter#resolve(io.swagger.v3.core.converter.AnnotatedType,
	 * io.swagger.v3.core.converter.ModelConverterContext, java.util.Iterator)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Schema resolve(AnnotatedType annotatedType, ModelConverterContext context, Iterator<ModelConverter> chain) {
		final PropertyBoxTypeInfo pbType = PropertyBoxTypeResolver.resolvePropertyBoxType(annotatedType.getType())
				.orElse(null);
		// PropertyBox type
		if (pbType != null) {
			if (pbType.isContainerType()) {
				// container
				if (pbType.isMap()) {
					// Map
					Schema mapModel = new ArraySchema()
							.additionalProperties(getPropertyBoxSchema(annotatedType, context)
									.orElseGet(() -> delegateToExtensionResolution()));
					return mapModel;
				} else {
					// Array
					ArraySchema arrayModel = new ArraySchema().items(getPropertyBoxSchema(annotatedType, context)
							.orElseGet(() -> delegateToExtensionResolution()));
					if (pbType.isUniqueItems()) {
						arrayModel.setUniqueItems(true);
					}
					return arrayModel;
				}
			}
			// Single
			return getPropertyBoxSchema(annotatedType, context).orElseGet(() -> delegateToExtensionResolution());
		}

		// Default behaviour
		if (chain.hasNext()) {
			return chain.next().resolve(annotatedType, context, chain);
		}
		return null;
	}

	/**
	 * Get the Schema to use to delegate the actual resolution to the {@link PropertyModelOpenAPIExtension}.
	 * @return Delgate resolution schema
	 */
	private static Schema<Object> delegateToExtensionResolution() {
		final Schema<Object> model = new Schema<>();
		model.setType("object");
		model.setTitle("PropertyBox");
		model.addExtension(SwaggerExtensions.MODEL_TYPE.getExtensionName(), PropertyBox.class.getName());
		return model;
	}

	/**
	 * Get the {@link PropertyBox} type Schema from given type, if a {@link PropertySet} is available using a
	 * {@link PropertySetRef} annotation.
	 * @param type The type to resolve
	 * @param context Converter context
	 * @return Optional {@link PropertyBox} type Schema
	 */
	private static Optional<Schema<Object>> getPropertyBoxSchema(AnnotatedType type, ModelConverterContext context) {
		return getPropertySet(type).map(ref -> PropertySetRefIntrospector.get().getPropertySet(ref))
				.map(propertySet -> {
					final Function<AnnotatedType, Schema<?>> resolver = t -> context.resolve(t);
					// check API model
					return getPropertySetModel(type).map(apiModel -> {
						final String name = apiModel.value().trim();
						// check defined
						if (!context.getDefinedModels().containsKey(name)) {
							Schema<Object> schema = buildPropertyBoxSchema(propertySet, resolver, false);
							// define model
							schema.setName(name);
							if (AnnotationUtils.getStringValue(apiModel.description()) != null) {
								schema.setDescription(apiModel.description());
							}
							context.defineModel(name, schema);
						}
						// return as ref
						Schema<Object> refModel = new Schema<>();
						refModel.setType("object");
						refModel.set$ref(name);
						return refModel;
					}).orElseGet(() -> {
						return buildPropertyBoxSchema(propertySet, resolver, false);
					});
				});
	}

	/**
	 * Check whether the given <code>type</code> is annotated with the {@link PropertySetRef} annotation.
	 * @param type The type to inspect
	 * @return Optional {@link PropertySetRef} annotation, if available
	 */
	private static Optional<PropertySetRef> getPropertySet(AnnotatedType type) {
		if (type.getCtxAnnotations() != null && type.getCtxAnnotations().length > 0) {
			// check meta-annotations
			List<PropertySetRef> annotations = AnnotationUtils.getAnnotations(Arrays.asList(type.getCtxAnnotations()),
					PropertySetRef.class);
			if (!annotations.isEmpty()) {
				return Optional.ofNullable(annotations.get(0));
			}
		}
		return Optional.empty();
	}

	/**
	 * Check whether the given <code>type</code> is annotated with the {@link ApiPropertySetModel} annotation.
	 * @param type The type to inspect
	 * @return Optional {@link ApiPropertySetModel} annotation, if available
	 */
	private static Optional<ApiPropertySetModel> getPropertySetModel(AnnotatedType type) {
		if (type.getCtxAnnotations() != null && type.getCtxAnnotations().length > 0) {
			// check meta-annotations
			List<ApiPropertySetModel> annotations = AnnotationUtils
					.getAnnotations(Arrays.asList(type.getCtxAnnotations()), ApiPropertySetModel.class);
			if (!annotations.isEmpty()) {
				ApiPropertySetModel am = annotations.get(0);
				if (am != null && am.value() != null && !am.value().trim().equals("")) {
					return Optional.ofNullable(annotations.get(0));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Resolve the Schema properties for given {@link PropertySet}.
	 * @param propertySet The property set
	 * @param resolver The property type resolver
	 * @param includeReadOnly Whether to include read-only properties in schema
	 * @return The resolved schema properties
	 */
	@SuppressWarnings("rawtypes")
	private static Map<String, Schema> resolveSchemaProperties(PropertySet<?> propertySet,
			Function<AnnotatedType, Schema<?>> resolver, boolean includeReadOnly) {
		Map<String, Schema> properties = new LinkedHashMap<>();
		for (Property<?> property : propertySet) {
			if (includeReadOnly || !property.isReadOnly()) {
				if (Path.class.isAssignableFrom(property.getClass())) {
					final String name = ((Path<?>) property).relativeName();
					AnnotatedType type = new AnnotatedType().type(property.getType()).schemaProperty(true)
							.propertyName(name).skipOverride(true).skipSchemaName(true);
					Schema<?> schema = resolver.apply(type);
					if (schema != null) {
						properties.put(name, schema);
					} else {
						LOGGER.warn("Failed to resolve Schema for PropertySet property [" + property + "]");
					}
				}
			}

		}
		return properties;
	}

	/**
	 * Build a {@link Schema} for given {@link PropertySet}.
	 * @param propertySet The property set
	 * @param resolver The property type resolver
	 * @param includeReadOnly Whether to include read-only properties in schema
	 * @return The Schema
	 */
	public static Schema<Object> buildPropertyBoxSchema(PropertySet<?> propertySet,
			Function<AnnotatedType, Schema<?>> resolver, boolean includeReadOnly) {
		final Schema<Object> model = new Schema<>();
		model.setType("object");
		model.setTitle("PropertyBox");
		model.setProperties(resolveSchemaProperties(propertySet, resolver, includeReadOnly));
		return model;
	}

}
