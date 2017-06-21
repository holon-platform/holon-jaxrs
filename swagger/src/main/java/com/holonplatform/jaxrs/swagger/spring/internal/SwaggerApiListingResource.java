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
package com.holonplatform.jaxrs.swagger.spring.internal;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang3.StringUtils;

import com.holonplatform.core.internal.utils.AnnotationUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.jaxrs.config.SwaggerConfigLocator;
import io.swagger.models.Swagger;

/**
 * JAX-RS resource to provide Swagger API documentation.
 * 
 * @since 5.0.0
 */
public class SwaggerApiListingResource {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, "application/yaml" })
	@ApiOperation(value = "Swagger API documentation in either JSON or YAML", hidden = true)
	public Response getApiListing(@QueryParam("type") String type) {
		Swagger swagger = getSwagger(getConfigId());
		if (swagger == null) {
			return Response.noContent().build();
		}

		ResponseBuilder response = Response.ok().entity(swagger);

		if (StringUtils.isNotBlank(type) && type.trim().equalsIgnoreCase("yaml")) {
			// as YAML
			response.type("application/yaml");
		} else {
			// as JSON
			response.type(MediaType.APPLICATION_JSON_TYPE);
		}

		return response.build();
	}

	/**
	 * Get the Swagger config id. By default, if the {@link ApiGroupId} annotation is present, the API group id is
	 * returned.
	 * @return The Swagger config id
	 */
	private String getConfigId() {
		if (getClass().isAnnotationPresent(ApiGroupId.class)) {
			return AnnotationUtils.getStringValue(getClass().getAnnotation(ApiGroupId.class).value());
		}
		return null;
	}

	/**
	 * Get the Swagger definition using given configuration id.
	 * @param configId Configuration is
	 * @return Swagger definition, <code>null</code> if not available
	 */
	private static Swagger getSwagger(String configId) {
		return SwaggerConfigLocator.getInstance().getSwagger(configId);
	}

}
