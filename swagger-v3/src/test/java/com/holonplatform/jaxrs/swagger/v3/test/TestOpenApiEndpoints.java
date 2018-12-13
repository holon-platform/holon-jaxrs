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

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.holonplatform.jaxrs.LogConfig;
import com.holonplatform.jaxrs.swagger.v3.internal.endpoints.QueryParamOpenApiEndpoint;
import com.holonplatform.jaxrs.swagger.v3.test.model.AbstractTestResource;
import com.holonplatform.jaxrs.swagger.v3.test.utils.OpenAPIEndpointUtils;
import com.holonplatform.test.JerseyTest5;

import io.swagger.v3.oas.models.OpenAPI;

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

	@Path("openapi")
	static class OpenApiEndpoint extends QueryParamOpenApiEndpoint {

	}

	@Override
	protected Application configure() {
		return new ResourceConfig().register(Resource1.class).register(OpenApiEndpoint.class);
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
	public void testOpenApi() {
		// dft
		Response response = target("/openapi").request().get();
		OpenAPI api = OpenAPIEndpointUtils.readAsJson(response);
		TestPropertyBoxModelConverter.validateApi(api);
		// json
		response = target("/openapi").queryParam("type", "json").request().get();
		api = OpenAPIEndpointUtils.readAsJson(response);
		TestPropertyBoxModelConverter.validateApi(api);
		// yaml
		response = target("/openapi").queryParam("type", "yaml").request().get();
		api = OpenAPIEndpointUtils.readAsYaml(response);
		TestPropertyBoxModelConverter.validateApi(api);
	}

}
