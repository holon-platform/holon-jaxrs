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

import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;

/**
 * A Swagger {@link ModelConverter} to handle {@link PropertyBox} type model objects.
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

		if (PropertyBox.class.equals(type)) {
			System.err.println("### resolveProperty [" + type + "] is a PropertyBox type");
			System.err.println("---> Annotations [" + annotations + "]");
		}

		if (chain.hasNext()) {
			return chain.next().resolveProperty(type, context, annotations, chain);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.converter.ModelConverter#resolve(java.lang.reflect.Type,
	 * io.swagger.converter.ModelConverterContext, java.util.Iterator)
	 */
	@Override
	public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> chain) {
		
		if (PropertyBox.class.equals(type)) {
			System.err.println(">>> resolve [" + type + "] is a PropertyBox type");
			Model model = context.resolve(type);
			System.err.println(">>> Model [" + model + "]");
		}
		
		if (chain.hasNext()) {
			return chain.next().resolve(type, context, chain);
		} else {
			return null;
		}
	}

}
