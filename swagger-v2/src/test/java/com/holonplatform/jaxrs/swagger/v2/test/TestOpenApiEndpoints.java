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
package com.holonplatform.jaxrs.swagger.v2.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.holonplatform.jaxrs.LogConfig;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpoint;
import com.holonplatform.jaxrs.swagger.v2.internal.endpoints.AcceptHeaderSwaggerEndpoint;
import com.holonplatform.jaxrs.swagger.v2.internal.endpoints.PathParamSwaggerEndpoint;
import com.holonplatform.jaxrs.swagger.v2.internal.endpoints.QueryParamSwaggerEndpoint;
import com.holonplatform.jaxrs.swagger.v2.test.model.AbstractTestResource;
import com.holonplatform.jaxrs.swagger.v2.test.utils.SwaggerEndpointUtils;
import com.holonplatform.jaxrs.swagger.v2.test.utils.SwaggerValidation;
import com.holonplatform.test.JerseyTest5;

import io.swagger.models.Swagger;

public class TestOpenApiEndpoints extends JerseyTest5 {

	private static Client client;

	@BeforeAll
	public static void setup() {
		LogConfig.setupLogging();
		client = JerseyClientBuilder.createClient();
	}

	@Path("resource1")
	static class Resource1 extends AbstractTestResource {

	}

	@Path("openapi/query")
	static class QueryOpenApiEndpoint extends QueryParamSwaggerEndpoint {

	}

	@ApiEndpoint(value = "com.holonplatform.jaxrs.swagger.v2.test.TestOpenApiEndpoints.1", scannerType = JaxrsScannerType.APPLICATION)
	@Path("/openapi.{type:json|yaml}")
	static class PathOpenApiEndpoint extends PathParamSwaggerEndpoint {

	}

	@ApiEndpoint(value = "com.holonplatform.jaxrs.swagger.v2.test.TestOpenApiEndpoints.2", scannerType = JaxrsScannerType.APPLICATION)
	@Path("openapi/accept")
	static class AcceptOpenApiEndpoint extends AcceptHeaderSwaggerEndpoint {

	}

	@Override
	protected Application configure() {
		return new ResourceConfig().register(Resource1.class).register(QueryOpenApiEndpoint.class)
				.register(PathOpenApiEndpoint.class).register(AcceptOpenApiEndpoint.class);
	}

	@Override
	protected Client getClient() {
		return client;
	}

	@Test
	public void testResource() {
		String value = target("/resource1/test10").request().get(String.class);
		assertEquals("test", value);
	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApiQueryParam() {
		// dft
		Response response = target("/openapi/query").request().get();
		Swagger api = SwaggerEndpointUtils.readAsJson(response);
		SwaggerValidation.validateTestResourceApi(api, null);
		// json
		response = target("/openapi/query").queryParam("type", "json").request().get();
		api = SwaggerEndpointUtils.readAsJson(response);
		SwaggerValidation.validateTestResourceApi(api, null);
		// yaml
		response = target("/openapi/query").queryParam("type", "yaml").request().get();
		api = SwaggerEndpointUtils.readAsYaml(response);
		SwaggerValidation.validateTestResourceApi(api, null);
	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApiPathParam() {
		// json
		Response response = target("/openapi.json").request().get();
		Swagger api = SwaggerEndpointUtils.readAsJson(response);
		SwaggerValidation.validateTestResourceApi(api, null);
		// yaml
		response = target("/openapi.yaml").request().get();
		api = SwaggerEndpointUtils.readAsYaml(response);
		SwaggerValidation.validateTestResourceApi(api, null);
	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApiAcceptHeader() {
		// json
		Response response = target("/openapi/accept").request().accept("application/json").get();
		Swagger api = SwaggerEndpointUtils.readAsJson(response);
		SwaggerValidation.validateTestResourceApi(api, null);
		// yaml
		response = target("/openapi/accept").request().accept("application/yaml").get();
		api = SwaggerEndpointUtils.readAsYaml(response);
		SwaggerValidation.validateTestResourceApi(api, null);
	}

}
