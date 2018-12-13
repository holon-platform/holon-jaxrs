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
package com.holonplatform.jaxrs.swagger.v3.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.holonplatform.jaxrs.LogConfig;
import com.holonplatform.jaxrs.swagger.annotations.ApiEndpoint;
import com.holonplatform.jaxrs.swagger.v3.internal.endpoints.AcceptHeaderOpenApiEndpoint;
import com.holonplatform.jaxrs.swagger.v3.internal.endpoints.PathParamOpenApiEndpoint;
import com.holonplatform.jaxrs.swagger.v3.internal.endpoints.QueryParamOpenApiEndpoint;
import com.holonplatform.jaxrs.swagger.v3.test.utils.OpenAPIEndpointUtils;
import com.holonplatform.test.JerseyTest5;

import io.swagger.v3.oas.models.OpenAPI;

public class TestOpenApiEndpointsContext extends JerseyTest5 {

	private static Client client;

	@BeforeAll
	public static void setup() {
		LogConfig.setupLogging();
		client = JerseyClientBuilder.createClient();
	}

	@ApiEndpoint(value = "com.holonplatform.jaxrs.swagger.v3.test.TestOpenApiEndpointsContext.1", configLocation = "test_openapi_config_3.yml")
	@Path("openapi/query")
	static class QueryOpenApiEndpoint extends QueryParamOpenApiEndpoint {

	}

	@ApiEndpoint(value = "com.holonplatform.jaxrs.swagger.v3.test.TestOpenApiEndpointsContext.2", configLocation = "test_openapi_config_4.yml")
	@Path("/openapi.{type:json|yaml}")
	static class PathOpenApiEndpoint extends PathParamOpenApiEndpoint {

	}

	@ApiEndpoint(value = "com.holonplatform.jaxrs.swagger.v3.test.TestOpenApiEndpointsContext.3", configLocation = "test_openapi_config_5.yml")
	@Path("openapi/accept")
	static class AcceptOpenApiEndpoint extends AcceptHeaderOpenApiEndpoint {

	}

	@Override
	protected Application configure() {
		return new ResourceConfig()
				// .register(Resource1.class).register(Resource2.class).register(Resource3.class)
				.register(QueryOpenApiEndpoint.class).register(PathOpenApiEndpoint.class)
				.register(AcceptOpenApiEndpoint.class);
	}

	@Override
	protected Client getClient() {
		return client;
	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApiQueryParam() {
		// dft
		Response response = target("/openapi/query").request().get();
		OpenAPI api = OpenAPIEndpointUtils.readAsJson(response);
		TestPropertyBoxModelConverter.validateApi(api, "Test file config 3");
		// json
		response = target("/openapi/query").queryParam("type", "json").request().get();
		api = OpenAPIEndpointUtils.readAsJson(response);
		TestPropertyBoxModelConverter.validateApi(api, "Test file config 3");
		// yaml
		response = target("/openapi/query").queryParam("type", "yaml").request().get();
		api = OpenAPIEndpointUtils.readAsYaml(response);
		TestPropertyBoxModelConverter.validateApi(api, "Test file config 3");
	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApiPathParam() {
		// json
		Response response = target("/openapi.json").request().get();
		OpenAPI api = OpenAPIEndpointUtils.readAsJson(response);
		validateContextApi(api, "/resource2/test21", "Test file config 4");
		// yaml
		response = target("/openapi.yaml").request().get();
		api = OpenAPIEndpointUtils.readAsYaml(response);
		validateContextApi(api, "/resource2/test21", "Test file config 4");
	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApiAcceptHeader() {
		// json
		Response response = target("/openapi/accept").request().accept("application/json").get();
		OpenAPI api = OpenAPIEndpointUtils.readAsJson(response);
		validateContextApi(api, "/resource3/test31", "Test file config 5");
		// yaml
		response = target("/openapi/accept").request().accept("application/yaml").get();
		api = OpenAPIEndpointUtils.readAsYaml(response);
		validateContextApi(api, "/resource3/test31", "Test file config 5");
	}

	private static void validateContextApi(OpenAPI api, String path, String title) {
		assertNotNull(api);
		assertNotNull(api.getInfo());
		assertEquals(title, api.getInfo().getTitle());

		assertNotNull(api.getPaths());
		assertEquals(1, api.getPaths().size());

		TestPropertyBoxModelConverter
				.validateModel1(TestPropertyBoxModelConverter.validateOperationResponse(api, path));
	}

}
