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
package com.holonplatform.jaxrs.client.internal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.http.HttpMethod;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.http.exceptions.HttpClientInvocationException;
import com.holonplatform.http.exceptions.UnsuccessfulResponseException;
import com.holonplatform.http.internal.rest.AbstractRestClient;
import com.holonplatform.http.internal.rest.DefaultRequestDefinition;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.ResponseEntity;
import com.holonplatform.http.rest.ResponseType;
import com.holonplatform.http.rest.RestClient;
import com.holonplatform.jaxrs.client.JaxrsRestClient;

/**
 * Default JAX-RS {@link RestClient} implementation.
 *
 * @since 5.0.0
 */
public class JaxrsClientRestClient extends AbstractRestClient implements JaxrsRestClient {

	/**
	 * JAX-RS client
	 */
	private final Client client;

	/**
	 * Constructor
	 * @param client Jax-rs client
	 */
	public JaxrsClientRestClient(Client client) {
		super();
		ObjectUtils.argumentNotNull(client, "Client must be not null");
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.client.JaxrsRestClient#getClient()
	 */
	@Override
	public Client getClient() {
		return client;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.internal.AbstractRestClient#buildDefinition()
	 */
	@Override
	protected RequestDefinition buildDefinition() {
		return new DefaultRequestDefinition(this);
	}

	@SuppressWarnings("resource")
	@Override
	public <T, R> ResponseEntity<T> invoke(RequestDefinition requestDefinition, HttpMethod method,
			RequestEntity<R> requestEntity, ResponseType<T> responseType, boolean onlySuccessfulStatusCode) {

		// invocation builder
		final Builder builder = JaxrsRestClientOperations.configure(getClient(), requestDefinition).request();
		// headers
		requestDefinition.getHeaders().forEach((n, v) -> builder.header(n, v));

		// invocation
		final javax.ws.rs.client.Invocation invocation = JaxrsRestClientOperations.buildRequestEntity(requestEntity)
				.map(r -> builder.build(method.getMethodName(), r)).orElse(builder.build(method.getMethodName()));

		// invoke
		Response response = null;
		try {
			response = invocation.invoke();
		} catch (Exception e) {
			throw new HttpClientInvocationException(e);
		}

		if (response == null) {
			throw new HttpClientInvocationException("Invocation returned a null Response");
		}

		// check error status code
		if (onlySuccessfulStatusCode && !HttpStatus.isSuccessStatusCode(response.getStatus())) {
			throw new UnsuccessfulResponseException(new JaxrsRawResponseEntity(response));
		}

		return new JaxrsResponseEntity<>(response, responseType);
	}

}
