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

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyAutoConfiguration;
import com.holonplatform.jaxrs.swagger.v3.spring.ResteasySwaggerV3AutoConfiguration;
import com.holonplatform.jaxrs.swagger.v3.test.ctxresources4.Ctx4Resource1;
import com.holonplatform.jaxrs.swagger.v3.test.utils.OpenAPIEndpointUtils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestOpenApiAutoConfigurationGroups4Legacy {

	@LocalServerPort
	private int port;

	@ComponentScan(basePackageClasses = Ctx4Resource1.class)
	@SpringBootConfiguration
	@EnableAutoConfiguration(exclude = { ResteasyAutoConfiguration.class, ResteasySwaggerV3AutoConfiguration.class })
	static class Config {

	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApi() {
		final Client client = JerseyClientBuilder.createClient();
		// group1
		Response response = client.target("http://localhost:" + port).path("ctxlgy4docs1").queryParam("type", "json")
				.request().get();
		OpenAPI api = OpenAPIEndpointUtils.readAsJson(response);
		validate(api, "Group1", "/resource1/test");
		// group2
		response = client.target("http://localhost:" + port).path("ctxlgy4docs2").queryParam("type", "yaml").request()
				.get();
		api = OpenAPIEndpointUtils.readAsYaml(response);
		validate(api, "Group2", "/resource2/test");
	}

	public static void validate(OpenAPI api, String title, String path) {
		assertNotNull(api);
		assertNotNull(api.getPaths());
		assertEquals(1, api.getPaths().size());

		assertNotNull(api.getInfo());
		Info info = api.getInfo();
		assertEquals(title, info.getTitle());

		PathItem item = api.getPaths().get(path);
		assertNotNull(item);
		Operation op = item.getGet();
		assertNotNull(op);

	}

}
