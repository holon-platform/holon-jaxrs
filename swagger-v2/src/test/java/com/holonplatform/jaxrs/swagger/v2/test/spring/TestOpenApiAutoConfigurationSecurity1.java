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
package com.holonplatform.jaxrs.swagger.v2.test.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyAutoConfiguration;
import com.holonplatform.jaxrs.swagger.v2.spring.ResteasySwaggerV2AutoConfiguration;
import com.holonplatform.jaxrs.swagger.v2.test.model.AbstractTestResource;
import com.holonplatform.jaxrs.swagger.v2.test.utils.SwaggerEndpointUtils;
import com.holonplatform.jaxrs.swagger.v2.test.utils.SwaggerValidation;

import io.swagger.models.SecurityRequirement;
import io.swagger.models.Swagger;

@ActiveProfiles("security1")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestOpenApiAutoConfigurationSecurity1 {

	@Path("resourcepath7")
	public static class TestResource extends AbstractTestResource {

	}

	@LocalServerPort
	private int port;

	@SpringBootConfiguration
	@EnableAutoConfiguration(exclude = { ResteasyAutoConfiguration.class, ResteasySwaggerV2AutoConfiguration.class })
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
		WebTarget target = client.target("http://localhost:" + port + "/resourcepath7").path("test10");
		String response = target.request().get(String.class);
		assertEquals("test", response);
	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApi() {
		final Client client = JerseyClientBuilder.createClient();
		// json
		Response response = client.target("http://localhost:" + port).path("api-docs").queryParam("type", "json")
				.request().get();
		Swagger api = SwaggerEndpointUtils.readAsJson(response);
		SwaggerValidation.validateTestResourceApi(api, "/resourcepath7", "Title security 1");
		validateSecurity(api);
		// yaml
		response = client.target("http://localhost:" + port).path("api-docs").queryParam("type", "yaml").request()
				.get();
		api = SwaggerEndpointUtils.readAsYaml(response);
		SwaggerValidation.validateTestResourceApi(api, "/resourcepath7", "Title security 1");
		validateSecurity(api);
	}

	private static void validateSecurity(Swagger api) {
		assertNotNull(api.getSecurity());
		assertEquals(2, api.getSecurity().size());

		SecurityRequirement sr = api.getSecurity().get(0);
		assertNotNull(sr);
		assertNotNull(sr.getRequirements());
		List<String> values = sr.getRequirements().get("req1");
		assertNotNull(values);
		assertEquals(1, values.size());
		assertEquals("val1", values.get(0));

		sr = api.getSecurity().get(1);
		assertNotNull(sr);
		assertNotNull(sr.getRequirements());
		values = sr.getRequirements().get("req2");
		assertNotNull(values);
		assertEquals(2, values.size());
		assertTrue(values.contains("val2"));
		assertTrue(values.contains("val3"));
	}

}
