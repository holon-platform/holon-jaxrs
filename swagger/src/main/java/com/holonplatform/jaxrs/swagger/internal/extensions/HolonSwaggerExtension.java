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

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.holonplatform.core.Path;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.jaxrs.swagger.annotations.ApiPropertySet;
import com.holonplatform.jaxrs.swagger.annotations.HolonSwaggerExtensions;
import com.holonplatform.jaxrs.swagger.internal.ApiPropertySetIntrospector;
import com.holonplatform.jaxrs.swagger.internal.SwaggerPropertyFactory;

import io.swagger.jaxrs.ext.AbstractSwaggerExtension;
import io.swagger.jaxrs.ext.SwaggerExtension;
import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;

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
public class HolonSwaggerExtension extends AbstractSwaggerExtension {

	/*
	 * (non-Javadoc)
	 * @see io.swagger.jaxrs.ext.AbstractSwaggerExtension#decorateOperation(io.swagger.models.Operation,
	 * java.lang.reflect.Method, java.util.Iterator)
	 */
	@Override
	public void decorateOperation(Operation operation, Method method, Iterator<SwaggerExtension> chain) {
		super.decorateOperation(operation, method, chain);

		// response property set
		final Map<String, Response> responses = operation.getResponses();
		if (responses != null) {
			getResponsePropertySet(method).ifPresent(propertySet -> {
				for (Response response : responses.values()) {
					ArrayProperty ap = isPropertyBoxArrayPropertyType(response.getSchema());
					if (ap != null) {
						ap.items(buildPropertyBoxProperty(propertySet, true));
					} else if (isPropertyBoxPropertyType(response.getSchema())) {
						response.schema(buildPropertyBoxProperty(propertySet, true));
					}
				}
			});
		}

	}

	private static Property buildPropertyBoxProperty(PropertySet<?> propertySet, boolean includeReadOnly) {

		final SwaggerPropertyFactory factory = SwaggerPropertyFactory.getDefault();

		final ObjectProperty property = new ObjectProperty();
		property.title("PropertyBox");
		// property.description("Holon Platform PropertyBox data container");
		property.getVendorExtensions().put(HolonSwaggerExtensions.MODEL_TYPE.getExtensionName(),
				PropertyBox.class.getName());

		if (propertySet != null) {
			// to respect insertion order
			property.properties(new LinkedHashMap<>());

			propertySet.forEach(p -> {
				if (includeReadOnly || !p.isReadOnly()) {
					if (Path.class.isAssignableFrom(p.getClass())) {
						Property sp = factory.create(p);
						if (sp != null) {
							property.property(((Path<?>) p).relativeName(), sp);
						}
					}
				}
			});
		}

		return property;
	}

	/**
	 * Check whether the given <code>method</code> return type is annotated with the {@link ApiPropertySet} annotation
	 * and extract the corresonding {@link PropertySet}.
	 * @param method Method to inspect
	 * @return Optional {@link PropertySet} obtained form the {@link ApiPropertySet} annotation, if available
	 */
	private static Optional<PropertySet<?>> getResponsePropertySet(Method method) {
		final AnnotatedType rt = method.getAnnotatedReturnType();
		if (rt != null && rt.isAnnotationPresent(ApiPropertySet.class)) {
			return Optional.ofNullable(
					ApiPropertySetIntrospector.get().getPropertySet(rt.getAnnotation(ApiPropertySet.class)));
		}
		// check arrays
		if (method.getReturnType() != null && method.getReturnType().isArray()) {

		}
		return Optional.empty();
	}

	/**
	 * Check whether the given property is of {@link PropertyBox} type using the
	 * {@link HolonSwaggerExtensions#MODEL_TYPE} extension name.
	 * @param property Property to check
	 * @return <code>true</code> if given property is of {@link PropertyBox} type
	 */
	private static boolean isPropertyBoxPropertyType(Property property) {
		if (property != null && property.getVendorExtensions() != null
				&& property.getVendorExtensions().containsKey(HolonSwaggerExtensions.MODEL_TYPE.getExtensionName())
				&& PropertyBox.class.getName().equals(
						property.getVendorExtensions().get(HolonSwaggerExtensions.MODEL_TYPE.getExtensionName()))) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the given property is and {@link ArrayProperty} of {@link PropertyBox} type using the
	 * {@link HolonSwaggerExtensions#MODEL_TYPE} extension name.
	 * @param property Property to check
	 * @return if the property is of {@link PropertyBox} type, return such property casted to {@link ArrayProperty},
	 *         <code>null</code> otherwise
	 */
	private static ArrayProperty isPropertyBoxArrayPropertyType(Property property) {
		if (property != null && ArrayProperty.class.isAssignableFrom(property.getClass())) {
			if (isPropertyBoxPropertyType(property)) {
				return (ArrayProperty) property;
			}
			final Property items = ((ArrayProperty) property).getItems();
			if (isPropertyBoxPropertyType(items)) {
				return (ArrayProperty) property;
			}
		}
		return null;
	}

}
