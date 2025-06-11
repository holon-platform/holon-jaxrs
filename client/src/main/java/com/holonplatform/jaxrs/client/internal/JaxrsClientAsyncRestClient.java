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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.client.AsyncInvoker;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.core.Response;

import com.holonplatform.async.http.AsyncRestClient;
import com.holonplatform.async.http.internal.AbstractAsyncRestClient;
import com.holonplatform.async.http.internal.DefaultAsyncRequestDefinition;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.http.HttpMethod;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.http.exceptions.UnsuccessfulResponseException;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.ResponseEntity;
import com.holonplatform.http.rest.ResponseType;
import com.holonplatform.jaxrs.client.JaxrsAsyncRestClient;

/**
 * Default JAX-RS {@link AsyncRestClient} implementation.
 *
 * @since 5.2.0
 */
public class JaxrsClientAsyncRestClient extends AbstractAsyncRestClient implements JaxrsAsyncRestClient {

	/**
	 * JAX-RS client
	 */
	private final Client client;

	/**
	 * Constructor
	 * @param client Jax-rs client
	 */
	public JaxrsClientAsyncRestClient(Client client) {
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
	 * @see com.holonplatform.async.http.internal.AbstractAsyncRestClient#buildDefinition()
	 */
	@Override
	protected AsyncRequestDefinition buildDefinition() {
		return new DefaultAsyncRequestDefinition(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.async.http.internal.AsyncInvoker#invoke(com.holonplatform.async.http.AsyncRestClient.
	 * AsyncRequestDefinition, com.holonplatform.http.HttpMethod, com.holonplatform.http.rest.RequestEntity,
	 * com.holonplatform.http.rest.ResponseType, boolean)
	 */
	@Override
	public <T, R> CompletionStage<ResponseEntity<T>> invoke(AsyncRequestDefinition requestDefinition, HttpMethod method,
			RequestEntity<R> requestEntity, ResponseType<T> responseType, boolean onlySuccessfulStatusCode) {

		// invocation builder
		final Builder builder = JaxrsRestClientOperations.configure(getClient(), requestDefinition).request();

		// headers
		requestDefinition.getHeaders().forEach((n, v) -> builder.header(n, v));

		// invoker
		final AsyncInvoker invoker = builder.async();

		// invoke
		return JaxrsRestClientOperations.buildRequestEntity(requestEntity).map(r -> {
			final CompletableFuture<ResponseEntity<T>> operation = new CompletableFuture<>();
			invoker.method(method.getMethodName(), r,
					new ResponseInvocationCallback<>(operation, responseType, onlySuccessfulStatusCode));
			return operation;
		}).orElseGet(() -> {
			final CompletableFuture<ResponseEntity<T>> operation = new CompletableFuture<>();
			invoker.method(method.getMethodName(),
					new ResponseInvocationCallback<>(operation, responseType, onlySuccessfulStatusCode));
			return operation;
		});
	}

	private final class ResponseInvocationCallback<T> implements InvocationCallback<Response> {

		private final CompletableFuture<ResponseEntity<T>> stage;
		private final ResponseType<T> responseType;
		private final boolean onlySuccessfulStatusCode;

		public ResponseInvocationCallback(CompletableFuture<ResponseEntity<T>> stage, ResponseType<T> responseType,
				boolean onlySuccessfulStatusCode) {
			super();
			this.stage = stage;
			this.responseType = responseType;
			this.onlySuccessfulStatusCode = onlySuccessfulStatusCode;
		}

		/*
		 * (non-Javadoc)
		 * @see jakarta.ws.rs.client.InvocationCallback#completed(java.lang.Object)
		 */
		@Override
		public void completed(Response response) {
			// check error status code
			if (onlySuccessfulStatusCode && !HttpStatus.isSuccessStatusCode(response.getStatus())) {
				stage.completeExceptionally(new UnsuccessfulResponseException(new JaxrsRawResponseEntity(response)));
			} else {
				stage.complete(new JaxrsResponseEntity<>(response, responseType));
			}
		}

		/*
		 * (non-Javadoc)
		 * @see jakarta.ws.rs.client.InvocationCallback#failed(java.lang.Throwable)
		 */
		@Override
		public void failed(Throwable throwable) {
			stage.completeExceptionally(throwable);
		}

	}

}
