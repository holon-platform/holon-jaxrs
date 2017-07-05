/*
 * Copyright 2000-2016 Holon TDCN.
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.http.exceptions.HttpEntityProcessingException;
import com.holonplatform.http.rest.ResponseEntity;
import com.holonplatform.http.rest.ResponseType;

/**
 * JAX-RS {@link ResponseEntity} implementation.
 * 
 * @param <T> Response entity type
 *
 * @since 5.0.0
 */
public class JaxrsResponseEntity<T> implements ResponseEntity<T> {

	/**
	 * Actual jax-rs response
	 */
	private final Response response;

	/**
	 * Response type
	 */
	private final ResponseType<T> type;

	/**
	 * Constructor
	 * @param response JAX-RS Response (not null)
	 * @param type Response type (not null)
	 */
	public JaxrsResponseEntity(Response response, ResponseType<T> type) {
		super();
		ObjectUtils.argumentNotNull(response, "JAX-RS Response must be not null");
		ObjectUtils.argumentNotNull(type, "Response type must be not null");
		this.response = response;
		this.type = type;
	}

	/**
	 * Get the JAX-RS response.
	 * @return the JAX-RS response
	 */
	protected Response getResponse() {
		return response;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.HttpResponse#getStatusCode()
	 */
	@Override
	public int getStatusCode() {
		return response.getStatus();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.messaging.MessageHeaders#getHeaders()
	 */
	@Override
	public Map<String, List<String>> getHeaders() {
		return response.getStringHeaders();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.messaging.Message#getPayload()
	 */
	@Override
	public Optional<T> getPayload() throws UnsupportedOperationException {
		return readAs(type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.messaging.Message#getPayloadType()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends T> getPayloadType() throws UnsupportedOperationException {
		return (Class<? extends T>) type.getType();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.rest.ResponseEntity#as(java.lang.Class)
	 */
	@Override
	public <E> Optional<E> as(Class<E> entityType) {
		ObjectUtils.argumentNotNull(entityType, "Entity type must be not null");
		return readAs(ResponseType.of(entityType));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.rest.ResponseEntity#as(com.holonplatform.http.rest.ResponseType)
	 */
	@Override
	public <E> Optional<E> as(ResponseType<E> entityType) {
		return readAs(entityType);
	}

	/**
	 * Read the message entity as an instance of the type represented by given <code>type</code> {@link ResponseType}.
	 * @param <E> Response entity type
	 * @param type Response entity type to read
	 * @return the message entity converted to given type, or an empty Optional for empty or zero-length responses
	 * @throws HttpEntityProcessingException If a entity processing error occurred (e.g. no message body reader
	 *         available for the requested type)
	 */
	@SuppressWarnings("unchecked")
	protected <E> Optional<E> readAs(ResponseType<E> type) {
		ObjectUtils.argumentNotNull(type, "Response type must be not null");
		if (response.hasEntity() && Void.class != type.getType()) {
			try {
				return Optional.ofNullable(type.isSimpleType() ? response.readEntity((Class<E>) type.getType())
						: response.readEntity(new GenericType<E>(type.getType())));
			} catch (Exception e) {
				// check zero-lenght response content
				if (isNoContentException(e)) {
					return Optional.empty();
				}
				throw new HttpEntityProcessingException("Failed to read HTTP entity as [" + type + "]", e);
			}
		}
		return Optional.empty();
	}

	/**
	 * Checks whether given exception is a {@link ProcessingException} that wraps a message body reader
	 * {@link NoContentException}.
	 * @param exception Exception to check
	 * @return <code>true</code> if given it a no content exception
	 */
	protected static boolean isNoContentException(Exception exception) {
		if (exception != null && exception instanceof ProcessingException && exception.getCause() != null) {
			return exception.getCause() instanceof NoContentException;
		}
		return false;
	}

}
