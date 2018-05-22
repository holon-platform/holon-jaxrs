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
package com.holonplatform.jaxrs.server.security.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.Realm;
import com.holonplatform.http.HttpHeaders;
import com.holonplatform.jaxrs.LogConfig;

public class TestAuth extends JerseyTest {

	public static class NoOpPasswordEncoder implements PasswordEncoder {

		@Override
		public String encode(CharSequence rawPassword) {
			return rawPassword.toString();
		}

		@Override
		public boolean matches(CharSequence rawPassword, String encodedPassword) {
			return rawPassword.toString().equals(encodedPassword);
		}

	}

	@Configuration
	@EnableWebSecurity
	public static class Config extends WebSecurityConfigurerAdapter {

		@Bean
		public static PasswordEncoder passwordEncoder() {
			return new NoOpPasswordEncoder();
		}

		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication()
					// user 1
					.withUser("a1").password("p1").authorities("R1", "R2")
					// user 2
					.and().withUser("a2").password("p2").authorities("R1", "R3");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/public/**").permitAll()
					// everything else is secured
					.anyRequest().authenticated()
					// use basic auth
					.and().httpBasic();
		}

	}

	private static Client client;

	@BeforeClass
	public static void setup() {
		LogConfig.setupLogging();
		client = JerseyClientBuilder.createClient();
	}

	@Override
	protected TestContainerFactory getTestContainerFactory() {
		return new GrizzlyWebTestContainerFactory();
	}

	/*
	 * (non-Javadoc)
	 * @see org.glassfish.jersey.test.JerseyTest#configureDeployment()
	 */
	@Override
	protected DeploymentContext configureDeployment() {
		return ServletDeploymentContext.forServlet(new ServletContainer(configure()))
				.contextParam("contextClass", AnnotationConfigWebApplicationContext.class.getName())
				.contextParam("contextConfigLocation", Config.class.getName()).addListener(ContextLoaderListener.class)
				.addFilter(DelegatingFilterProxy.class, "springSecurityFilterChain").build();
	}

	// Avoid conflict with Resteasy in classpath
	@Override
	protected Client getClient() {
		return client;
	}

	@Override
	protected ResourceConfig configure() {

		final Realm realm = Realm.builder().withDefaultAuthorizer().build();

		return new ResourceConfig().register(RolesAllowedDynamicFeature.class).register(new ContextResolver<Realm>() {

			@Override
			public Realm getContext(Class<?> type) {
				return realm;
			}
		}).register(ProtectedResource.class).register(PublicResource.class).register(SpringSecurityTestResource.class);
	}

	// -------

	@Path("springsecurity")
	public static class SpringSecurityTestResource {

		@GET
		@Path("test")
		@Produces(MediaType.TEXT_PLAIN)
		public String test() {
			return "test";
		}

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

	@SuppressWarnings("resource")
	@Test
	public void testBaseAuth() {

		Response response = target("/springsecurity/test").request()
				.header(HttpHeaders.AUTHORIZATION, buildBasicAuth("a1", "p1")).buildGet().invoke();
		assertNotNull(response);
		assertEquals(200, response.getStatus());
	}

	@SuppressWarnings("resource")
	@Test
	public void testAuth() {

		/*
		 * String value = target("/public/test").request().get(String.class); assertEquals("test", value);
		 */

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

		String value = target("/protected/pub").request().header(HttpHeaders.AUTHORIZATION, buildBasicAuth("a1", "p1"))
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
