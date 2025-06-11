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

import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.exception.ExceptionUtils;
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
import com.holonplatform.auth.exceptions.AuthenticationException;
import com.holonplatform.auth.jwt.JwtAuthenticator;
import com.holonplatform.auth.jwt.JwtConfiguration;
import com.holonplatform.auth.jwt.JwtSignatureAlgorithm;
import com.holonplatform.auth.jwt.JwtTokenBuilder;
import com.holonplatform.http.HttpHeaders;
import com.holonplatform.jaxrs.LogConfig;
import com.holonplatform.jaxrs.server.ResourceUtils;
import com.holonplatform.jaxrs.server.auth.AuthenticationFeature;
import com.holonplatform.test.JerseyTest5;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Providers;

public class TestAuthJwt extends JerseyTest5 {

	@BeforeAll
	static void setup() {
		LogConfig.setupLogging();
	}

	@Path("authorize")
	public static class AuthorizationEndpoint {

		@Context
		private Providers providers;

		@POST
		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		@Produces(MediaType.TEXT_PLAIN)
		public Response jwt(@FormParam("id") String accountId, @FormParam("pwd") String password) {

			// realm
			Realm realm = ResourceUtils.lookupResource(getClass(), Realm.class, providers)
					.orElseThrow(() -> new InternalServerErrorException("Realm not available"));

			// authenticate
			Authentication authc;
			try {
				authc = realm.authenticate(AuthenticationToken.accountCredentials(accountId, password));
			} catch (AuthenticationException ae) {
				return Response.status(Status.UNAUTHORIZED).entity(ExceptionUtils.getRootCauseMessage(ae)).build();
			}

			// configuration
			JwtConfiguration configuration = ResourceUtils.lookupResource(getClass(), JwtConfiguration.class, providers)
					.orElseThrow(() -> new InternalServerErrorException("JWT configuration not available"));

			// build JWT
			String jwt = JwtTokenBuilder.get().buildJwt(configuration, authc, UUID.randomUUID().toString());
			// ok
			return Response.ok(jwt, MediaType.TEXT_PLAIN).build();

		}

	}

	@Authenticate(schemes = HttpHeaders.SCHEME_BEARER)
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

		final SecretKey key = Jwts.SIG.HS256.key().build();

		final JwtConfiguration cfg = JwtConfiguration.builder().issuer("AuthIssuer").includeDetails(true)
				.includePermissions(true).signatureAlgorithm(JwtSignatureAlgorithm.HS256).sharedKey(key.getEncoded())
				.build();

		final Realm realm = Realm.builder().withResolver(AuthenticationToken.httpBearerResolver())
				.withAuthenticator(Account.authenticator(provider))
				.withAuthenticator(JwtAuthenticator.builder().configuration(cfg).issuer("AuthIssuer").build())
				.withDefaultAuthorizer().build();

		return new ResourceConfig().register(AuthenticationFeature.class).register(RolesAllowedDynamicFeature.class)
				// context
				.register(new ContextResolver<JwtConfiguration>() {

					@Override
					public JwtConfiguration getContext(Class<?> type) {
						return cfg;
					}
				}).register(new ContextResolver<Realm>() {

					@Override
					public Realm getContext(Class<?> type) {
						return realm;
					}
				})
				// endpoints
				.register(AuthorizationEndpoint.class).register(ProtectedResource.class);
	}

	// Test

	@SuppressWarnings("resource")
	@Test
	public void testAuth() {

		Response response = target("/protected/pub").request().buildGet().invoke();
		assertNotNull(response);
		assertEquals(401, response.getStatus());
		assertNotNull(response.getHeaderString(HttpHeaders.WWW_AUTHENTICATE));

		// obtain jwt token
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
		formData.putSingle("id", "a1");
		formData.putSingle("pwd", "p1");
		String token = target("/authorize").request().post(Entity.form(formData), String.class);

		response = target("/protected/pub").request().header(HttpHeaders.AUTHORIZATION, buildBearerAuth(token))
				.buildGet().invoke();
		assertNotNull(response);
		assertEquals(200, response.getStatus());

		String value = target("/protected/pub").request().header(HttpHeaders.AUTHORIZATION, buildBearerAuth(token))
				.get(String.class);
		assertEquals("pub", value);

		value = target("/protected/name").request().header(HttpHeaders.AUTHORIZATION, buildBearerAuth(token))
				.get(String.class);
		assertEquals("a1", value);

		value = target("/protected/r1").request().header(HttpHeaders.AUTHORIZATION, buildBearerAuth(token))
				.get(String.class);
		assertEquals("r1", value);

		value = target("/protected/r2").request().header(HttpHeaders.AUTHORIZATION, buildBearerAuth(token))
				.get(String.class);
		assertEquals("r2", value);

		value = target("/protected/r13").request().header(HttpHeaders.AUTHORIZATION, buildBearerAuth(token))
				.get(String.class);
		assertEquals("r13", value);

		response = target("/protected/r3").request().header(HttpHeaders.AUTHORIZATION, buildBearerAuth(token))
				.buildGet().invoke();
		assertNotNull(response);
		assertEquals(403, response.getStatus());

		response = target("/protected/prv").request().header(HttpHeaders.AUTHORIZATION, buildBearerAuth(token))
				.buildGet().invoke();
		assertNotNull(response);
		assertEquals(403, response.getStatus());

		// obtain jwt token
		formData = new MultivaluedHashMap<>();
		formData.putSingle("id", "a2");
		formData.putSingle("pwd", "p2");
		token = target("/authorize").request().post(Entity.form(formData), String.class);

		value = target("/protected/r3").request().header(HttpHeaders.AUTHORIZATION, buildBearerAuth(token))
				.get(String.class);
		assertEquals("r3", value);
	}

	private static String buildBearerAuth(String token) {
		return HttpHeaders.SCHEME_BEARER + " " + token;
	}

}
