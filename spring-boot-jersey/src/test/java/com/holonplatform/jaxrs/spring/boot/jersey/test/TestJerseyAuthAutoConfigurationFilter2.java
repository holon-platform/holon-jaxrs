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
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import com.holonplatform.jaxrs.spring.boot.jersey.test.resources.TestEndpoint;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("filter2")
public class TestJerseyAuthAutoConfigurationFilter2 {

	@LocalServerPort
	private int port;

	@Configuration
	@EnableAutoConfiguration
	@ComponentScan(basePackageClasses = TestEndpoint.class)
	static class Config {

	}

	@Autowired
	private ResourceConfig jerseyConfig;

	@Test
	public void testConfig() {
		assertTrue((Boolean) jerseyConfig.getProperty(ServletProperties.FILTER_FORWARD_ON_404));
	}

	@Test
	public void testEndpoint() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:" + port + "/test").path("ping");
		String response = target.request().get(String.class);
		assertEquals("pong", response);
	}

}
