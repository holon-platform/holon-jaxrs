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
package com.holonplatform.jaxrs.examples;

import java.util.Optional;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.ContextResolver;

import com.holonplatform.auth.Account;
import com.holonplatform.auth.Account.AccountProvider;
import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.AuthenticationToken;
import com.holonplatform.auth.Credentials;
import com.holonplatform.auth.Realm;
import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.core.Context;
import com.holonplatform.http.HttpHeaders;
import com.holonplatform.jaxrs.server.auth.JaxrsAuthenticationInspector;

@SuppressWarnings("unused")
public class ExampleAuth {

	// tag::auth[]
	@Authenticate(schemes = HttpHeaders.SCHEME_BASIC) // <1>
	@Path("protected")
	class ProtectedResource {

		@GET
		@Path("test")
		@Produces(MediaType.TEXT_PLAIN)
		public String test() {
			return "test";
		}

	}

	@Path("semiprotected")
	class SemiProtectedResource { // <2>

		@GET
		@Path("public")
		@Produces(MediaType.TEXT_PLAIN)
		public String publicMethod() { // <3>
			return "public";
		}

		@Authenticate(schemes = HttpHeaders.SCHEME_BASIC) // <4>
		@GET
		@Path("protected")
		@Produces(MediaType.TEXT_PLAIN)
		public String protectedMethod() {
			return "protected";
		}

	}

	// configuration
	public void configureJaxrsApplication() {

		AccountProvider provider = id -> { // <5>
			// a test provider wich always returns an Account with given id and s3cr3t as password
			return Optional.ofNullable(Account.builder(id).credentials(Credentials.builder().secret("s3cr3t").build())
					.enabled(true).build());
		};

		Realm realm = Realm.builder() // <6>
				.withResolver(AuthenticationToken.httpBasicResolver()) // <7>
				.withAuthenticator(Account.authenticator(provider)) // <8>
				.withDefaultAuthorizer().build();

		ContextResolver<Realm> realmContextResolver = new ContextResolver<Realm>() { // <9>

			@Override
			public Realm getContext(Class<?> type) {
				return realm;
			}
		};

		register(realmContextResolver); // <10>
	}
	// end::auth[]

	// tag::realm1[]
	class RealmContextResolver implements ContextResolver<Realm> {

		@Override
		public Realm getContext(Class<?> type) {
			return Realm.builder() //
					.withResolver(AuthenticationToken.httpBasicResolver()) // <1>
					.withAuthenticator(Account.authenticator(getAccountProvider())) // <2>
					.withDefaultAuthorizer() // <3>
					.build();
		}

	}
	// end::realm1[]

	public void contextResourceRealm() {
		// tag::realm2[]
		Context.get().classLoaderScope() // <1>
				.map(s -> s.put(Realm.CONTEXT_KEY,
						Realm.builder().withResolver(AuthenticationToken.httpBasicResolver())
								.withAuthenticator(Account.authenticator(getAccountProvider())).withDefaultAuthorizer()
								.build()));
		// end::realm2[]
	}

	// tag::authinsp[]
	@Authenticate
	@GET
	@Path("name")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPrincipalName(@jakarta.ws.rs.core.Context SecurityContext securityContext) {
		JaxrsAuthenticationInspector inspector = JaxrsAuthenticationInspector.of(securityContext); // <1>

		boolean isAuthenticated = inspector.isAuthenticated(); // <2>
		Optional<Authentication> auth = inspector.getAuthentication(); // <3>
		Authentication authc = inspector.requireAuthentication(); // <4>

		boolean permitted = inspector.isPermitted("ROLE1"); // <5>
		permitted = inspector.isPermittedAny("ROLE1", "ROLE2"); // <6>

		return inspector.getAuthentication().map(a -> a.getName()).orElse(null);
	}
	// end::authinsp[]

	private static AccountProvider getAccountProvider() {
		return null;
	}

	private void register(Class<?> resource) {

	}

	private void register(Object resource) {

	}

}
