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
import java.lang.reflect.Type;
import java.util.Iterator;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.jaxrs.swagger.SwaggerExtensions;
import com.holonplatform.jaxrs.swagger.internal.PropertyBoxTypeInfo;

import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.models.Model;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;

/**
 * A Swagger {@link ModelConverter} to handle {@link PropertyBox} type model objects and setting the
 * {@link SwaggerExtensions#MODEL_TYPE} extension property value.
 * <p>
 * This converter is automatically loaded and registered in Swagger using Java service extensions.
 * </p>
 *
 * @since 5.0.0
 */
public class PropertyBoxModelConverter implements ModelConverter {

	/*
	 * (non-Javadoc)
	 * @see io.swagger.converter.ModelConverter#resolveProperty(java.lang.reflect.Type,
	 * io.swagger.converter.ModelConverterContext, java.lang.annotation.Annotation[], java.util.Iterator)
	 */
	@Override
	public Property resolveProperty(Type type, ModelConverterContext context, Annotation[] annotations,
			Iterator<ModelConverter> chain) {

		final PropertyBoxTypeInfo pbType = PropertyBoxTypeInfo.check(type).orElse(null);

		// PropertyBox type
		if (pbType != null) {
			// container
			if (pbType.isContainerType()) {
				if (pbType.isMap()) {
					MapProperty property = new MapProperty();
					property.additionalProperties(context.resolveProperty(PropertyBox.class, new Annotation[] {}));
					return property;
				} else {
					ArrayProperty property = new ArrayProperty();
					property.items(context.resolveProperty(PropertyBox.class, new Annotation[] {}));
					property.setUniqueItems(pbType.isUniqueItems());
					return property;
				}
			}
			// simple
			ObjectProperty property = new ObjectProperty();
			property.title("PropertyBox");
			property.getVendorExtensions().put(SwaggerExtensions.MODEL_TYPE.getExtensionName(),
					PropertyBox.class.getName());
			return property;
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

		// skip PropertyBox types
		if (PropertyBoxTypeInfo.check(type).isPresent()) {
			return null;
		}

		if (chain.hasNext()) {
			return chain.next().resolve(type, context, chain);
		}
		return null;
	}

}
