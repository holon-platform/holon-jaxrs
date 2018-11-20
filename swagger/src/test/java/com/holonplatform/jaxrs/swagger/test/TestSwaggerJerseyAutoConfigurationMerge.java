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
package com.holonplatform.jaxrs.swagger.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyAutoConfiguration;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.spring.SwaggerResteasyAutoConfiguration;
import com.holonplatform.jaxrs.swagger.test.resources6.TestEndpoint6a;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class TestSwaggerJerseyAutoConfigurationMerge {

	@LocalServerPort
	private int port;

	@Configuration
	@EnableAutoConfiguration(exclude = { ResteasyAutoConfiguration.class, SwaggerResteasyAutoConfiguration.class })
	@ComponentScan(basePackageClasses = TestEndpoint6a.class)
	static class Config {

	}

	@Test
	public void testEndpoints() {
		Client client = JerseyClientBuilder.createClient();
		WebTarget target = client.target("http://localhost:" + port).path("object/{objectId}")
				.resolveTemplate("objectId", "test");
		String response = target.request().get(String.class);
		assertEquals("test", response);

		target = client.target("http://localhost:" + port).path("test2/check");
		response = target.request().get(String.class);
		assertEquals("text", response);
	}

	@Test
	public void testSwaggerJson() {
		Client client = JerseyClientBuilder.createClient();
		WebTarget target = client.target("http://localhost:" + port + SwaggerConfigurationProperties.DEFAULT_PATH);
		try (Response response = target.request().get()) {
			assertEquals(200, response.getStatus());
			assertNotNull(response.getEntity());
			assertEquals("application/json", response.getMediaType().toString());
			final String json = response.readEntity(String.class);
			assertTrue(json.contains("/object/{objectId}"));
			assertTrue(json.contains("/test2/check"));
		}
	}

}
