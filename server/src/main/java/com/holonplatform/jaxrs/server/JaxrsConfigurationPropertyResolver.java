/*
 * Copyright 2000-2016 Holon TDCN.
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
package com.holonplatform.jaxrs.server;

import java.util.stream.Stream;

import javax.ws.rs.core.Configuration;

import com.holonplatform.core.config.ConfigPropertyProvider;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;

/**
 * {@link ConfigPropertyProvider} using JAX-RS {@link Configuration}
 * 
 * @since 5.0.0
 */
public class JaxrsConfigurationPropertyResolver implements ConfigPropertyProvider {

	/*
	 * Configuration
	 */
	private final Configuration configuration;

	/**
	 * Constructor
	 * @param configuration JAX-RS configuration
	 */
	public JaxrsConfigurationPropertyResolver(Configuration configuration) {
		super();
		ObjectUtils.argumentNotNull(configuration, "Configuration must be not null");
		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.config.ConfigPropertyProvider#containsProperty(java.lang.String)
	 */
	@Override
	public boolean containsProperty(String key) {
		return configuration.getProperty(key) != null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.config.ConfigPropertyProvider#getProperty(java.lang.String, java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(String key, Class<T> targetType) throws IllegalArgumentException {
		ObjectUtils.argumentNotNull(key, "Property name must be not null");
		ObjectUtils.argumentNotNull(targetType, "Property type must be not null");

		Object value = configuration.getProperty(key);
		if (value != null) {
			if (TypeUtils.isAssignable(targetType, value.getClass())) {
				return (T) value;
			}
			if (TypeUtils.isString(value.getClass())) {
				return ConversionUtils.convertStringValue((String) value, targetType);
			}
			if (TypeUtils.isBoolean(targetType) && !TypeUtils.isBoolean(value.getClass())) {
				return (T) ConversionUtils.convertStringValue(value.toString(), boolean.class);
			}
			if (TypeUtils.isNumber(targetType)) {
				if (TypeUtils.isNumber(value.getClass())) {
					return (T) ConversionUtils.convertNumberToTargetClass((Number) value, (Class<Number>) targetType);
				} else {
					return (T) ConversionUtils.parseNumber(value.toString(), (Class<Number>) targetType);
				}
			}
			return (T) value;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.config.ConfigPropertyProvider#getPropertyNames()
	 */
	@Override
	public Stream<String> getPropertyNames() throws UnsupportedOperationException {
		return configuration.getPropertyNames().stream();
	}

}
