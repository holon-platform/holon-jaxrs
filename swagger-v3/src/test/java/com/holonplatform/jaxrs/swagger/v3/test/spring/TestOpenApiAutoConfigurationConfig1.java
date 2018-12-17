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
import com.holonplatform.jaxrs.swagger.v3.spring.ResteasySwaggerV3AutoConfiguration;
import com.holonplatform.jaxrs.swagger.v3.test.resources.Resources;
import com.holonplatform.jaxrs.swagger.v3.test.utils.OpenAPIEndpointUtils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

@ActiveProfiles("config1")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestOpenApiAutoConfigurationConfig1 {

	@LocalServerPort
	private int port;

	@ComponentScan(basePackageClasses = Resources.class)
	@SpringBootConfiguration
	@EnableAutoConfiguration(exclude = { ResteasyAutoConfiguration.class, ResteasySwaggerV3AutoConfiguration.class })
	static class Config {

	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApi() {
		final Client client = JerseyClientBuilder.createClient();
		// json
		Response response = client.target("http://localhost:" + port).path("api-docs").queryParam("type", "json")
				.request().get();
		OpenAPI api = OpenAPIEndpointUtils.readAsJson(response);
		validate(api);
		// yaml
		response = client.target("http://localhost:" + port).path("api-docs").queryParam("type", "yaml").request()
				.get();
		api = OpenAPIEndpointUtils.readAsYaml(response);
		validate(api);
	}

	public static void validate(OpenAPI api) {
		assertNotNull(api);
		assertNotNull(api.getPaths());
		assertEquals(25, api.getPaths().size());
		
		assertNotNull(api.getInfo());
		Info info = api.getInfo();
		assertEquals("Title config1", info.getTitle());
		assertEquals("0.0.1", info.getVersion());
		assertEquals("Description config1", info.getDescription());
		assertEquals("https://foo.bar", info.getTermsOfService());
		
		assertNotNull(api.getInfo().getContact());
		assertEquals("Test Contact", api.getInfo().getContact().getName());
		assertEquals("contact@email.com", api.getInfo().getContact().getEmail());
		assertEquals("https://foo.bar/contact", api.getInfo().getContact().getUrl());
		
		assertNotNull(api.getInfo().getLicense());
		assertEquals("My license", api.getInfo().getLicense().getName());
		assertEquals("https://foo.bar/license", api.getInfo().getLicense().getUrl());
		
		assertNotNull(api.getServers());
		assertEquals(1, api.getServers().size());
		Server server = api.getServers().get(0);
		assertNotNull(server);
		assertEquals("https://localhost:8080", server.getUrl());
		assertEquals("Test server", server.getDescription());
		
		assertNotNull(api.getExternalDocs());
		assertEquals("https://foo.bar/docs", api.getExternalDocs().getUrl());
		assertEquals("Ext docs", api.getExternalDocs().getDescription());
		
		assertNotNull(api.getTags());
		assertEquals(1, api.getTags().size());
		Tag tag = api.getTags().get(0);
		assertNotNull(tag);
		assertEquals("MyTag", tag.getName());
		assertEquals("MyTag description", tag.getDescription());
		
	}

}
