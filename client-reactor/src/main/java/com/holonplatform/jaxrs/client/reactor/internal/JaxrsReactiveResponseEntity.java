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

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.Response;

import com.holonplatform.http.rest.ResponseType;
import com.holonplatform.jaxrs.client.internal.JaxrsResponseEntity;
import com.holonplatform.reactor.http.ReactiveResponseEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * JAX-RS {@link ReactiveResponseEntity} implementation.
 * 
 * @param <T> Response entity type
 *
 * @since 5.2.0
 */
public class JaxrsReactiveResponseEntity<T> extends JaxrsResponseEntity<T> implements ReactiveResponseEntity<T> {

	public JaxrsReactiveResponseEntity(Response response, ResponseType<T> type) {
		super(response, type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveResponseEntity#asMono()
	 */
	@Override
	public Mono<T> asMono() {
		return Mono.justOrEmpty(getPayload());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveResponseEntity#asMono(java.lang.Class)
	 */
	@Override
	public <E> Mono<E> asMono(Class<E> entityType) {
		return Mono.justOrEmpty(as(entityType));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveResponseEntity#asMono(com.holonplatform.http.rest.ResponseType)
	 */
	@Override
	public <E> Mono<E> asMono(ResponseType<E> entityType) {
		return Mono.justOrEmpty(as(entityType));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveResponseEntity#asFlux(java.lang.Class)
	 */
	@Override
	public <E> Flux<E> asFlux(Class<E> entityType) {
		final ResponseType<List<E>> rt = ResponseType.of(entityType, List.class);
		return as(rt).map(r -> Flux.fromIterable(r)).orElse(Flux.empty());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.http.ReactiveResponseEntity#asInputStream()
	 */
	@Override
	public Mono<InputStream> asInputStream() {
		return Mono.justOrEmpty(getResponse().readEntity(InputStream.class));
	}

}
