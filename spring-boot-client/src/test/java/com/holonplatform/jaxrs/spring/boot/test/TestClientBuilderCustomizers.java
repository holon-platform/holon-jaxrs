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
package com.holonplatform.jaxrs.spring.boot.test;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.holonplatform.http.rest.RestClient;
import com.holonplatform.jaxrs.client.JaxrsRestClient;
import com.holonplatform.jaxrs.spring.boot.JaxrsClientBuilder;
import com.holonplatform.jaxrs.spring.boot.JaxrsClientCustomizer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ActiveProfiles("ssl")
public class TestClientBuilderCustomizers {

	@Path("test")
	public static class TestEndpoint {

		@GET
		@Path("ping")
		@Produces(MediaType.TEXT_PLAIN)
		public String ping() {
			return "pong";
		}

	}

	@Configuration
	@EnableAutoConfiguration
	static class Config {

		@Bean
		public ResourceConfig jerseyConfig() {
			ResourceConfig cfg = new ResourceConfig();
			cfg.register(TestEndpoint.class);
			return cfg;
		}

		@Bean
		public JaxrsClientCustomizer propertyCustomizer() {
			return cb -> cb.property("test.customizers", "test");
		}

		@Bean
		public JaxrsClientCustomizer sslCustomizer() throws KeyManagementException, NoSuchAlgorithmException {
			final SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

			} }, new java.security.SecureRandom());

			return cb -> {
				cb.sslContext(sslcontext).hostnameVerifier((s1, s2) -> true);
			};
		}

	}

	@Autowired
	private JaxrsClientBuilder clientBuilder;

	@Test
	public void testSslEndpoint() {
		Client client = clientBuilder.build();

		Assert.assertEquals("test", client.getConfiguration().getProperty("test.customizers"));

		WebTarget target = client.target("https://localhost:8443/test").path("ping");
		String response = target.request().get(String.class);
		Assert.assertEquals("pong", response);
	}

	@Test
	public void testClientFactory() {
		RestClient client = RestClient.create();
		Assert.assertTrue(client instanceof JaxrsRestClient);

		Assert.assertEquals("test",
				((JaxrsRestClient) client).getClient().getConfiguration().getProperty("test.customizers"));

		String response = client.request().target("https://localhost:8443/test").path("ping").getForEntity(String.class)
				.orElse(null);
		Assert.assertEquals("pong", response);
	}

}
