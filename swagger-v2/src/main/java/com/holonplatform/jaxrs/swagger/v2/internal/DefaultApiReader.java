/*
 * Copyright 2016-2018 Axioma srl.
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
package com.holonplatform.jaxrs.swagger.v2.internal;

import java.util.HashSet;
import java.util.Set;

import com.holonplatform.jaxrs.swagger.ApiReader;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.v2.SwaggerV2;

import io.swagger.config.SwaggerConfig;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Swagger;

/**
 * Default {@link ApiReader} implementation.
 *
 * @since 5.2.0
 */
public class DefaultApiReader implements ApiReader<Swagger> {

	private final SwaggerConfig configuration;

	public DefaultApiReader(SwaggerConfig configuration) {
		super();
		this.configuration = (configuration != null) ? configuration : new BeanConfig();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiReader#read(java.util.Set)
	 */
	@Override
	public Swagger read(Set<Class<?>> classes) throws ApiConfigurationException {
		Set<Class<?>> cls = new HashSet<>();
		if (classes != null) {
			cls.addAll(classes);
		}
		if (!cls.contains(SwaggerV2.CONTEXT_READER_LISTENER)) {
			cls.add(SwaggerV2.CONTEXT_READER_LISTENER);
		}
		final Reader reader = new Reader(configuration.configure(new Swagger()));
		return reader.read(cls);
	}

}
