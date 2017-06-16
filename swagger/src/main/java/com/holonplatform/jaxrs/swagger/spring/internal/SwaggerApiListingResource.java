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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.jaxrs.listing.BaseApiListingResource;

/**
 * JAX-RS resource to provide Swagger api documentation.
 * 
 * @since 5.0.0
 */
@Path("/api-docs")
public class SwaggerApiListingResource extends BaseApiListingResource {

	@Context
	ServletContext context;

	@GET
	@Produces({ MediaType.APPLICATION_JSON, "application/yaml" })
	@ApiOperation(value = "Swagger API documentation in either JSON or YAML", hidden = true)
	public Response getListing(@Context Application app, @Context ServletConfig sc, @Context HttpHeaders headers,
			@Context UriInfo uriInfo, @QueryParam("type") String type) throws JsonProcessingException {
		if (StringUtils.isNotBlank(type) && type.trim().equalsIgnoreCase("yaml")) {
			return getListingYamlResponse(app, context, sc, headers, uriInfo);
		} else {
			return getListingJsonResponse(app, context, sc, headers, uriInfo);
		}
	}

}
