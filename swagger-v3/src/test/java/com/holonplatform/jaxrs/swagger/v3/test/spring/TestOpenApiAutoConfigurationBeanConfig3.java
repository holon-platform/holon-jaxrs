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

import java.util.Collections;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyAutoConfiguration;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.annotations.ApiConfiguration;
import com.holonplatform.jaxrs.swagger.v3.spring.ResteasySwaggerV3AutoConfiguration;
import com.holonplatform.jaxrs.swagger.v3.test.resources.Resources;
import com.holonplatform.jaxrs.swagger.v3.test.resources.context2.Resource2;
import com.holonplatform.jaxrs.swagger.v3.test.resources.context3.Resource3;
import com.holonplatform.jaxrs.swagger.v3.test.utils.OpenAPIEndpointUtils;

import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestOpenApiAutoConfigurationBeanConfig3 {

	@LocalServerPort
	private int port;

	@ComponentScan(basePackageClasses = Resources.class)
	@SpringBootConfiguration
	@EnableAutoConfiguration(exclude = { ResteasyAutoConfiguration.class, ResteasySwaggerV3AutoConfiguration.class })
	static class Config {

		@Bean
		public SwaggerConfiguration apiConfiguration1() {
			return new ApiConfig1();
		}

		@Bean
		public SwaggerConfiguration apiConfiguration2() {
			return new ApiConfig2();
		}

	}

	@ApiConfiguration(contextId = "test_bean_config_context_3_1", path = "docs1", endpointType = ApiEndpointType.QUERY_PARAMETER)
	private static class ApiConfig1 extends SwaggerConfiguration {

		public ApiConfig1() {
			super();
			setOpenAPI(new OpenAPI().info(new Info().title("Test bean config 3_1").version("1.8.1")));
			setResourcePackages(Collections.singleton(Resource2.class.getPackage().getName()));
		}

	}

	@ApiConfiguration(contextId = "test_bean_config_context_3_2", path = "docs2", endpointType = ApiEndpointType.PATH_PARAMETER)
	private static class ApiConfig2 extends SwaggerConfiguration {

		public ApiConfig2() {
			super();
			setOpenAPI(new OpenAPI().info(new Info().title("Test bean config 3_2").version("1.8.2")));
			setResourcePackages(Collections.singleton(Resource3.class.getPackage().getName()));
		}

	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApi1() {
		final Client client = JerseyClientBuilder.createClient();
		// json
		Response response = client.target("http://localhost:" + port).path("docs1").queryParam("type", "json").request()
				.get();
		OpenAPI api = OpenAPIEndpointUtils.readAsJson(response);
		validate1(api);
		// yaml
		response = client.target("http://localhost:" + port).path("docs1").queryParam("type", "yaml").request().get();
		api = OpenAPIEndpointUtils.readAsYaml(response);
		validate1(api);
	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApi2() {
		final Client client = JerseyClientBuilder.createClient();
		// json
		Response response = client.target("http://localhost:" + port).path("docs2.json").request().get();
		OpenAPI api = OpenAPIEndpointUtils.readAsJson(response);
		validate2(api);
		// yaml
		response = client.target("http://localhost:" + port).path("docs2.yaml").request().get();
		api = OpenAPIEndpointUtils.readAsYaml(response);
		validate2(api);
	}

	private static void validate1(OpenAPI api) {
		assertNotNull(api);
		assertNotNull(api.getPaths());
		assertEquals(1, api.getPaths().size());
		assertNotNull(api.getInfo());
		Info info = api.getInfo();
		assertEquals("Test bean config 3_1", info.getTitle());
		assertEquals("1.8.1", info.getVersion());
	}

	private static void validate2(OpenAPI api) {
		assertNotNull(api);
		assertNotNull(api.getPaths());
		assertEquals(1, api.getPaths().size());
		assertNotNull(api.getInfo());
		Info info = api.getInfo();
		assertEquals("Test bean config 3_2", info.getTitle());
		assertEquals("1.8.2", info.getVersion());
	}

}
