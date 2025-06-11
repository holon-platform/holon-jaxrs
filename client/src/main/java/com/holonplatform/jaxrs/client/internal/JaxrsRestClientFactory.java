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
package com.holonplatform.jaxrs.client.internal;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientBuilder;

import com.holonplatform.http.exceptions.RestClientCreationException;
import com.holonplatform.http.rest.RestClient;
import com.holonplatform.http.rest.RestClientFactory;
import com.holonplatform.jaxrs.client.JaxrsRestClient;

/**
 * A {@link RestClientFactory} to provide JAX-RS {@link RestClient} implementations using {@link JaxrsClientRestClient}.
 * 
 * @since 5.0.0
 */
@Priority(RestClientFactory.DEFAULT_PRIORITY)
public class JaxrsRestClientFactory implements RestClientFactory {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.rest.RestClientFactory#getRestClientImplementationClass()
	 */
	@Override
	public Class<?> getRestClientImplementationClass() {
		return JaxrsRestClient.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.rest.RestClientFactory#create(java.lang.ClassLoader)
	 */
	@Override
	public RestClient create(ClassLoader classLoader) throws RestClientCreationException {
		try {
			return new JaxrsClientRestClient(ClientBuilder.newClient());
		} catch (Exception e) {
			throw new RestClientCreationException("Failed to create a JaxrsRestClient", e);
		}
	}

}
