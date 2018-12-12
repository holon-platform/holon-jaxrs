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
package com.holonplatform.jaxrs.swagger.v3.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.holonplatform.jaxrs.LogConfig;
import com.holonplatform.jaxrs.swagger.v3.test.model.AbstractTestResource;
import com.holonplatform.test.JerseyTest5;

import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

public class TestJerseyPropertyBoxModelConverter extends JerseyTest5 {

	private static Client client;

	@BeforeAll
	static void setup() {
		LogConfig.setupLogging();
		client = JerseyClientBuilder.createClient();
	}

	@Path("resource1")
	static class TestResource1 extends AbstractTestResource {

	}

	@Override
	protected Application configure() {

		Set<Class<?>> classes = new HashSet<>();
		classes.add(TestResource1.class);

		ResourceConfig config = new ResourceConfig();
		classes.forEach(c -> config.register(c));

		OpenAPI oas = new OpenAPI();
		Info info = new Info().title("Swagger Sample App bootstrap code")
				.description("This is a sample server Petstore server.  You can find out more about Swagger "
						+ "at [http://swagger.io](http://swagger.io) or on [irc.freenode.net, #swagger](http://swagger.io/irc/).  For this sample, "
						+ "you can use the api key `special-key` to test the authorization filters.")
				.termsOfService("http://swagger.io/terms/").contact(new Contact().email("apiteam@swagger.io"))
				.license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html"));

		oas.info(info);
		SwaggerConfiguration oasConfig = new SwaggerConfiguration().openAPI(oas).prettyPrint(true)
				.resourceClasses(classes.stream().map(c -> c.getName()).collect(Collectors.toSet()));

		try {
			new JaxrsOpenApiContextBuilder()
					// .servletConfig(servletConfig)
					.application(config).openApiConfiguration(oasConfig).buildContext(true);
		} catch (OpenApiConfigurationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return config;
	}

	@Override
	protected Client getClient() {
		return client;
	}

	@Test
	public void testPropertyBoxModel() {
		assertNotNull(getClient());
	}

}
