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
package com.holonplatform.jaxrs.swagger.v3.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.holonplatform.jaxrs.swagger.v3.internal.endpoints.AbstractOpenApiEndpoint;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Default JAX-RS resource to implement an OpenAPI documentation endpoint.
 * <p>
 * This endpoint uses the <code>Accept</code> header value to declare the OpenAPI output type, which can be either
 * <code>json</code> or <code>yaml</code>.
 * </p>
 *
 * @since 5.2.0
 */
public class AcceptHeaderOpenApiEndpoint extends AbstractOpenApiEndpoint {

	@Context
	Application application;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(hidden = true)
	public Response getOpenApiJson(@Context HttpHeaders headers, @Context UriInfo uriInfo) throws Exception {
		return super.getOpenApi(headers, application, uriInfo, "json");
	}

	@GET
	@Produces({ "application/yaml" })
	@Operation(hidden = true)
	public Response getOpenApiYaml(@Context HttpHeaders headers, @Context UriInfo uriInfo) throws Exception {
		return super.getOpenApi(headers, application, uriInfo, "yaml");
	}

}
