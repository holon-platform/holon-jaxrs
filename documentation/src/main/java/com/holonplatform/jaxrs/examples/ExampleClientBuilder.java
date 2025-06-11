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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import jakarta.ws.rs.client.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.holonplatform.http.rest.RestClient;
import com.holonplatform.jaxrs.spring.boot.JaxrsClientBuilder;
import com.holonplatform.jaxrs.spring.boot.JaxrsClientCustomizer;

@SuppressWarnings("unused")
public class ExampleClientBuilder {

	// tag::clientbuilder1[]
	@SpringBootApplication
	static class Application {

		@Bean
		public JaxrsClientCustomizer propertyCustomizer() { // <1>
			return cb -> cb.property("test.jaxrs.client.customizers", "test");
		}

		@Bean
		public JaxrsClientCustomizer sslCustomizer() throws KeyManagementException, NoSuchAlgorithmException { // <2>
			// setup a SSLContext with a "trust all" manager
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
				// customize ClientBuilder
				cb.sslContext(sslcontext).hostnameVerifier((s1, s2) -> true);
			};
		}

		public static void main(String[] args) {
			SpringApplication.run(Application.class, args);
		}

	}
	// end::clientbuilder1[]

	// tag::clientbuilder2[]
	@Autowired
	private JaxrsClientBuilder clientBuilder;

	private void getClient() {
		Client jaxrsClient = clientBuilder.build(); // <1>

		RestClient restClient = RestClient.create(); // <2>
	}
	// end::clientbuilder2[]

}
