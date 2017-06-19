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
package com.holonplatform.jaxrs.swagger.internal.extensions;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.jaxrs.swagger.SwaggerExtensions;
import com.holonplatform.jaxrs.swagger.annotations.ApiPropertySet;
import com.holonplatform.jaxrs.swagger.internal.ApiPropertySetIntrospector;
import com.holonplatform.jaxrs.swagger.internal.PropertyBoxTypeInfo;
import com.holonplatform.jaxrs.swagger.internal.SwaggerContext;
import com.holonplatform.jaxrs.swagger.internal.SwaggerPropertyFactory;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationException;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.ext.AbstractSwaggerExtension;
import io.swagger.jaxrs.ext.SwaggerExtension;
import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Operation;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.util.ParameterProcessor;

/**
 * A {@link SwaggerExtension} to handle {@link PropertyBox} type model properties in operations parameters and response
 * types.
 * <p>
 * The {@link ApiPropertySet} annotation can be used to declare the {@link PropertySet} of each operation
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
public class PropertyBoxSwaggerExtension extends AbstractSwaggerExtension {

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
				PropertySet<?> propertySet = ApiPropertySetIntrospector.get().getPropertySet(aps);
				if (propertySet != null) {
					final Property propertyBoxProperty = buildPropertyBoxProperty(propertySet, true,
							AnnotationUtils.getStringValue(aps.model()), SwaggerContext.getSwagger());

					// responses
					for (Response response : responses.values()) {
						ArrayProperty ap = isPropertyBoxArrayPropertyType(response.getSchema());
						if (ap != null) {
							ap.items(propertyBoxProperty);
						} else if (isPropertyBoxPropertyType(response.getSchema())) {
							response.schema(propertyBoxProperty);
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
			final PropertyBoxTypeInfo pbType = PropertyBoxTypeInfo.check(type).orElse(null);
			if (pbType != null) {
				// check property set
				final ApiPropertySet aps = hasApiPropertySet(annotations);
				if (aps != null) {
					PropertySet<?> propertySet = ApiPropertySetIntrospector.get().getPropertySet(aps);
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

						final Model model = buildPropertyBoxModel(propertySet, false,
								(pbType.isContainerType() && !pbType.isMap()), aps.model(),
								SwaggerContext.getSwagger());
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
	 * Check whether the {@link ApiPropertySet} annotation is present in given annotations list.
	 * @param annotations Annotations to scan
	 * @return If the {@link ApiPropertySet} annotation is present in given annotations list, returns it
	 */
	private static ApiPropertySet hasApiPropertySet(List<Annotation> annotations) {
		List<ApiPropertySet> as = AnnotationUtils.getAnnotations(annotations, ApiPropertySet.class);
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
	 * Check whether the given <code>method</code> return type is annotated with the {@link ApiPropertySet} annotation.
	 * @param method Method to inspect
	 * @return Optional {@link ApiPropertySet} annotation, if available
	 */
	private static Optional<ApiPropertySet> getResponsePropertySet(Method method) {
		final AnnotatedType rt = method.getAnnotatedReturnType();
		if (rt != null) {
			if (rt.isAnnotationPresent(ApiPropertySet.class)) {
				return Optional.of(rt.getAnnotation(ApiPropertySet.class));
			}
			// check meta-annotations
			List<ApiPropertySet> annotations = AnnotationUtils.getAnnotations(rt, ApiPropertySet.class);
			if (!annotations.isEmpty()) {
				return Optional.ofNullable(annotations.get(0));
			}
		}
		return Optional.empty();
	}

	/**
	 * Get a Swagger name-property map form given <code>propertySet</code>.
	 * @param propertySet Property set
	 * @param includeReadOnly Whether to include {@link PropertySet} read-only properties
	 * @return Swagger name-property map
	 */
	private static Map<String, Property> getPropertySetProperties(PropertySet<?> propertySet, boolean includeReadOnly) {

		final SwaggerPropertyFactory factory = SwaggerPropertyFactory.getDefault();

		Map<String, Property> properties = new LinkedHashMap<>();

		if (propertySet != null) {
			propertySet.forEach(p -> {
				if (includeReadOnly || !p.isReadOnly()) {
					if (Path.class.isAssignableFrom(p.getClass())) {
						Property sp = factory.create(p);
						if (sp != null) {
							properties.put(((Path<?>) p).relativeName(), sp);
						}
					}
				}
			});
		}

		return properties;
	}

	/**
	 * Build a {@link PropertyBox} type Swagger property using given <code>propertySet</code>.
	 * @param propertySet Property set
	 * @param includeReadOnly Whether to include {@link PropertySet} read-only properties
	 * @param modelName If not null, define a Model with given name and use a {@link RefProperty} to reference it
	 * @param swagger Current {@link Swagger} instance, required to define a Model by name
	 * @return Swagger property
	 */
	private static Property buildPropertyBoxProperty(PropertySet<?> propertySet, boolean includeReadOnly,
			String modelName, Swagger swagger) {
		if (modelName != null && !modelName.trim().equals("")) {
			// define model and use a ref
			return new RefProperty(defineModel(swagger, propertySet, modelName));
		}
		// explicit object definition
		ObjectProperty property = new ObjectProperty();
		property.title("PropertyBox");
		property.getVendorExtensions().put(SwaggerExtensions.MODEL_TYPE.getExtensionName(),
				PropertyBox.class.getName());
		property.properties(getPropertySetProperties(propertySet, includeReadOnly));
		return property;
	}

	/**
	 * Build a {@link PropertyBox} type Swagger model using given <code>propertySet</code>.
	 * @param propertySet Property set
	 * @param includeReadOnly Whether to include {@link PropertySet} read-only properties
	 * @param array <code>true</code> to create an array model type
	 * @param modelName If not null, define a Model with given name and use a {@link RefModel} to reference it
	 * @param swagger Current {@link Swagger} instance, required to define a Model by name
	 * @return Swagger model
	 */
	private static Model buildPropertyBoxModel(PropertySet<?> propertySet, boolean includeReadOnly, boolean array,
			String modelName, Swagger swagger) {

		if (array) {
			// Array model
			ArrayModel model = new ArrayModel();
			model.items(buildPropertyBoxProperty(propertySet, includeReadOnly, modelName, swagger));
			return model;
		}

		// Check ref model
		if (modelName != null && !modelName.trim().equals("")) {
			return new RefModel(defineModel(swagger, propertySet, modelName));
		}

		// Simple model
		ModelImpl model = new ModelImpl();
		model.type(ModelImpl.OBJECT);
		model.name("PropertyBox");
		model.setProperties(getPropertySetProperties(propertySet, includeReadOnly));
		model.getVendorExtensions().put(SwaggerExtensions.MODEL_TYPE.getExtensionName(), PropertyBox.class.getName());
		return model;
	}

	/**
	 * Define a name {@link PropertySet} Model in given {@link Swagger} instance.
	 * @param swagger Swagger instance (not null)
	 * @param propertySet Property set
	 * @param modelName Model name (not null)
	 * @return Defined model name
	 */
	private static String defineModel(Swagger swagger, PropertySet<?> propertySet, String modelName) {
		if (modelName != null) {
			if (swagger == null) {
				throw new SwaggerConfigurationException(
						"Cannot define Model with name [" + modelName + "]: missing context Swagger instance");
			}
			ModelImpl model = new ModelImpl();
			model.type(ModelImpl.OBJECT);
			model.name(modelName);
			model.setProperties(getPropertySetProperties(propertySet, true));
			model.getVendorExtensions().put(SwaggerExtensions.MODEL_TYPE.getExtensionName(),
					PropertyBox.class.getName());
			// define
			swagger.addDefinition(modelName, model);
			return modelName;
		}
		return null;
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
	 * Check if the given property is and {@link ArrayProperty} of {@link PropertyBox} type using the
	 * {@link SwaggerExtensions#MODEL_TYPE} extension name.
	 * @param property Property to check
	 * @return if the property is of {@link PropertyBox} type, return such property casted to {@link ArrayProperty},
	 *         <code>null</code> otherwise
	 */
	private static ArrayProperty isPropertyBoxArrayPropertyType(Property property) {
		if (property != null && ArrayProperty.class.isAssignableFrom(property.getClass())) {
			final Property items = ((ArrayProperty) property).getItems();
			if (isPropertyBoxPropertyType(items)) {
				return (ArrayProperty) property;
			}
		}
		return null;
	}

}
