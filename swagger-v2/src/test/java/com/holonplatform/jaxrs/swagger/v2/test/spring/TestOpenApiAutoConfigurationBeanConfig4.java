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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyAutoConfiguration;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.annotations.ApiConfiguration;
import com.holonplatform.jaxrs.swagger.v2.spring.ResteasySwaggerV2AutoConfiguration;
import com.holonplatform.jaxrs.swagger.v2.test.ctxresources2.Ctx2Resource1;
import com.holonplatform.jaxrs.swagger.v2.test.utils.SwaggerEndpointUtils;

import io.swagger.config.SwaggerConfig;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Info;
import io.swagger.models.Swagger;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestOpenApiAutoConfigurationBeanConfig4 {

	@LocalServerPort
	private int port;

	@ComponentScan(basePackageClasses = Ctx2Resource1.class)
	@SpringBootConfiguration
	@EnableAutoConfiguration(exclude = { ResteasyAutoConfiguration.class, ResteasySwaggerV2AutoConfiguration.class })
	static class Config {

		@Bean
		public SwaggerConfig apiConfiguration1() {
			return new ApiConfig1();
		}

		@Bean
		public SwaggerConfig apiConfiguration2() {
			return new ApiConfig2();
		}

	}

	@ApiConfiguration(contextId = "ctx_resource_2_1", path = "docs1", endpointType = ApiEndpointType.QUERY_PARAMETER)
	private static class ApiConfig1 extends BeanConfig {

		public ApiConfig1() {
			super();
			setInfo(new Info().title("Test bean config 4_1").version("1.9.1"));
		}

	}

	@ApiConfiguration(contextId = "ctx_resource_2_2", path = "docs2", endpointType = ApiEndpointType.PATH_PARAMETER)
	private static class ApiConfig2 extends BeanConfig {

		public ApiConfig2() {
			super();
			setInfo(new Info().title("Test bean config 4_2").version("1.9.2"));
		}

	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApi1() {
		final Client client = JerseyClientBuilder.createClient();
		// json
		Response response = client.target("http://localhost:" + port).path("docs1").queryParam("type", "json").request()
				.get();
		Swagger api = SwaggerEndpointUtils.readAsJson(response);
		validate1(api);
		// yaml
		response = client.target("http://localhost:" + port).path("docs1").queryParam("type", "yaml").request().get();
		api = SwaggerEndpointUtils.readAsYaml(response);
		validate1(api);
	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApi2() {
		final Client client = JerseyClientBuilder.createClient();
		// json
		Response response = client.target("http://localhost:" + port).path("docs2.json").request().get();
		Swagger api = SwaggerEndpointUtils.readAsJson(response);
		validate2(api);
		// yaml
		response = client.target("http://localhost:" + port).path("docs2.yaml").request().get();
		api = SwaggerEndpointUtils.readAsYaml(response);
		validate2(api);
	}

	private static void validate1(Swagger api) {
		assertNotNull(api);
		assertNotNull(api.getPaths());
		assertEquals(1, api.getPaths().size());
		assertNotNull(api.getInfo());
		Info info = api.getInfo();
		assertEquals("Test bean config 4_1", info.getTitle());
		assertEquals("1.9.1", info.getVersion());
	}

	private static void validate2(Swagger api) {
		assertNotNull(api);
		assertNotNull(api.getPaths());
		assertEquals(1, api.getPaths().size());
		assertNotNull(api.getInfo());
		Info info = api.getInfo();
		assertEquals("Test bean config 4_2", info.getTitle());
		assertEquals("1.9.2", info.getVersion());
	}

}
