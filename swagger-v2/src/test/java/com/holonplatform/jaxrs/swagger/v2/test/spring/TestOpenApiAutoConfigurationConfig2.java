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

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyAutoConfiguration;
import com.holonplatform.jaxrs.swagger.v2.spring.ResteasySwaggerV2AutoConfiguration;
import com.holonplatform.jaxrs.swagger.v2.test.resources.Resources;
import com.holonplatform.jaxrs.swagger.v2.test.utils.SwaggerEndpointUtils;

import io.swagger.models.Info;
import io.swagger.models.Swagger;

@ActiveProfiles("config2")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestOpenApiAutoConfigurationConfig2 {

	@LocalServerPort
	private int port;

	@ComponentScan(basePackageClasses = Resources.class)
	@SpringBootConfiguration
	@EnableAutoConfiguration(exclude = { ResteasyAutoConfiguration.class, ResteasySwaggerV2AutoConfiguration.class })
	static class Config {

	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApi() {
		final Client client = JerseyClientBuilder.createClient();
		// json
		Response response = client.target("http://localhost:" + port).path("api-docs").queryParam("type", "json")
				.request().get();
		Swagger api = SwaggerEndpointUtils.readAsJson(response);
		validate(api);
		// yaml
		response = client.target("http://localhost:" + port).path("api-docs").queryParam("type", "yaml").request()
				.get();
		api = SwaggerEndpointUtils.readAsYaml(response);
		validate(api);
	}

	public static void validate(Swagger api) {
		assertNotNull(api);
		assertNotNull(api.getPaths());
		assertEquals(25, api.getPaths().size());

		assertNotNull(api.getInfo());
		Info info = api.getInfo();
		assertEquals("Title config2", info.getTitle());
		assertEquals("0.0.2", info.getVersion());

		assertNotNull(api.getInfo().getContact());
		assertEquals("Test Contact", api.getInfo().getContact().getName());

		assertNotNull(api.getInfo().getLicense());
		assertEquals("My license", api.getInfo().getLicense().getName());
		assertEquals("https://foo.bar/license", api.getInfo().getLicense().getUrl());

		assertNotNull(api.getHost());
		assertEquals("https://localhost:8080", api.getHost());

	}

}
