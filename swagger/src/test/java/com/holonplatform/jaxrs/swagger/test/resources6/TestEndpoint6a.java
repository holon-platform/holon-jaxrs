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
package com.holonplatform.jaxrs.swagger.test.resources6;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.holonplatform.jaxrs.swagger.annotations.ApiDefinition;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@ApiDefinition(docsPath = SwaggerConfigurationProperties.DEFAULT_PATH, title = "Test", version = "v1", prettyPrint = true)
@Api
@Component
@Path("object")
public class TestEndpoint6a {

	@GET
	@Path("ping")
	@Produces(MediaType.TEXT_PLAIN)
	public String ping() {
		return "pong";
	}

	@ApiOperation("Test description")
	@ApiResponses(@ApiResponse(code = 200, message = "OK"))
	@GET
	@Path("{objectId}")
	public Response getObject(@PathParam("objectId") String templateVar) {
		return Response.ok().entity(templateVar).build();
	}

}
