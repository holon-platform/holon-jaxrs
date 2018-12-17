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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
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
import com.holonplatform.jaxrs.swagger.v3.spring.ResteasySwaggerV3AutoConfiguration;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("disabled")
public class TestOpenApiAutoConfigurationDisabled {

	@LocalServerPort
	private int port;

	@Path("test")
	public static class TestResource {

		@GET
		@Path("ping")
		@Produces(MediaType.TEXT_PLAIN)
		public String ping() {
			return "pong";
		}

	}

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
		Client client = JerseyClientBuilder.createClient();
		WebTarget target = client.target("http://localhost:" + port + "/test").path("ping");
		String response = target.request().get(String.class);
		assertEquals("pong", response);
	}

	@Test
	public void testDisabled() {
		Client client = JerseyClientBuilder.createClient();
		WebTarget target = client.target("http://localhost:" + port + "/api-docs");
		try (Response response = target.request().get()) {
			assertEquals(404, response.getStatus());
		}
	}

}
