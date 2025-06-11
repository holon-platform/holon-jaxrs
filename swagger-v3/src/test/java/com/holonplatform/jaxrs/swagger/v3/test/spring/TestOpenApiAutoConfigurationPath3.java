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
package com.holonplatform.jaxrs.swagger.v3.test.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyAutoConfiguration;
import com.holonplatform.jaxrs.swagger.v3.spring.ResteasySwaggerV3AutoConfiguration;
import com.holonplatform.jaxrs.swagger.v3.test.model.AbstractTestResource;
import com.holonplatform.jaxrs.swagger.v3.test.utils.OpenAPIEndpointUtils;
import com.holonplatform.jaxrs.swagger.v3.test.utils.OpenApiValidation;

import io.swagger.v3.oas.models.OpenAPI;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

@ActiveProfiles("path3")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestOpenApiAutoConfigurationPath3 {

	@Path("resourcepath3")
	public static class TestResource extends AbstractTestResource {

	}

	@LocalServerPort
	private int port;

	@SpringBootConfiguration
	@EnableAutoConfiguration(exclude = { ResteasyAutoConfiguration.class, ResteasySwaggerV3AutoConfiguration.class })
	static class Config {

		@Bean
		public ResourceConfig applicationConfig() {
			ResourceConfig cfg = new ResourceConfig();
			cfg.register(TestResource.class);
			return cfg;
		}

	}

	@Test
	public void testEndpoint() {
		final Client client = JerseyClientBuilder.createClient();
		WebTarget target = client.target("http://localhost:" + port + "/resourcepath3").path("test10");
		String response = target.request().get(String.class);
		assertEquals("test", response);
	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApi() {
		final Client client = JerseyClientBuilder.createClient();
		// json
		Response response = client.target("http://localhost:" + port).path("docs").request().accept("application/json")
				.get();
		OpenAPI api = OpenAPIEndpointUtils.readAsJson(response);
		OpenApiValidation.validateTestResourceApi(api, "/resourcepath3", null);
		// yaml
		response = client.target("http://localhost:" + port).path("docs").request().accept("application/yaml").get();
		api = OpenAPIEndpointUtils.readAsYaml(response);
		OpenApiValidation.validateTestResourceApi(api, "/resourcepath3", null);
	}

}
