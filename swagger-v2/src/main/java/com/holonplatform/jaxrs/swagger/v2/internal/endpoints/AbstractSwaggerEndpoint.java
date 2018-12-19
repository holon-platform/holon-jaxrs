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
package com.holonplatform.jaxrs.swagger.v2.internal.endpoints;

import java.util.Collections;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.internal.endpoints.AbstractJaxrsApiEndpoint;
import com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration;
import com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContextBuilder;

import io.swagger.config.SwaggerConfig;
import io.swagger.core.filter.SpecFilter;
import io.swagger.core.filter.SwaggerSpecFilter;
import io.swagger.jaxrs.config.SwaggerConfigLocator;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

/**
 * Base JAX-RS resource to implement an Swagger documentation endpoint.
 *
 * @since 5.2.0
 */
public abstract class AbstractSwaggerEndpoint extends AbstractJaxrsApiEndpoint<Swagger> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.endpoints.AbstractJaxrsApiEndpoint#getApi(java.lang.String,
	 * javax.ws.rs.core.Application, javax.ws.rs.core.HttpHeaders, javax.ws.rs.core.UriInfo)
	 */
	@Override
	protected ApiDefinition<Swagger> getApi(String contextId, Application application, HttpHeaders headers,
			UriInfo uriInfo) throws Exception {
		// check available
		Swagger api = SwaggerConfigLocator.getInstance().getSwagger(contextId);

		// build context
		if (api == null) {
			api = JaxrsSwaggerApiContextBuilder.create()
					// context id
					.contextId(contextId)
					// JAX-RS Application
					.application(application)
					// scanner type
					.scannerType(getJaxrsScannerType().orElse(JaxrsScannerType.DEFAULT))
					// build and init
					.build(true).read();
		}

		final SwaggerConfig config = SwaggerConfigLocator.getInstance().getConfig(contextId);
		
		// check filters
		if (config != null && config.getFilterClass() != null) {
			try {
				final SwaggerSpecFilter filter = (SwaggerSpecFilter) Class.forName(config.getFilterClass())
						.newInstance();
				api = new SpecFilter().filter(api, filter, Collections.unmodifiableMap(uriInfo.getQueryParameters()),
						getCookies(headers), Collections.unmodifiableMap(headers.getRequestHeaders()));
			} catch (Exception e) {
				LOGGER.warn("Failed to load filter using class [" + config.getFilterClass() + "]", e);
			}
		}
		// check pretty
		boolean pretty = false;
		if (config != null && config instanceof SwaggerConfiguration) {
			pretty = ((SwaggerConfiguration)config).isPrettyPrint();
		}
		return ApiDefinition.create(api, pretty);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.internal.endpoints.AbstractJaxrsApiEndpoint#getApiOutput(com.holonplatform.jaxrs.
	 * swagger.internal.endpoints.AbstractJaxrsApiEndpoint.OutputType, java.lang.Object, boolean)
	 */
	@Override
	protected String getApiOutput(OutputType outputType, Swagger api, boolean pretty) throws Exception {
		switch (outputType) {
		case YAML:
			return pretty ? Yaml.pretty().writeValueAsString(api) : Yaml.mapper().writeValueAsString(api);
		case JSON:
		default:
			return pretty ? Json.pretty(api) : Json.mapper().writeValueAsString(api);
		}
	}

}
