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
package com.holonplatform.jaxrs.swagger.v3.internal.endpoints;

import java.util.Collections;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.internal.endpoints.AbstractJaxrsApiEndpoint;
import com.holonplatform.jaxrs.swagger.v3.internal.context.JaxrsOpenApiContextBuilder;

import io.swagger.v3.core.filter.OpenAPISpecFilter;
import io.swagger.v3.core.filter.SpecFilter;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.integration.OpenApiContextLocator;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * Base JAX-RS resource to implement an OpenAPI documentation endpoint.
 *
 * @since 5.2.0
 */
public abstract class AbstractOpenApiEndpoint extends AbstractJaxrsApiEndpoint<OpenAPI> {

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.internal.endpoints.AbstractJaxrsApiEndpoint#getApi(java.lang.
	 * String, jakarta.ws.rs.core.Application, jakarta.ws.rs.core.HttpHeaders,
	 * jakarta.ws.rs.core.UriInfo)
	 */
	@Override
	protected ApiDefinition<OpenAPI> getApi(String contextId, Application application, HttpHeaders headers,
			UriInfo uriInfo) throws Exception {
		// check available
		OpenApiContext openApiContext = OpenApiContextLocator.getInstance().getOpenApiContext(contextId);

		// build context
		if (openApiContext == null) {
			openApiContext = JaxrsOpenApiContextBuilder.create()
					// context id
					.contextId(contextId)
					// JAX-RS Application
					.application(application)
					// config location
					.configLocation(getConfigLocation().orElse(null))
					// scanner type
					.scannerType(getJaxrsScannerType().orElse(JaxrsScannerType.DEFAULT))
					// build and init
					.build(true);
		}

		// read the OpenAPI definitions
		OpenAPI api = openApiContext.read();

		// check filters
		if (openApiContext.getOpenApiConfiguration() != null
				&& openApiContext.getOpenApiConfiguration().getFilterClass() != null) {
			try {
				OpenAPISpecFilter filterImpl = (OpenAPISpecFilter) Class
						.forName(openApiContext.getOpenApiConfiguration().getFilterClass()).getDeclaredConstructor()
						.newInstance();
				SpecFilter f = new SpecFilter();
				api = f.filter(api, filterImpl, Collections.unmodifiableMap(uriInfo.getQueryParameters()),
						getCookies(headers), Collections.unmodifiableMap(headers.getRequestHeaders()));
			} catch (Exception e) {
				LOGGER.error("failed to load filter", e);
			}
		}

		// check pretty
		boolean pretty = false;
		if (openApiContext.getOpenApiConfiguration() != null
				&& Boolean.TRUE.equals(openApiContext.getOpenApiConfiguration().isPrettyPrint())) {
			pretty = true;
		}

		return ApiDefinition.create(api, pretty);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.internal.endpoints.AbstractJaxrsApiEndpoint#getApiOutput(com.
	 * holonplatform.jaxrs. swagger.internal.endpoints.AbstractJaxrsApiEndpoint.OutputType,
	 * java.lang.Object, boolean)
	 */
	@Override
	protected String getApiOutput(OutputType outputType, OpenAPI api, boolean pretty) throws Exception {
		switch (outputType) {
		case YAML:
			return pretty ? Yaml.pretty(api) : Yaml.mapper().writeValueAsString(api);
		case JSON:
		default:
			return pretty ? Json.pretty(api) : Json.mapper().writeValueAsString(api);
		}
	}

}
