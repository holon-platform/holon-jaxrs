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
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implievalidatorDescriptor. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.jaxrs.swagger.v2.internal.extensions;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalTime;
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
import com.holonplatform.jaxrs.swagger.annotations.ApiPropertySetModel;
import com.holonplatform.jaxrs.swagger.internal.SwaggerExtensions;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.internal.resolver.SwaggerPropertySetSerializationTreeResolver;
import com.holonplatform.jaxrs.swagger.internal.types.PropertyBoxTypeInfo;
import com.holonplatform.jaxrs.swagger.internal.types.PropertyBoxTypeResolver;
import com.holonplatform.json.model.PropertySetSerializationNode;
import com.holonplatform.json.model.PropertySetSerializationTree;

import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.RefModel;
import io.swagger.models.properties.AbstractNumericProperty;
import io.swagger.models.properties.AbstractProperty;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.StringProperty;
import io.swagger.models.utils.PropertyModelConverter;

/**
 * A Swagger {@link ModelConverter} to handle {@link PropertyBox} type model objects and setting the
 * {@link SwaggerExtensions#MODEL_TYPE} extension property value.
 * <p>
 * This converter is automatically loaded and registered in Swagger using Java service extensions.
 * </p>
 *
 * @since 5.0.0
 */
public class SwaggerV2PropertyBoxModelConverter implements ModelConverter {

	private static final Logger LOGGER = SwaggerLogger.create();

	/*
	 * (non-Javadoc)
	 * @see io.swagger.converter.ModelConverter#resolveProperty(java.lang.reflect.Type,
	 * io.swagger.converter.ModelConverterContext, java.lang.annotation.Annotation[], java.util.Iterator)
	 */
	@Override
	public io.swagger.models.properties.Property resolveProperty(Type type, ModelConverterContext context,
			Annotation[] annotations, Iterator<ModelConverter> chain) {
		// check LocalTime type
		if (LocalTime.class == type) {
			StringProperty lt = new StringProperty();
			lt.setFormat("time");
			return lt;
		}
		// check PropertyBox type
		final PropertyBoxTypeInfo pbType = PropertyBoxTypeResolver.resolvePropertyBoxType(type).orElse(null);
		if (pbType != null) {
			// container
			if (pbType.isContainerType()) {
				if (pbType.isMap()) {
					MapProperty property = new MapProperty();
					property.additionalProperties(context.resolveProperty(PropertyBox.class, annotations));
					return property;
				} else {
					ArrayProperty property = new ArrayProperty();
					property.items(context.resolveProperty(PropertyBox.class, annotations));
					property.setUniqueItems(pbType.isUniqueItems());
					return property;
				}
			}
			// simple
			return modelToProperty(
					getPropertyBoxSchema(type, context).orElseGet(() -> delegateToExtensionResolution()));
		}
		// Default behaviour
		if (chain.hasNext()) {
			return chain.next().resolveProperty(type, context, annotations, chain);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.converter.ModelConverter#resolve(java.lang.reflect.Type,
	 * io.swagger.converter.ModelConverterContext, java.util.Iterator)
	 */
	@Override
	public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> chain) {
		final PropertyBoxTypeInfo pbType = PropertyBoxTypeResolver.resolvePropertyBoxType(type).orElse(null);
		// PropertyBox type
		if (pbType != null) {
			if (pbType.isContainerType()) {
				// container
				// Array
				ArrayModel arrayModel = new ArrayModel();
				arrayModel.setItems(modelToProperty(
						getPropertyBoxSchema(type, context).orElseGet(() -> delegateToExtensionResolution())));
				if (pbType.isUniqueItems()) {
					arrayModel.setUniqueItems(true);
				}
				return arrayModel;
			}
			// Single
			return getPropertyBoxSchema(type, context).orElseGet(() -> delegateToExtensionResolution());
		}

		// Default behaviour
		if (chain.hasNext()) {
			return chain.next().resolve(type, context, chain);
		}
		return null;
	}

