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

import com.fasterxml.jackson.databind.JavaType;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.jaxrs.swagger.annotations.HolonSwaggerExtensions;

import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;

/**
 * A Swagger {@link ModelConverter} to handle {@link PropertyBox} type model objects and setting the
 * {@link HolonSwaggerExtensions#MODEL_TYPE} extension property value.
 * <p>
 * This converter is automatically loaded and registered in Swagger using Java service extensions.
 * </p>
 *
 * @since 5.0.0
 */
public class PropertyBoxModelConverter implements ModelConverter {

	/**
	 * Whether QueryDSL is available from classpath of current ClassLoader
	 */
	public static final boolean JACKSON_DATABIND_PRESENT = ClassUtils
			.isPresent("com.fasterxml.jackson.databind.JavaType", ClassUtils.getDefaultClassLoader());

	/*
	 * (non-Javadoc)
	 * @see io.swagger.converter.ModelConverter#resolveProperty(java.lang.reflect.Type,
	 * io.swagger.converter.ModelConverterContext, java.lang.annotation.Annotation[], java.util.Iterator)
	 */
	@Override
	public Property resolveProperty(Type type, ModelConverterContext context, Annotation[] annotations,
			Iterator<ModelConverter> chain) {

		Property property = null;
		if (chain.hasNext()) {
			property = chain.next().resolveProperty(type, context, annotations, chain);
		}

		if (property != null) {
			if (isPropertyBoxType(type) || PropertyBox[].class == type) {
				property.getVendorExtensions().put(HolonSwaggerExtensions.MODEL_TYPE.getExtensionName(),
						PropertyBox.class.getName());
			}
		}

		return property;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.converter.ModelConverter#resolve(java.lang.reflect.Type,
	 * io.swagger.converter.ModelConverterContext, java.util.Iterator)
	 */
	@Override
	public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> chain) {

		boolean propertyBox = false;
		if (isPropertyBoxType(type)) {
			propertyBox = true;
		}

		Model model = null;
		if (chain.hasNext()) {
			model = chain.next().resolve(type, context, chain);
		}

		if (model != null) {
			if (propertyBox) {
				model.getVendorExtensions().put(HolonSwaggerExtensions.MODEL_TYPE.getExtensionName(),
						PropertyBox.class.getName());
				if (model.getProperties() != null) {
					model.getProperties().remove("invalidAllowed");
				}
			}
		}

		return model;
	}

	private static boolean isPropertyBoxType(Type type) {
		if (type != null) {
			if (PropertyBox.class.equals(type)) {
				return true;
			}
			if (JACKSON_DATABIND_PRESENT && type instanceof JavaType) {
				return ((JavaType) type).isTypeOrSubTypeOf(PropertyBox.class);
			}
		}
		return false;
	}

}
