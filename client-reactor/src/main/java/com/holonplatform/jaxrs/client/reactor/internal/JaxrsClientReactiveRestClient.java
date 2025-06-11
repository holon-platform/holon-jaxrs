/*
 * Copyright 2016-2018 Axioma srl.
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

import java.util.Optional;

import jakarta.ws.rs.client.AsyncInvoker;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.core.Response;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.http.HttpMethod;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.http.exceptions.UnsuccessfulResponseException;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.ResponseType;
import com.holonplatform.jaxrs.client.internal.JaxrsRawResponseEntity;
import com.holonplatform.jaxrs.client.internal.JaxrsRestClientOperations;
import com.holonplatform.jaxrs.client.reactor.JaxrsReactiveRestClient;
import com.holonplatform.reactor.http.ReactiveResponseEntity;
import com.holonplatform.reactor.http.ReactiveRestClient;
import com.holonplatform.reactor.http.internal.AbstractReactiveRestClient;
import com.holonplatform.reactor.http.internal.DefaultReactiveRequestDefinition;

import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

/**
 * Default JAX-RS {@link ReactiveRestClient} implementation.
 *
 * @since 5.2.0
 */
public class JaxrsClientReactiveRestClient extends AbstractReactiveRestClient implements JaxrsReactiveRestClient {

	/**
	 * JAX-RS client
	 */
	private final Client client;

	/**
	 * Constructor
	 * @param client Jax-rs client
	 */
	public JaxrsClientReactiveRestClient(Client client) {
		super();
		ObjectUtils.argumentNotNull(client, "Client must be not null");
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.client.reactor.JaxrsReactiveRestClient#getClient()
	 */
	@Override
	public Client getClient() {
		return client;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.internal.AbstractReactiveRestClient#buildDefinition()
	 */
	@Override
	protected ReactiveRequestDefinition buildDefinition() {
		return new DefaultReactiveRequestDefinition(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.reactor.http.internal.ReactiveInvoker#invoke(com.holonplatform.reactor.http.ReactiveRestClient.
	 * ReactiveRequestDefinition, com.holonplatform.http.HttpMethod, com.holonplatform.http.rest.RequestEntity,
	 * com.holonplatform.http.rest.ResponseType, boolean)
	 */
	@Override
	public <T, R> Mono<ReactiveResponseEntity<T>> invoke(ReactiveRequestDefinition requestDefinition, HttpMethod method,
			RequestEntity<R> requestEntity, ResponseType<T> responseType, boolean onlySuccessfulStatusCode) {

		// invocation builder
		final Builder builder = JaxrsRestClientOperations.configure(getClient(), requestDefinition).request();

		// headers
		requestDefinition.getHeaders().forEach((n, v) -> builder.header(n, v));

		// invoker
		final AsyncInvoker invoker = builder.async();

		return Mono.<ReactiveResponseEntity<T>>create(sink -> {
			Optional<Entity<?>> entity = JaxrsRestClientOperations.buildRequestEntity(requestEntity);
			if (entity.isPresent()) {
				invoker.method(method.getMethodName(), entity.get(),
						new ResponseInvocationCallback<>(sink, responseType, onlySuccessfulStatusCode));
			} else {
				invoker.method(method.getMethodName(),
						new ResponseInvocationCallback<>(sink, responseType, onlySuccessfulStatusCode));
			}
		});
	}

	private final class ResponseInvocationCallback<T> implements InvocationCallback<Response> {

		private final MonoSink<ReactiveResponseEntity<T>> sink;
		private final ResponseType<T> responseType;
		private final boolean onlySuccessfulStatusCode;

		public ResponseInvocationCallback(MonoSink<ReactiveResponseEntity<T>> stage, ResponseType<T> responseType,
				boolean onlySuccessfulStatusCode) {
			super();
			this.sink = stage;
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
				sink.error(new UnsuccessfulResponseException(new JaxrsRawResponseEntity(response)));
			} else {
				sink.success(new JaxrsReactiveResponseEntity<>(response, responseType));
			}
		}

		/*
		 * (non-Javadoc)
		 * @see jakarta.ws.rs.client.InvocationCallback#failed(java.lang.Throwable)
		 */
		@Override
		public void failed(Throwable throwable) {
			sink.error(throwable);
		}

	}

}
