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
package com.holonplatform.jaxrs.client.reactor;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import com.holonplatform.jaxrs.client.reactor.internal.JaxrsClientReactiveRestClient;
import com.holonplatform.reactor.http.ReactiveRestClient;

/**
 * An {@link ReactiveRestClient} using a JAX-RS {@link Client} to perform invocations.
 *
 * @since 5.2.0
 */
public interface JaxrsReactiveRestClient extends ReactiveRestClient {

	/**
	 * Get the JAX-RS Client bound to this {@link ReactiveRestClient}.
	 * @return the JAX-RS Client
	 */
	Client getClient();

	/**
	 * Create a {@link ReactiveRestClient} using given JAX-RS <code>client</code>.
	 * @param client JAX-RS {@link Client} to perform invocations (not null)
	 * @return A new {@link ReactiveRestClient} instance
	 */
	static ReactiveRestClient create(Client client) {
		return new JaxrsClientReactiveRestClient(client);
	}

	/**
	 * Create a {@link ReactiveRestClient} using the default client builder implementation class provided by the JAX-RS
	 * implementation provider.
	 * @return A new {@link ReactiveRestClient} instance
	 */
	static ReactiveRestClient create() {
		return new JaxrsClientReactiveRestClient(ClientBuilder.newClient());
	}

}
