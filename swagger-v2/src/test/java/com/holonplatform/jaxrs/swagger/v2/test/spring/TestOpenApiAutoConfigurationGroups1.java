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
import com.holonplatform.jaxrs.swagger.v2.test.utils.SwaggerValidation;

import io.swagger.models.Info;
import io.swagger.models.Swagger;

@ActiveProfiles("groups1")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestOpenApiAutoConfigurationGroups1 {

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

		// group1
		Response response = client.target("http://localhost:" + port).path("docs1").queryParam("type", "json").request()
				.get();
		Swagger api = SwaggerEndpointUtils.readAsJson(response);
		validate(api, "The API group 1", "/resource2/test21");
		// group2
		response = client.target("http://localhost:" + port).path("docs2").queryParam("type", "yaml").request().get();
		api = SwaggerEndpointUtils.readAsYaml(response);
		validate(api, "The API group 2", "/resource3/test31");
	}

	public static void validate(Swagger api, String description, String path) {
		assertNotNull(api);
		assertNotNull(api.getPaths());
		assertEquals(1, api.getPaths().size());

		assertNotNull(api.getInfo());
		Info info = api.getInfo();
		assertEquals("Title groups1", info.getTitle());
		assertEquals("0.1.2", info.getVersion());
		assertEquals(description, info.getDescription());

		SwaggerValidation.validateModel1(SwaggerValidation.validateOperationResponse(api, path));

	}

}
