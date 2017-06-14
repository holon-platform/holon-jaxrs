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
import java.util.Map;

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
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;

/**
 * TODO
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

		AnnotatedType rt = method.getAnnotatedReturnType();
		if (rt != null) {
			System.err.println("Method " + method.getName() + ">>> has ApiPropertySet annotation ["
					+ rt.isAnnotationPresent(ApiPropertySet.class) + "]");
		}

		// response property set
		ApiPropertySet apiPropertySet = null;
		if (method.getAnnotatedReturnType() != null) {
			apiPropertySet = method.getAnnotatedReturnType().getAnnotation(ApiPropertySet.class);
		}

		// responses
		Map<String, Response> responses = operation.getResponses();
		if (responses != null) {
			for (Response response : responses.values()) {
				Property p = response.getSchema();
				if (p != null && p.getVendorExtensions().containsKey("x-holon-type")
						&& PropertyBox.class.getName().equals(p.getVendorExtensions().get("x-holon-type"))
						&& apiPropertySet != null) {
					operation.getVendorExtensions().put("x-holon-return-propertyset", apiPropertySet);
				}
			}
		}

		// temp
		if (apiPropertySet != null) {
			final PropertySet<?> responsePropertySet = ApiPropertySetIntrospector.get().getPropertySet(apiPropertySet);
			operation.getResponses().get("200").schema(buildPropertyBoxProperty(responsePropertySet, true));
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

}
