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
package com.holonplatform.jaxrs.client.reactor.internal;

import javax.annotation.Priority;
import javax.ws.rs.client.ClientBuilder;

import com.holonplatform.async.http.AsyncRestClientFactory;
import com.holonplatform.http.exceptions.RestClientCreationException;
import com.holonplatform.jaxrs.client.reactor.JaxrsReactiveRestClient;
import com.holonplatform.reactor.http.ReactiveRestClient;
import com.holonplatform.reactor.http.ReactiveRestClientFactory;

/**
 * A {@link ReactiveRestClientFactory} to provide JAX-RS {@link ReactiveRestClient} implementations using
 * {@link ReactiveRestClientFactory}.
 * 
 * @since 5.2.0
 */
@Priority(AsyncRestClientFactory.DEFAULT_PRIORITY)
public class JaxrsReactiveRestClientFactory implements ReactiveRestClientFactory {

	@Override
	public Class<?> getRestClientImplementationClass() {
		return JaxrsReactiveRestClient.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveRestClientFactory#create(java.lang.ClassLoader)
	 */
	@Override
	public ReactiveRestClient create(ClassLoader classLoader) throws RestClientCreationException {
		try {
			return new JaxrsClientReactiveRestClient(ClientBuilder.newClient());
		} catch (Exception e) {
			throw new RestClientCreationException("Failed to create a Jaxrs ReactiveRestClient", e);
		}
	}

}
