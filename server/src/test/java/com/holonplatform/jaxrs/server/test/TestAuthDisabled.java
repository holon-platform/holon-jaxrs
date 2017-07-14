/*
 * Copyright 2000-2016 Holon TDCN.
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
package com.holonplatform.jaxrs.server.test;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;

import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.http.HttpHeaders;
import com.holonplatform.jaxrs.LogConfig;
import com.holonplatform.jaxrs.server.auth.AuthenticationFeature;

public class TestAuthDisabled extends JerseyTest {

	private static Client client;

	@BeforeClass
	public static void setup() {
		LogConfig.setupLogging();
		client = JerseyClientBuilder.createClient();
	}

	@Authenticate(schemes = HttpHeaders.SCHEME_BASIC)
	@Path("protected")
	public static class ProtectedResource {

		@GET
		@Path("test")
		@Produces(MediaType.TEXT_PLAIN)
		public String test() {
			return "test";
		}

	}

	@Override
	protected Application configure() {

		return new ResourceConfig().register(ProtectedResource.class)
				.property(AuthenticationFeature.DISABLE_AUTHENTICATION, "disabled");
	}

	// Avoid conflict with Resteasy in classpath
	@Override
	protected Client getClient() {
		return client;
	}

	@Test
	public void testAuth() {
		String value = target("/protected/test").request().get(String.class);
		assertEquals("test", value);
	}

}