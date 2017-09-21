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
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyAutoConfiguration;
import com.holonplatform.jaxrs.swagger.test.resources.TestEndpoint;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("path")
public class TestSwaggerJerseyAutoConfigurationPath {

	@LocalServerPort
	private int port;

	@Configuration
	@EnableAutoConfiguration(exclude = ResteasyAutoConfiguration.class)
	static class Config {

		@Bean
		public ResourceConfig applicationConfig() {
			ResourceConfig cfg = new ResourceConfig();
			cfg.register(TestEndpoint.class);
			return cfg;
		}

	}

	@Test
	public void testEndpoint() {
		Client client = JerseyClientBuilder.createClient();
		WebTarget target = client.target("http://localhost:" + port + "/api/test").path("ping");
		String response = target.request().get(String.class);
		Assert.assertEquals("pong", response);
	}

	@Test
	public void testSwaggerJson() {
		Client client = JerseyClientBuilder.createClient();
		WebTarget target = client.target("http://localhost:" + port + "/api/docs");
		Response response = target.request().get();
		Assert.assertEquals(200, response.getStatus());
		Assert.assertNotNull(response.getEntity());
		Assert.assertEquals("application/json", response.getMediaType().toString());
	}

	@Test
	public void testSwaggerYaml() {
		Client client = JerseyClientBuilder.createClient();
		WebTarget target = client.target("http://localhost:" + port + "/api/docs").queryParam("type", "yaml");
		Response response = target.request().get();
		Assert.assertEquals(200, response.getStatus());
		Assert.assertNotNull(response.getEntity());
		Assert.assertEquals("application/yaml", response.getMediaType().toString());
	}

}
