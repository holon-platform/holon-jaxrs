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
package com.holonplatform.jaxrs.swagger.internal;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyValueConverter.PropertyConversionException;

import io.swagger.converter.ModelConverters;

/**
 * Default {@link SwaggerPropertyFactory} implementation.
 *
 * @since 5.0.0
 */
public enum DefaultSwaggerPropertyFactory implements SwaggerPropertyFactory {

	INSTANCE;

	/* (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.SwaggerPropertyFactory#create(com.holonplatform.core.property.Property)
	 */
	@Override
	public io.swagger.models.properties.Property create(Property<?> property) throws PropertyConversionException {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		
		// TODO validators
		
		io.swagger.models.properties.Property p = ModelConverters.getInstance().readAsProperty(property.getType());
		configureProperty(p, property);
		return p;
	}

	private static void configureProperty(io.swagger.models.properties.Property property, Property<?> source) {
		// read-only
		if (source.isReadOnly()) {
			property.setReadOnly(Boolean.TRUE);
		}
		// i18n
		String message = LocalizationContext.translate(source, true);
		if (message != null) {
			property.setTitle(message);
		}
	}

}
