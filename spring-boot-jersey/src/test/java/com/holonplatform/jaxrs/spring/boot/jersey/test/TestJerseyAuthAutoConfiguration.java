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

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.auth.Account;
import com.holonplatform.auth.AuthenticationToken;
import com.holonplatform.auth.Credentials;
import com.holonplatform.auth.Realm;
import com.holonplatform.http.HttpHeaders;
import com.holonplatform.jaxrs.spring.boot.jersey.test.authresources.TestAuthEndpoint;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestJerseyAuthAutoConfiguration {

	@LocalServerPort
	private int port;

	@Configuration
	@EnableAutoConfiguration
	@ComponentScan(basePackageClasses = TestAuthEndpoint.class)
	static class Config {

		@Bean
		public Realm realm() {
			return Realm.builder()
					// HTTP Basic authorization schema resolver
					.withResolver(AuthenticationToken.httpBasicResolver())
					// authenticator
					.withAuthenticator(Account.authenticator(id -> {
						if ("test".equals(id)) {
							return Optional.of(Account.builder(id)
									.credentials(Credentials.builder().secret("test").build()).build());
						}
						return Optional.empty();
					}))
					// default authorizer
					.withDefaultAuthorizer().build();
		}

	}

	@Test
	public void testEndpoint() throws UnsupportedEncodingException {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:" + port + "/testauth").path("ping");
		String response = target.request()
				.header(HttpHeaders.AUTHORIZATION,
						HttpHeaders.SCHEME_BASIC + " "
								+ Base64.getEncoder().encodeToString(new String("test:test").getBytes("ISO-8859-1")))
				.get(String.class);
		assertEquals("pong", response);
	}

}
