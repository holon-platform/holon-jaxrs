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
package com.holonplatform.jaxrs.swagger.test.config.resteasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import com.holonplatform.jaxrs.swagger.spring.SwaggerJerseyAutoConfiguration;
import com.holonplatform.jaxrs.swagger.test.resources2.TestEndpoint2;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dft")
public class TestSwaggerResteasyAutoConfigurationResource {

	@LocalServerPort
	private int port;

	@SpringBootConfiguration
	@EnableAutoConfiguration(exclude = { JerseyAutoConfiguration.class, SwaggerJerseyAutoConfiguration.class })
	@ComponentScan(basePackageClasses = TestEndpoint2.class)
	static class Config {

	}

	@Test
	public void testEndpoint() {
		Client client = new ResteasyClientBuilder().build();
		WebTarget target = client.target("http://localhost:" + port + "/test2").path("ping");
		String response = target.request().get(String.class);
		assertEquals("pong", response);
	}

	@Test
	public void testSwaggerJson() {
		Client client = new ResteasyClientBuilder().build();
		WebTarget target = client.target("http://localhost:" + port + "/docs");
		try (Response response = target.request().get()) {
			assertEquals(200, response.getStatus());
			assertNotNull(response.readEntity(String.class));
			assertEquals("application/json", response.getMediaType().toString());
		}
	}

	@Test
	public void testSwaggerYaml() {
		Client client = new ResteasyClientBuilder().build();
		WebTarget target = client.target("http://localhost:" + port + "/docs").queryParam("type", "yaml");
		try (Response response = target.request().get()) {
			assertEquals(200, response.getStatus());
			assertNotNull(response.readEntity(String.class));
			assertEquals("application/yaml", response.getMediaType().toString());
		}
	}

}
