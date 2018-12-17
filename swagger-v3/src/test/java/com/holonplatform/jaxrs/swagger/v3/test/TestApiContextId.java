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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.jaxrs.swagger.annotations.ApiContextId;
import com.holonplatform.jaxrs.swagger.v3.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.v3.OpenApi;
import com.holonplatform.jaxrs.swagger.v3.test.model.Model1;
import com.holonplatform.jaxrs.swagger.v3.test.utils.OpenApiValidation;

import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;

public class TestApiContextId {

	private static final String CONTEXT_1 = "com.holonplatform.jaxrs.swagger.v3.test.TestApiContextId_1";
	private static final String CONTEXT_2 = "com.holonplatform.jaxrs.swagger.v3.test.TestApiContextId_2";

	@ApiContextId(CONTEXT_1)
	@Path("resource1")
	private static class TestResource1 {

		@Operation(summary = "Test operation 1")
		@GET
		@Path("test")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Model1.class) PropertyBox test() {
			return PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build();
		}

	}

	@ApiContextId(CONTEXT_2)
	@Path("resource2")
	private static class TestResource2 {

		@Operation(summary = "Test operation 2")
		@GET
		@Path("test")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Model1.class) PropertyBox test() {
			return PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build();
		}

	}

	@ApiContextId(CONTEXT_2)
	@Path("resource3")
	private static class TestResource3 {

		@Operation(summary = "Test operation 3")
		@GET
		@Path("test")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Model1.class) PropertyBox test() {
			return PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build();
		}

	}

	@Test
	public void testApiContextId() throws OpenApiConfigurationException {

		final ResourceConfig application = new ResourceConfig();
		application.register(TestResource1.class);
		application.register(TestResource2.class);
		application.register(TestResource3.class);

		// context 1
		final SwaggerConfiguration configuration1 = new SwaggerConfiguration();
		configuration1.setOpenAPI(new OpenAPI().info(new Info().title("Title of " + CONTEXT_1).version("1")));

		final OpenApiContext openApiContext1 = OpenApi.adapt(new JaxrsOpenApiContextBuilder<>().application(application)
				.openApiConfiguration(configuration1).ctxId(CONTEXT_1).buildContext(true));
		OpenAPI api = openApiContext1.read();
		validateApiContext1(api);

		// context 2
		final SwaggerConfiguration configuration2 = new SwaggerConfiguration();
		configuration2.setOpenAPI(new OpenAPI().info(new Info().title("Title of " + CONTEXT_2).version("1")));

		final OpenApiContext openApiContext2 = OpenApi.contextBuilder().application(application)
				.configuration(configuration2).contextId(CONTEXT_2).scannerType(JaxrsScannerType.APPLICATION)
				.build(true);
		api = openApiContext2.read();
		validateApiContext2(api);

	}

	private static void validateApiContext1(OpenAPI api) {
		assertNotNull(api);
		assertNotNull(api.getInfo());
		assertEquals("Title of " + CONTEXT_1, api.getInfo().getTitle());

		assertNotNull(api.getPaths());
		assertEquals(1, api.getPaths().size());

		OpenApiValidation.validateModel1(OpenApiValidation.validateOperationResponse(api, "/resource1/test"));
	}

	private static void validateApiContext2(OpenAPI api) {
		assertNotNull(api);
		assertNotNull(api.getInfo());
		assertEquals("Title of " + CONTEXT_2, api.getInfo().getTitle());

		assertNotNull(api.getPaths());
		assertEquals(2, api.getPaths().size());

		OpenApiValidation.validateModel1(OpenApiValidation.validateOperationResponse(api, "/resource2/test"));
		OpenApiValidation.validateModel1(OpenApiValidation.validateOperationResponse(api, "/resource3/test"));

		PathItem item = api.getPaths().get("/resource2/test");
		assertNotNull(item);
		io.swagger.v3.oas.models.Operation op = item.getGet();
		assertNotNull(op);
		assertEquals("Test operation 2", op.getSummary());

		item = api.getPaths().get("/resource3/test");
		assertNotNull(item);
		op = item.getGet();
		assertNotNull(op);
		assertEquals("Test operation 3", op.getSummary());
	}

}
