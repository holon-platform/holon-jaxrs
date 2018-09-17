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

import javax.annotation.Priority;
import javax.ws.rs.client.ClientBuilder;

import com.holonplatform.async.http.AsyncRestClient;
import com.holonplatform.async.http.AsyncRestClientFactory;
import com.holonplatform.http.exceptions.RestClientCreationException;
import com.holonplatform.jaxrs.client.JaxrsAsyncRestClient;

/**
 * A {@link AsyncRestClientFactory} to provide JAX-RS {@link AsyncRestClient} implementations using
 * {@link JaxrsClientAsyncRestClient}.
 * 
 * @since 5.2.0
 */
@Priority(AsyncRestClientFactory.DEFAULT_PRIORITY)
public class JaxrsAsyncRestClientFactory implements AsyncRestClientFactory {

	@Override
	public Class<?> getRestClientImplementationClass() {
		return JaxrsAsyncRestClient.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.async.http.AsyncRestClientFactory#create(java.lang.ClassLoader)
	 */
	@Override
	public AsyncRestClient create(ClassLoader classLoader) throws RestClientCreationException {
		try {
			return new JaxrsClientAsyncRestClient(ClientBuilder.newClient());
		} catch (Exception e) {
			throw new RestClientCreationException("Failed to create a Jaxrs AsyncRestClient", e);
		}
	}

}
