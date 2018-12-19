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

import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import com.holonplatform.jaxrs.swagger.internal.endpoints.AbstractJaxrsApiEndpoint;

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

		}
		return null;
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
