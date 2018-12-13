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
package com.holonplatform.jaxrs.swagger.v3.internal.integration;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.holonplatform.core.Path;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.BuiltinValidator;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.ValidatorDescriptor;
import com.holonplatform.core.internal.property.PropertySetRefIntrospector;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.CollectionProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.jaxrs.swagger.SwaggerExtensions;
import com.holonplatform.jaxrs.swagger.annotations.ApiPropertySetModel;
import com.holonplatform.jaxrs.swagger.v3.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.v3.internal.resolver.SwaggerPropertySetSerializationTreeResolver;
import com.holonplatform.jaxrs.swagger.v3.internal.types.PropertyBoxTypeInfo;
import com.holonplatform.jaxrs.swagger.v3.internal.types.PropertyBoxTypeResolver;
import com.holonplatform.json.model.PropertySetSerializationNode;
import com.holonplatform.json.model.PropertySetSerializationTree;

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
	 * Get the Schema to use to delegate the actual resolution to the {@link PropertyModelOpenApiExtension}.
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
							// define model
							Schema<Object> schema = buildPropertyBoxSchema(propertySet, resolver, false);
							schema.setTitle(name);
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

	// -------- PropertyBox Schema resolution

	/**
	 * Build a {@link Schema} for given {@link PropertySet}.
	 * @param propertySet The property set
	 * @param resolver The type resolver
	 * @param includeReadOnly Whether to include read-only properties in schema
	 * @return The Schema
	 */
	public static Schema<Object> buildPropertyBoxSchema(PropertySet<?> propertySet,
			Function<AnnotatedType, Schema<?>> resolver, boolean includeReadOnly) {
		return resolvePropertyBox(null, propertySet, resolver, includeReadOnly);
	}

	/**
	 * Resolve a {@link Schema} for given {@link PropertySet}.
	 * @param parent Optional parent Schema
	 * @param propertySet The property set
	 * @param resolver The type resolver
	 * @param includeReadOnly Whether to include read-only properties in schema
	 * @return The resolved Schema
	 */
	private static Schema<Object> resolvePropertyBox(Schema<?> parent, PropertySet<?> propertySet,
			Function<AnnotatedType, Schema<?>> resolver, boolean includeReadOnly) {
		final PropertySetSerializationTree tree = SwaggerPropertySetSerializationTreeResolver.getDefault()
				.resolve(propertySet);
		return resolvePropertyBoxNodes(parent, tree, resolver, includeReadOnly);
	}

	/**
	 * Resolve given serialization nodes.
	 * @param parent Optional parent Schema
	 * @param nodes Node to resolve
	 * @param resolver Type resolver
	 * @param includeReadOnly Whether to include read-only properties in schema
	 * @return The resolved PropertyBox type schema
	 */
	private static Schema<Object> resolvePropertyBoxNodes(Schema<?> parent,
			Iterable<PropertySetSerializationNode> nodes, Function<AnnotatedType, Schema<?>> resolver,
			boolean includeReadOnly) {
		final Schema<Object> model = new Schema<>();
		model.setType("object");
		if (parent == null) {
			model.setTitle("PropertyBox");
		}
		// properties
		@SuppressWarnings("rawtypes")
		final Map<String, Schema> properties = new LinkedHashMap<>();
		for (PropertySetSerializationNode node : nodes) {
			Schema<?> schema = null;
			Optional<Property<?>> property = isValidNodeProperty(node);
			if (property.isPresent()) {
				schema = resolveSchemaProperty(model, property.get(), node.getName(), resolver, includeReadOnly);
			} else {
				schema = resolvePropertyBoxNodes(model, node.getChildren(), resolver, includeReadOnly);
			}
			if (schema != null) {
				properties.put(node.getName(), schema);
			}
		}
		model.setProperties(properties);
		return model;
	}

	/**
	 * Checks whether a node represents a property and the property is valid, i.e. it is a {@link Path}.
	 * @param node The node
	 * @return <code>true</code> if the node represents a valid property
	 */
	private static Optional<Property<?>> isValidNodeProperty(PropertySetSerializationNode node) {
		return node.getProperty().filter(p -> Path.class.isAssignableFrom(p.getClass()));
	}

	/**
	 * Resolve the Schema of a single {@link Property}.
	 * @param parent Parent schema
	 * @param property The property to resolve
	 * @param name The property name
	 * @param resolver The type resolver
	 * @param includeReadOnly Whether to include read-only properties in schema
	 * @return The resolved Schema, or <code>null</code>
	 */
	private static Schema<?> resolveSchemaProperty(Schema<?> parent, Property<?> property, String name,
			Function<AnnotatedType, Schema<?>> resolver, boolean includeReadOnly) {
		// check nested PropertyBox
		if (PropertyBox.class.isAssignableFrom(property.getType())) {
			return property.getConfiguration().getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE).map(ps -> {
				return resolvePropertyBox(parent, ps, resolver, includeReadOnly);
			}).orElseGet(() -> buildUnresolvedPropertyBoxSchema());
		}
		// check PropertyBox collection property
		if (isPropertyBoxCollectionProperty(property)) {
			final ArraySchema arrayModel = new ArraySchema();
			arrayModel.setItems(
					property.getConfiguration().getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE).map(ps -> {
						return resolvePropertyBox(parent, ps, resolver, includeReadOnly);
					}).orElseGet(() -> buildUnresolvedPropertyBoxSchema()));
			if (Set.class.isAssignableFrom(property.getType())) {
				arrayModel.setUniqueItems(true);
			}
			return arrayModel;
		}
		// default resolution
		final boolean collection = CollectionProperty.class.isAssignableFrom(property.getClass());
		// property type
		Class<?> type = collection ? ((CollectionProperty<?, ?>) property).getElementType() : property.getType();
		// resolve
		Schema<?> schema = resolver.apply(new AnnotatedType().type(type).schemaProperty(true).propertyName(name)
				.skipOverride(true).skipSchemaName(true));
		if (schema != null) {
			configureProperty(parent, schema, property, name);
			if (collection) {
				final ArraySchema array = new ArraySchema();
				array.setItems(schema);
				return array;
			}
			return schema;
		}
		LOGGER.warn("Failed to resolve schema property [" + property + "] named [" + name + "]");
		return null;
	}

	/**
	 * Configure the schema of a resolved {@link Property}.
	 * @param parent Parent schema
	 * @param schema Property schema
	 * @param property The property
	 * @param name Property name
	 */
	private static void configureProperty(Schema<?> parent, Schema<?> schema, Property<?> property, String name) {
		// check local time
		if (LocalTime.class.isAssignableFrom(property.getType())) {
			schema.setType("string");
			schema.setFormat("time");
			schema.setProperties(null);
		}
		// check date type
		if (TypeUtils.isDate(property.getType())) {
			property.getTemporalType().ifPresent(tt -> {
				switch (tt) {
				case DATE:
					schema.setFormat("date");
					break;
				case DATE_TIME:
					schema.setFormat("date-time");
					break;
				case TIME:
					schema.setFormat("time");
					break;
				default:
					break;
				}
			});
		}
		// read only
		if (property.isReadOnly()) {
			schema.setReadOnly(Boolean.TRUE);
		}
		// title
		String message = LocalizationContext.translate(property, true);
		if (message != null) {
			schema.setTitle(message);
		}
		// validators
		property.getValidators().forEach(v -> {
			if (v instanceof BuiltinValidator) {
				((BuiltinValidator<?>) v).getDescriptor().ifPresent(d -> {
					configurePropertyValidation(parent, schema, property, name, d);
				});
			}
		});
	}

	/**
	 * Configure property schema validation.
	 * @param parent Parent schema
	 * @param schema Property schema
	 * @param property The property
	 * @param name Property name
	 * @param validatorDescriptor The validator descriptor
	 */
	private static void configurePropertyValidation(Schema<?> parent, Schema<?> schema, Property<?> property,
			String name, ValidatorDescriptor validatorDescriptor) {
		// required
		if (validatorDescriptor.isRequired() && parent != null) {
			parent.addRequiredItem(name);
		}
		// min
		if (validatorDescriptor.getMin() != null) {
			if (TypeUtils.isNumber(property.getType())) {
				schema.setMinimum(new BigDecimal(validatorDescriptor.getMin().doubleValue()));
				schema.setExclusiveMinimum(validatorDescriptor.isExclusiveMin());
			} else if (TypeUtils.isString(property.getType())) {
				schema.setMinLength(validatorDescriptor.isExclusiveMin() ? validatorDescriptor.getMin().intValue() + 1
						: validatorDescriptor.getMin().intValue());
			} else if (CollectionProperty.class.isAssignableFrom(property.getClass())) {
				schema.setMinItems(validatorDescriptor.isExclusiveMin() ? validatorDescriptor.getMin().intValue() + 1
						: validatorDescriptor.getMin().intValue());
			}
		}
		// max
		if (validatorDescriptor.getMax() != null) {
			if (TypeUtils.isNumber(property.getType())) {
				schema.setMaximum(new BigDecimal(validatorDescriptor.getMax().doubleValue()));
				schema.setExclusiveMaximum(validatorDescriptor.isExclusiveMax());
			} else if (TypeUtils.isString(property.getType())) {
				schema.setMaxLength(validatorDescriptor.isExclusiveMax() ? validatorDescriptor.getMax().intValue() - 1
						: validatorDescriptor.getMax().intValue());
			} else if (CollectionProperty.class.isAssignableFrom(property.getClass())) {
				schema.setMaxItems(validatorDescriptor.isExclusiveMax() ? validatorDescriptor.getMax().intValue() - 1
						: validatorDescriptor.getMax().intValue());
			}
		}
		// pattern
		if (validatorDescriptor.getPattern() != null) {
			schema.setPattern(validatorDescriptor.getPattern());
		}
		// email
		if (validatorDescriptor.isEmail()) {
			schema.setFormat("email");
		}
	}

	/**
	 * Checks whether given property is a {@link CollectionProperty} of {@link PropertyBox} type.
	 * @param property The property to check
	 * @return whether given property is a {@link CollectionProperty} of {@link PropertyBox} type
	 */
	private static boolean isPropertyBoxCollectionProperty(Property<?> property) {
		return (CollectionProperty.class.isAssignableFrom(property.getClass())
				&& PropertyBox.class.isAssignableFrom(((CollectionProperty<?, ?>) property).getElementType()));
	}

	/**
	 * Create a default Schema for a failed PropertyBox type resolution.
	 * @return The default unresolved schema
	 */
	private static Schema<Object> buildUnresolvedPropertyBoxSchema() {
		final Schema<Object> model = new Schema<>();
		model.setType("object");
		model.setTitle("PropertyBox");
		return model;
	}

}
