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

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.ApiOperation;

/**
 * Default JAX-RS resource to implement a Swagger documentation endpoint.
 * <p>
 * This endpoint uses a <code>type</code> query parameter to declare the OpenAPI output type, which can be either
 * <code>json</code> or <code>yaml</code>.
 * </p>
 *
 * @since 5.2.0
 */
public class QueryParamSwaggerEndpoint extends AbstractSwaggerEndpoint {

	@Context
	Application application;

	@GET
	@Produces({ MediaType.APPLICATION_JSON, "application/yaml" })
	@ApiOperation(value = "Get the API model", hidden = true)
	public Response getSwaggerApi(@Context HttpHeaders headers, @Context UriInfo uriInfo,
			@QueryParam("type") String type) {
		return getApi(application, headers, uriInfo, type);
	}

}
