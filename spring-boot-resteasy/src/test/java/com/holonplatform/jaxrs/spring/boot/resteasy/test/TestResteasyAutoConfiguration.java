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
package com.holonplatform.jaxrs.spring.boot.resteasy.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfig;
import com.holonplatform.jaxrs.spring.boot.resteasy.test.beans.TestBeanEndpoint;
import com.holonplatform.jaxrs.spring.boot.resteasy.test.beans.TestService;
import com.holonplatform.jaxrs.spring.boot.resteasy.test.resources.TestEndpoint;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class TestResteasyAutoConfiguration {

	@Configuration
	@EnableAutoConfiguration(exclude = JsonbAutoConfiguration.class)
	@ComponentScan(basePackageClasses = TestBeanEndpoint.class)
	static class Config {

		@Bean
		public TestService testService() {
			return new TestService();
		}

		@Bean
		public ResteasyConfig resteasyConfig() {
			ResteasyConfig cfg = new ResteasyConfig();
			cfg.register(TestEndpoint.class);
			return cfg;
		}

	}

	@Autowired
	private ResteasyConfig resteasyConfig;

	@LocalServerPort
	private int port;

	@Test
	public void testConfig() {
		Assert.assertNotNull(resteasyConfig);
	}

	@Test
	public void testEndpoint() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:" + port + "/test").path("ping");
		String response = target.request().get(String.class);
		Assert.assertEquals("pong", response);
	}

	@Test
	public void testEndpoint2() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:" + port + "/test2").path("ping");
		String response = target.request().get(String.class);
		Assert.assertEquals("pung", response);
	}

}
