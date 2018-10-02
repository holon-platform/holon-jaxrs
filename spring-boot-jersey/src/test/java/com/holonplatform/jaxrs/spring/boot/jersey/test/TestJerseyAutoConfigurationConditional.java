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
package com.holonplatform.jaxrs.spring.boot.jersey.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.holonplatform.jaxrs.spring.boot.jersey.test.resources.TestEndpoint;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
public class TestJerseyAutoConfigurationConditional {

	@Configuration
	@EnableAutoConfiguration
	@ComponentScan(basePackageClasses = TestEndpoint.class)
	static class Config {

		@Bean
		public ResourceConfig jerseyConfig() {
			ResourceConfig cfg = new ResourceConfig();
			cfg.property("test", "customConfig");
			return cfg;
		}

	}

	@Autowired
	private ResourceConfig jerseyConfig;

	@Test
	public void testConfig() {
		assertNotNull(jerseyConfig);
		assertEquals("customConfig", jerseyConfig.getProperty("test"));
	}

	@Test
	public void testEndpoint() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8888/test").path("ping");
		String response = target.request().get(String.class);
		assertEquals("pong", response);
	}

}
