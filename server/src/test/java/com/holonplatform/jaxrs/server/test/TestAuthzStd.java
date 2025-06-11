/*
 * Copyright 2016-2017 Axioma srl.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Optional;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.ContextResolver;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.holonplatform.auth.Account;
import com.holonplatform.auth.Account.AccountProvider;
import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.AuthenticationToken;
import com.holonplatform.auth.Credentials;
import com.holonplatform.auth.Realm;
import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.http.HttpHeaders;
import com.holonplatform.jaxrs.LogConfig;
import com.holonplatform.test.JerseyTest5;

public class TestAuthzStd extends JerseyTest5 {

	private static Client client;

	@BeforeAll
	static void setup() {
		LogConfig.setupLogging();
		client = JerseyClientBuilder.createClient();
	}

	@Path("public")
	public static class PublicResource {

		@GET
		@Path("test")
		@Produces(MediaType.TEXT_PLAIN)
		public String test() {
			return "test";
		}

	}

	@Path("semiprotected")
	public static class SemiProtectedResource {

		@GET
		@Path("test")
		@Produces(MediaType.TEXT_PLAIN)
		public String test() {
			return "test";
		}

		@Authenticate(schemes = HttpHeaders.SCHEME_BASIC)
		@RolesAllowed("R1")
		@GET
		@Path("r1")
		@Produces(MediaType.TEXT_PLAIN)
		public String r1() {
			return "R1";
		}

	}

	@Authenticate(schemes = HttpHeaders.SCHEME_BASIC)
	@Path("protected")
	public static class ProtectedResource {

		@PermitAll
		@GET
		@Path("pub")
		@Produces(MediaType.TEXT_PLAIN)
		public String pub() {
			return "pub";
		}

		@RolesAllowed("R1")
		@GET
		@Path("r1")
		@Produces(MediaType.TEXT_PLAIN)
		public String r1() {
			return "r1";
		}

		@RolesAllowed("R2")
		@GET
		@Path("r2")
		@Produces(MediaType.TEXT_PLAIN)
		public String r2() {
			return "r2";
		}

		@RolesAllowed("R3")
		@GET
		@Path("r3")
		@Produces(MediaType.TEXT_PLAIN)
		public String r3() {
			return "r3";
		}

		@RolesAllowed({ "R1", "R3" })
		@GET
		@Path("r13")
		@Produces(MediaType.TEXT_PLAIN)
		public String r13() {
			return "r13";
		}

		@DenyAll
		@GET
		@Path("prv")
		@Produces(MediaType.TEXT_PLAIN)
		public String prv() {
			return "Denied";
		}

		@PermitAll
		@GET
		@Path("name")
		@Produces(MediaType.TEXT_PLAIN)
		public String info(@Context SecurityContext securityContext) {
			Authentication authc = (Authentication) securityContext.getUserPrincipal();
			return authc.getName();
		}

	}

	@Override
	protected Application configure() {

		final AccountProvider provider = id -> {
			Account account = null;
			if ("a1".equals(id)) {
				account = Account.builder(id).credentials(Credentials.builder().secret("p1").build()).enabled(true)
						.withPermission("R1").withPermission("R2").build();
			} else if ("a2".equals(id)) {
				account = Account.builder(id).credentials(Credentials.builder().secret("p2").build()).enabled(true)
						.withPermission("R1").withPermission("R3").build();
			}
			return Optional.ofNullable(account);
		};

		final Realm realm = Realm.builder().withResolver(AuthenticationToken.httpBasicResolver())
				.withAuthenticator(Account.authenticator(provider)).withDefaultAuthorizer().build();

		return new ResourceConfig().register(RolesAllowedDynamicFeature.class).register(new ContextResolver<Realm>() {

			@Override
			public Realm getContext(Class<?> type) {
				return realm;
			}
		}).register(ProtectedResource.class).register(SemiProtectedResource.class).register(PublicResource.class);
	}

	// Avoid conflict with Resteasy in classpath
	@Override
	protected Client getClient() {
		return client;
	}

	@SuppressWarnings("resource")
	@Test
	public void testAuth() {

		String value = target("/public/test").request().get(String.class);
		assertEquals("test", value);

		value = target("/semiprotected/test").request().get(String.class);
		assertEquals("test", value);

		Response response = target("/protected/pub").request().buildGet().invoke();
		assertNotNull(response);
		assertEquals(401, response.getStatus());
		assertNotNull(response.getHeaderString(HttpHeaders.WWW_AUTHENTICATE));

		response = target("/protected/pub").request().header(HttpHeaders.AUTHORIZATION, buildBasicAuth("a1", "wrong"))
				.buildGet().invoke();
		assertNotNull(response);
		assertEquals(401, response.getStatus());

		response = target("/protected/pub").request().header(HttpHeaders.AUTHORIZATION, buildBasicAuth("a1", "p1"))
				.buildGet().invoke();
		assertNotNull(response);
		assertEquals(200, response.getStatus());

		value = target("/protected/pub").request().header(HttpHeaders.AUTHORIZATION, buildBasicAuth("a1", "p1"))
				.get(String.class);
		assertEquals("pub", value);

		value = target("/protected/name").request().header(HttpHeaders.AUTHORIZATION, buildBasicAuth("a1", "p1"))
				.get(String.class);
		assertEquals("a1", value);

		value = target("/protected/r1").request().header(HttpHeaders.AUTHORIZATION, buildBasicAuth("a1", "p1"))
				.get(String.class);
		assertEquals("r1", value);

		value = target("/protected/r2").request().header(HttpHeaders.AUTHORIZATION, buildBasicAuth("a1", "p1"))
				.get(String.class);
		assertEquals("r2", value);

		value = target("/protected/r13").request().header(HttpHeaders.AUTHORIZATION, buildBasicAuth("a1", "p1"))
				.get(String.class);
		assertEquals("r13", value);

		response = target("/protected/r3").request().header(HttpHeaders.AUTHORIZATION, buildBasicAuth("a1", "p1"))
				.buildGet().invoke();
		assertNotNull(response);
		assertEquals(403, response.getStatus());

		response = target("/protected/prv").request().header(HttpHeaders.AUTHORIZATION, buildBasicAuth("a1", "p1"))
				.buildGet().invoke();
		assertNotNull(response);
		assertEquals(403, response.getStatus());

		value = target("/protected/r3").request().header(HttpHeaders.AUTHORIZATION, buildBasicAuth("a2", "p2"))
				.get(String.class);
		assertEquals("r3", value);
	}

	private static String buildBasicAuth(String username, String password) {
		try {
			return HttpHeaders.SCHEME_BASIC + " " + Base64.getEncoder()
					.encodeToString(new String((username + ":" + password)).getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
