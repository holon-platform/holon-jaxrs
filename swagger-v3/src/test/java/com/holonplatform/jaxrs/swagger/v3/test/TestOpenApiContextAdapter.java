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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import jakarta.ws.rs.Path;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;

import com.holonplatform.jaxrs.swagger.v3.SwaggerV3;
import com.holonplatform.jaxrs.swagger.v3.test.model.AbstractTestResource;
import com.holonplatform.jaxrs.swagger.v3.test.utils.OpenApiValidation;

import io.swagger.v3.jaxrs2.integration.JaxrsApplicationScanner;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.OpenApiContextLocator;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class TestOpenApiContextAdapter {

	@Path("resource1")
	private static class TestResource1 extends AbstractTestResource {

	}

	@Test
	public void testContextAdapter() throws OpenApiConfigurationException {

		final String id = TestOpenApiContextAdapter.class.getName() + "_1";

		final SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration.setOpenAPI(new OpenAPI().info(new Info().title("Title of " + id).version("1")));
		configuration.setScannerClass(JaxrsApplicationScanner.class.getName());

		final ResourceConfig application = new ResourceConfig();
		application.register(TestResource1.class);
		
		final OpenApiContext openApiContext = SwaggerV3.adapt(new JaxrsOpenApiContextBuilder<>()
				.application(application)
				.openApiConfiguration(configuration).ctxId(id).buildContext(true));
		
		assertTrue(getOpenApiContext(id).isPresent());

		OpenAPI api = openApiContext.read();
		OpenApiValidation.validateTestResourceApi(api, "Title of " + id);

	}

	@Test
	public void testContextBuilder() {

		final String id = TestOpenApiContextAdapter.class.getName() + "_2";

		final SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration.setOpenAPI(new OpenAPI().info(new Info().title("Title of " + id).version("1")));
		configuration.setScannerClass(JaxrsApplicationScanner.class.getName());

		final ResourceConfig application = new ResourceConfig();
		application.register(TestResource1.class);

		final OpenApiContext openApiContext = com.holonplatform.jaxrs.swagger.v3.internal.context.JaxrsOpenApiContextBuilder
				.create().application(application).configuration(configuration).contextId(id).build(true);

		OpenAPI api = openApiContext.read();
		OpenApiValidation.validateTestResourceApi(api, "Title of " + id);

		assertTrue(getOpenApiContext(id).isPresent());

	}

	private static Optional<OpenApiContext> getOpenApiContext(String contextId) {
		return Optional.ofNullable(OpenApiContextLocator.getInstance().getOpenApiContext(contextId));
	}

}