	/**
	 * Get the {@link PropertyBox} type Schema from given type, if a {@link PropertySet} is available using a
	 * {@link PropertySetRef} annotation.
	 * @param type The type to resolve
	 * @param context Converter context
	 * @return Optional {@link PropertyBox} type Schema
	 */
	private static Optional<Model> getPropertyBoxSchema(Type type, ModelConverterContext context) {
		return getPropertySet(type).map(ref -> PropertySetRefIntrospector.get().getPropertySet(ref))
				.map(propertySet -> {
					final Function<Type, io.swagger.models.properties.Property> resolver = t -> context
							.resolveProperty(t, new Annotation[0]);
					// check API model
					return getPropertySetModel(type).map(apiModel -> {
						final String name = apiModel.value().trim();
						// define model
						Model schema = buildPropertyBoxSchema(propertySet, resolver, false);
						schema.setTitle(name);
						if (AnnotationUtils.getStringValue(apiModel.description()) != null) {
							schema.setDescription(apiModel.description());
						}
						context.defineModel(name, schema);
						// return as ref
						RefModel model = new RefModel(name);
						return (Model) model;
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
	private static Optional<PropertySetRef> getPropertySet(Type type) {
		if (type instanceof AnnotatedElement) {
			// check meta-annotations
			List<PropertySetRef> annotations = AnnotationUtils.getAnnotations((AnnotatedElement) type,
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
	private static Optional<ApiPropertySetModel> getPropertySetModel(Type type) {
		if (type instanceof AnnotatedElement) {
			// check meta-annotations
			List<ApiPropertySetModel> annotations = AnnotationUtils.getAnnotations((AnnotatedElement) type,
					ApiPropertySetModel.class);
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
	 * Get the model actual resolution to the {@link SwaggerV2ApiExtension}.
	 * @return Delgate resolution model
	 */
	private static Model delegateToExtensionResolution() {
		final ModelImpl model = new ModelImpl();
		model.setType(ModelImpl.OBJECT);
		model.setTitle("PropertyBox");
		model.setVendorExtension(SwaggerExtensions.MODEL_TYPE.getExtensionName(), PropertyBox.class.getName());
		return model;
	}

	/**
	 * Build a {@link Model} for given {@link PropertySet}.
	 * @param propertySet The property set
	 * @param resolver The type resolver
	 * @param includeReadOnly Whether to include read-only properties in schema
	 * @return The Model
	 */
	public static Model buildPropertyBoxSchema(PropertySet<?> propertySet,
			Function<Type, io.swagger.models.properties.Property> resolver, boolean includeReadOnly) {
		return resolvePropertyBox(null, propertySet, resolver, includeReadOnly);
	}

	/**
	 * Resolve a {@link Model} for given {@link PropertySet}.
	 * @param parent Optional parent Model
	 * @param propertySet The property set
	 * @param resolver The type resolver
	 * @param includeReadOnly Whether to include read-only properties in schema
	 * @return The resolved Model
	 */
	private static Model resolvePropertyBox(Model parent, PropertySet<?> propertySet,
			Function<Type, io.swagger.models.properties.Property> resolver, boolean includeReadOnly) {
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
	private static Model resolvePropertyBoxNodes(Model parent, Iterable<PropertySetSerializationNode> nodes,
			Function<Type, io.swagger.models.properties.Property> resolver, boolean includeReadOnly) {
		final ModelImpl model = new ModelImpl();
		model.setType(ModelImpl.OBJECT);
		if (parent == null) {
			model.setTitle("PropertyBox");
		}
		// properties
		final Map<String, io.swagger.models.properties.Property> properties = new LinkedHashMap<>();
		for (PropertySetSerializationNode node : nodes) {
			io.swagger.models.properties.Property modelProperty = null;
			Optional<Property<?>> property = isValidNodeProperty(node);
			if (property.isPresent()) {
				modelProperty = resolveSchemaProperty(model, property.get(), node.getName(), resolver, includeReadOnly);
			} else {
				modelProperty = modelToProperty(
						resolvePropertyBoxNodes(model, node.getChildren(), resolver, includeReadOnly));
			}
			if (modelProperty != null) {
				properties.put(node.getName(), modelProperty);
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
	 * Resolve a single {@link Property}.
	 * @param parent Parent Model
	 * @param property The property to resolve
	 * @param name The property name
	 * @param resolver The type resolver
	 * @param includeReadOnly Whether to include read-only properties in schema
	 * @return The resolved property, or <code>null</code>
	 */
	private static io.swagger.models.properties.Property resolveSchemaProperty(Model parent, Property<?> property,
			String name, Function<Type, io.swagger.models.properties.Property> resolver, boolean includeReadOnly) {
		// check nested PropertyBox
		if (PropertyBox.class.isAssignableFrom(property.getType())) {
			return modelToProperty(
					property.getConfiguration().getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE).map(ps -> {
						return resolvePropertyBox(parent, ps, resolver, includeReadOnly);
					}).orElseGet(() -> buildUnresolvedPropertyBoxSchema()));
		}
		// check PropertyBox collection property
		if (isPropertyBoxCollectionProperty(property)) {
			final ArrayProperty arrayModel = new ArrayProperty();
			arrayModel.setItems(modelToProperty(
					property.getConfiguration().getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE).map(ps -> {
						return resolvePropertyBox(parent, ps, resolver, includeReadOnly);
					}).orElseGet(() -> buildUnresolvedPropertyBoxSchema())));
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
		io.swagger.models.properties.Property schema = resolver.apply(type);
		if (schema != null) {
			configureProperty(parent, schema, property, name);
			if (collection) {
				final ArrayProperty array = new ArrayProperty();
				array.setItems(schema);
				return array;
			}
			return schema;
		}
		LOGGER.warn("Failed to resolve schema property [" + property + "] named [" + name + "]");
		return null;
	}

	/**
	 * Configure a resolved {@link Property}.
	 * @param parent Parent Model
	 * @param schema Property schema
	 * @param property The property
	 * @param name Property name
	 */
	private static void configureProperty(Model parent, io.swagger.models.properties.Property schema,
			Property<?> property, String name) {
		// check date type
		if (TypeUtils.isDate(property.getType()) && schema instanceof AbstractProperty) {
			property.getTemporalType().ifPresent(tt -> {
				switch (tt) {
				case DATE:
					((AbstractProperty) schema).setFormat("date");
					break;
				case DATE_TIME:
					((AbstractProperty) schema).setFormat("date-time");
					break;
				case TIME:
					((AbstractProperty) schema).setFormat("time");
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
	 * Configure property validation.
	 * @param parent Parent Model
	 * @param schema Property schema
	 * @param property The property
	 * @param name Property name
	 * @param validatorDescriptor The validator descriptor
	 */
	private static void configurePropertyValidation(Model parent, io.swagger.models.properties.Property schema,
			Property<?> property, String name, ValidatorDescriptor validatorDescriptor) {
		// required
		if (validatorDescriptor.isRequired()) {
			schema.setRequired(true);
		}
		// min
		// min
		if (validatorDescriptor.getMin() != null) {
			if (schema instanceof AbstractNumericProperty) {
				(((AbstractNumericProperty) schema))
						.setMinimum(new BigDecimal(validatorDescriptor.getMin().doubleValue()));
				(((AbstractNumericProperty) schema)).setExclusiveMinimum(validatorDescriptor.isExclusiveMin());
			} else if (schema instanceof StringProperty) {
				((StringProperty) schema)
						.minLength(validatorDescriptor.isExclusiveMin() ? validatorDescriptor.getMin().intValue() + 1
								: validatorDescriptor.getMin().intValue());
			} else if (schema instanceof ArrayProperty) {
				((ArrayProperty) schema)
						.setMinItems(validatorDescriptor.isExclusiveMin() ? validatorDescriptor.getMin().intValue() + 1
								: validatorDescriptor.getMin().intValue());
			}
		}
		// max
		if (validatorDescriptor.getMax() != null) {
			if (schema instanceof AbstractNumericProperty) {
				(((AbstractNumericProperty) schema))
						.setMaximum(new BigDecimal(validatorDescriptor.getMax().doubleValue()));
				(((AbstractNumericProperty) schema)).setExclusiveMaximum(validatorDescriptor.isExclusiveMax());
			} else if (schema instanceof StringProperty) {
				((StringProperty) property)
						.maxLength(validatorDescriptor.isExclusiveMax() ? validatorDescriptor.getMax().intValue() - 1
								: validatorDescriptor.getMax().intValue());
			} else if (schema instanceof ArrayProperty) {
				((ArrayProperty) property)
						.setMaxItems(validatorDescriptor.isExclusiveMax() ? validatorDescriptor.getMax().intValue() - 1
								: validatorDescriptor.getMax().intValue());
			}
		}
		// pattern
		if (validatorDescriptor.getPattern() != null) {
			if (schema instanceof StringProperty) {
				((StringProperty) schema).setPattern(validatorDescriptor.getPattern());
			}
		}
		// email
		if (validatorDescriptor.isEmail()) {
			if (schema instanceof StringProperty) {
				((StringProperty) schema).setFormat("email");
			}
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
	 * Create a default Model for a failed PropertyBox type resolution.
	 * @return The default unresolved schema
	 */
	private static Model buildUnresolvedPropertyBoxSchema() {
		final ModelImpl model = new ModelImpl();
		model.setType(ModelImpl.OBJECT);
		model.setTitle("PropertyBox");
		return model;
	}

	public static io.swagger.models.properties.Property modelToProperty(Model model) {
		return new PropertyModelConverter().modelToProperty(model);
	}

}
