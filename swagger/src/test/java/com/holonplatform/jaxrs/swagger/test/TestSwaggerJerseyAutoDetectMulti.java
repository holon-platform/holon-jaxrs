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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyAutoConfiguration;
import com.holonplatform.jaxrs.swagger.spring.SwaggerResteasyAutoConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class TestSwaggerJerseyAutoDetectMulti {

	@LocalServerPort
	private int port;

	@Configuration
	@EnableAutoConfiguration(exclude = { ResteasyAutoConfiguration.class, SwaggerResteasyAutoConfiguration.class })
	@ComponentScan(basePackages = "com.holonplatform.jaxrs.swagger.test.resources4")
	static class Config {

	}

	@Test
	public void testEndpoints() {
		Client client = JerseyClientBuilder.createClient();

		WebTarget target = client.target("http://localhost:" + port + "/v1").path("ping");
		String response = target.request().get(String.class);
		Assert.assertEquals("pong", response);

		target = client.target("http://localhost:" + port + "/v2").path("ping");
		response = target.request().get(String.class);
		Assert.assertEquals("pong", response);
	}

	@Test
	public void testSwaggerJson() {
		Client client = JerseyClientBuilder.createClient();

		WebTarget target = client.target("http://localhost:" + port + "/v1/docs");
		try (Response response = target.request().get()) {
			Assert.assertEquals(200, response.getStatus());
			Assert.assertNotNull(response.getEntity());
			Assert.assertEquals("application/json", response.getMediaType().toString());
		}

		target = client.target("http://localhost:" + port + "/v2/docs");
		try (Response response = target.request().get()) {
			Assert.assertEquals(200, response.getStatus());
			Assert.assertNotNull(response.getEntity());
			Assert.assertEquals("application/json", response.getMediaType().toString());
		}
	}

	@Test
	public void testSwaggerYaml() {
		Client client = JerseyClientBuilder.createClient();

		WebTarget target = client.target("http://localhost:" + port + "/v1/docs").queryParam("type", "yaml");
		try (Response response = target.request().get()) {
			Assert.assertEquals(200, response.getStatus());
			Assert.assertNotNull(response.getEntity());
			Assert.assertEquals("application/yaml", response.getMediaType().toString());
		}

		target = client.target("http://localhost:" + port + "/v2/docs").queryParam("type", "yaml");
		try (Response response = target.request().get()) {
			Assert.assertEquals(200, response.getStatus());
			Assert.assertNotNull(response.getEntity());
			Assert.assertEquals("application/yaml", response.getMediaType().toString());
		}
	}

}
