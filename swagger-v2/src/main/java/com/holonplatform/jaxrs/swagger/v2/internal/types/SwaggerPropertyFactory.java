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
package com.holonplatform.jaxrs.swagger.v2.internal.types;

import java.io.Serializable;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyValueConverter.PropertyConversionException;

import io.swagger.models.Swagger;

/**
 * Swagger {@link io.swagger.models.properties.Property} factory using Holon {@link Property}.
 * 
 * @since 5.0.0
 */
public interface SwaggerPropertyFactory extends Serializable {

	/**
	 * Create a Swagger model property using given <code>property</code>.
	 * @param swagger Current Swagger instance, or <code>null</code> if not available
	 * @param property Property to convert (not null)
	 * @return Swagger property
	 * @throws PropertyConversionException IF a property conversion error occurred
	 */
	io.swagger.models.properties.Property create(Swagger swagger, Property<?> property)
			throws PropertyConversionException;

	/**
	 * Get the default SwaggerPropertyFactory.
	 * @return Default SwaggerPropertyFactory
	 */
	static SwaggerPropertyFactory getDefault() {
		return DefaultSwaggerPropertyFactory.INSTANCE;
	}

}
