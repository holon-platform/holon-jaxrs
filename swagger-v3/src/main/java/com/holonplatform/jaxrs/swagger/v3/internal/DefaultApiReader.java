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
package com.holonplatform.jaxrs.swagger.v3.internal;

import java.util.Collections;
import java.util.Set;

import com.holonplatform.jaxrs.swagger.ApiReader;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.v3.SwaggerV3;

import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * Default {@link ApiReader} implementation.
 *
 * @since 5.2.0
 */
public class DefaultApiReader implements ApiReader<OpenAPI> {

	protected final OpenAPIConfiguration configuration;

	public DefaultApiReader(OpenAPIConfiguration configuration) {
		super();
		this.configuration = (configuration != null) ? configuration : new SwaggerConfiguration();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiReader#read(java.util.Set)
	 */
	@Override
	public OpenAPI read(Set<Class<?>> classes) throws ApiConfigurationException {
		try {
			return SwaggerV3.adapt(new Reader(configuration)).read(classes, Collections.emptyMap());
		} catch (Exception e) {
			throw new ApiConfigurationException(e);
		}
	}

}
