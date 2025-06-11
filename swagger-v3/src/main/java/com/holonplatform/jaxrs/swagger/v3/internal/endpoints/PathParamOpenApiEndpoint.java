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

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Default JAX-RS resource to implement an OpenAPI documentation endpoint.
 * <p>
 * This endpoint uses a <code>type</code> path parameter to declare the OpenAPI output type, which can be either
 * <code>json</code> or <code>yaml</code>.
 * </p>
 *
 * @since 5.2.0
 */
public class PathParamOpenApiEndpoint extends AbstractOpenApiEndpoint {

	@Context
	Application application;

	@GET
	@Produces({ MediaType.APPLICATION_JSON, "application/yaml" })
	@Operation(hidden = true)
	public Response getOpenApi(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("type") String type) {
		return getApi(application, headers, uriInfo, type);
	}

}
