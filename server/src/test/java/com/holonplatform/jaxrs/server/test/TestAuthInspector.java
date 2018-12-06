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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import org.junit.jupiter.api.Test;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.AuthenticationInspector;
import com.holonplatform.auth.AuthenticationToken;
import com.holonplatform.auth.Authenticator;
import com.holonplatform.auth.Permission;
import com.holonplatform.auth.Realm;
import com.holonplatform.auth.exceptions.AuthenticationException;
import com.holonplatform.auth.exceptions.UnknownAccountException;
import com.holonplatform.auth.token.AccountCredentialsToken;
import com.holonplatform.jaxrs.server.auth.JaxrsAuthenticationInspector;
import com.holonplatform.jaxrs.server.internal.auth.AuthSecurityContext;

public class TestAuthInspector {

	@Test
	public void testAuthInspectorWithAuthContext() {

		final Permission p1 = Permission.create("p1");
		final Permission p2 = Permission.create("p2");
		final Permission p3 = Permission.create("p3");

		final Authenticator<AccountCredentialsToken> authenticator = new Authenticator<AccountCredentialsToken>() {

			@Override
			public Class<? extends AccountCredentialsToken> getTokenType() {
				return AccountCredentialsToken.class;
			}

			@Override
			public Authentication authenticate(AccountCredentialsToken authenticationToken)
					throws AuthenticationException {
				if ("usr".equals(authenticationToken.getPrincipal())) {
					return Authentication.builder("usr").withPermission(p1).withPermission(p2).build();
				}
				throw new UnknownAccountException("usr");
			}
		};

		final AuthContext ctx = AuthContext
				.create(Realm.builder().withAuthenticator(authenticator).withDefaultAuthorizer().build());

		final SecurityContext sc = new AuthSecurityContext(ctx, false);

		AuthenticationInspector i = JaxrsAuthenticationInspector.of(sc);

		assertNotNull(i);
		assertFalse(i.isAuthenticated());

		ctx.authenticate(AuthenticationToken.accountCredentials("usr", "pwd"));

		assertTrue(i.isAuthenticated());
		assertTrue(i.getAuthentication().isPresent());
		assertEquals("usr", i.getAuthentication().get().getName());

		assertTrue(i.isPermitted("p1"));
		assertTrue(i.isPermitted("p1", "p2"));
		assertTrue(i.isPermittedAny("p1", "p2"));
		assertTrue(i.isPermittedAny("p1", "p3"));
		assertFalse(i.isPermitted("p3"));

		assertTrue(i.isPermitted(p1));
		assertTrue(i.isPermitted(p1, p2));
		assertTrue(i.isPermittedAny(p1, p2));
		assertTrue(i.isPermittedAny(p1, p3));
		assertFalse(i.isPermitted(p3));
	}

	@Test
	public void testBaseAuthInspector() {

		final Permission p1 = Permission.create("p1");
		final Permission p2 = Permission.create("p2");
		final Permission p3 = Permission.create("p3");

		SecurityContext sc = new SecurityContext() {

			@Override
			public boolean isUserInRole(String role) {
				return "p1".equals(role) || "p2".equals(role);
			}

			@Override
			public boolean isSecure() {
				return false;
			}

			@Override
			public Principal getUserPrincipal() {
				return new Principal() {

					@Override
					public String getName() {
						return "usr";
					}
				};
			}

			@Override
			public String getAuthenticationScheme() {
				return null;
			}
		};

		AuthenticationInspector i = JaxrsAuthenticationInspector.of(sc);

		assertNotNull(i);

		assertTrue(i.isAuthenticated());

		assertTrue(i.isPermitted("p1"));
		assertTrue(i.isPermitted("p1", "p2"));
		assertTrue(i.isPermittedAny("p1", "p2"));
		assertTrue(i.isPermittedAny("p1", "p3"));
		assertFalse(i.isPermitted("p3"));

		assertTrue(i.isPermitted(p1));
		assertTrue(i.isPermitted(p1, p2));
		assertTrue(i.isPermittedAny(p1, p2));
		assertTrue(i.isPermittedAny(p1, p3));
		assertFalse(i.isPermitted(p3));

		sc = new SecurityContext() {

			@Override
			public boolean isUserInRole(String role) {
				return "p1".equals(role) || "p2".equals(role);
			}

			@Override
			public boolean isSecure() {
				return false;
			}

			@Override
			public Principal getUserPrincipal() {
				return Authentication.builder("usr").build();
			}

			@Override
			public String getAuthenticationScheme() {
				return null;
			}
		};

		i = JaxrsAuthenticationInspector.of(sc);

		assertNotNull(i);

		assertTrue(i.isAuthenticated());
		assertTrue(i.getAuthentication().isPresent());
		assertEquals("usr", i.getAuthentication().get().getName());

		assertTrue(i.isPermitted("p1"));
		assertTrue(i.isPermitted("p1", "p2"));
		assertTrue(i.isPermittedAny("p1", "p2"));
		assertTrue(i.isPermittedAny("p1", "p3"));
		assertFalse(i.isPermitted("p3"));

		assertTrue(i.isPermitted(p1));
		assertTrue(i.isPermitted(p1, p2));
		assertTrue(i.isPermittedAny(p1, p2));
		assertTrue(i.isPermittedAny(p1, p3));
		assertFalse(i.isPermitted(p3));

	}

}
