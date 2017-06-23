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

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import com.holonplatform.http.HttpResponse;
import com.holonplatform.http.ResponseType;

/**
 * JAX-RS {@link HttpResponse} implementation.
 *
 * @since 5.0.0
 */
public class JaxrsHttpResponse<T> implements HttpResponse<T> {

	private final Response response;
	private final ResponseType<T> type;

	public JaxrsHttpResponse(Response response, ResponseType<T> type) {
		super();
		this.response = response;
		this.type = type;
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
	@SuppressWarnings("unchecked")
	@Override
	public Optional<T> getPayload() throws UnsupportedOperationException {
		T payload = null;
		if (type.isSimpleType()) {
			payload = response.readEntity((Class<T>) type.getType());
		} else {
			payload = response.readEntity(new GenericType<T>(type.getType()));
		}
		return Optional.ofNullable(payload);
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

}
