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
import com.holonplatform.jaxrs.swagger.v2.test.resources.Resources;
import com.holonplatform.jaxrs.swagger.v2.test.utils.SwaggerEndpointUtils;

import io.swagger.config.SwaggerConfig;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Info;
import io.swagger.models.Swagger;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestOpenApiAutoConfigurationBeanConfig2 {

	@LocalServerPort
	private int port;

	@ComponentScan(basePackageClasses = Resources.class)
	@SpringBootConfiguration
	@EnableAutoConfiguration(exclude = { ResteasyAutoConfiguration.class, ResteasySwaggerV2AutoConfiguration.class })
	static class Config {

		@Bean
		public SwaggerConfig apiConfiguration() {
			return new ApiConfig();
		}

	}

	@ApiConfiguration(contextId = "test_bean_config_context_2", path = "bdocs", endpointType = ApiEndpointType.PATH_PARAMETER)
	private static class ApiConfig extends BeanConfig {

		public ApiConfig() {
			super();
			setInfo(new Info().title("Test bean config 2").version("1.7.1"));
		}

	}

	@SuppressWarnings("resource")
	@Test
	public void testOpenApi() {
		final Client client = JerseyClientBuilder.createClient();
		// json
		Response response = client.target("http://localhost:" + port).path("bdocs.json").request().get();
		Swagger api = SwaggerEndpointUtils.readAsJson(response);
		validate(api);
		// yaml
		response = client.target("http://localhost:" + port).path("bdocs.yaml").request().get();
		api = SwaggerEndpointUtils.readAsYaml(response);
		validate(api);
	}

	public static void validate(Swagger api) {
		assertNotNull(api);
		assertNotNull(api.getPaths());
		assertEquals(25, api.getPaths().size());

		assertNotNull(api.getInfo());
		Info info = api.getInfo();
		assertEquals("Test bean config 2", info.getTitle());
		assertEquals("1.7.1", info.getVersion());

	}

}
