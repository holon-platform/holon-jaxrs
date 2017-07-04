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
import java.util.Map.Entry;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.http.HttpMethod;
import com.holonplatform.http.HttpResponse;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.http.MediaType;
import com.holonplatform.http.RequestEntity;
import com.holonplatform.http.ResponseType;
import com.holonplatform.http.RestClient;
import com.holonplatform.http.internal.AbstractRestClient;
import com.holonplatform.http.internal.DefaultRequestDefinition;
import com.holonplatform.http.internal.HttpUtils;
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.RestClient.Invoker#invoke(com.holonplatform.http.RestClient.RequestDefinition,
	 * com.holonplatform.http.HttpMethod, com.holonplatform.http.RequestEntity, com.holonplatform.http.ResponseType)
	 */
	@Override
	public <T, R> HttpResponse<T> invoke(RequestDefinition requestDefinition, HttpMethod method,
			RequestEntity<R> requestEntity, ResponseType<T> responseType) {

		// invocation builder
		final Builder builder = configure(requestDefinition).request();
		// headers
		requestDefinition.getHeaders().forEach((n, v) -> builder.header(n, v));

		// invocation
		final javax.ws.rs.client.Invocation invocation = buildRequestEntity(requestEntity)
				.map(r -> builder.build(method.getMethodName(), r)).orElse(builder.build(method.getMethodName()));

		// invoke
		Response response = null;
		try {
			response = invocation.invoke();
		} catch (Exception e) {
			throw new RestClientException(e);
		}

		if (response == null) {
			throw new RestClientException("Invocation returned a null Response");
		}

		// check error status code
		if (HttpStatus.isErrorStatusCode(response.getStatus())) {
			throw new UnsuccessfulInvocationException(new JaxrsHttpResponse<>(response, responseType));
		}

		return new JaxrsHttpResponse<>(response, responseType);
	}

	/**
	 * Configure a JAX-RS {@link WebTarget} using given request definition
	 * @param request Request definition
	 * @return Configured WebTarget
	 */
	protected WebTarget configure(RequestDefinition request) {
		WebTarget target = getClient().target(request.getRequestURI());
		// template parameters
		target = target.resolveTemplates(request.getTemplateParameters());
		// query parameters
		for (Entry<String, Object[]> qp : request.getQueryParameters().entrySet()) {
			target = target.queryParam(qp.getKey(), qp.getValue());
		}
		// property set
		final WebTarget configuredTarget = target;
		request.getPropertySet().ifPresent(ps -> configuredTarget.register(new PropertyBoxReaderInterceptor(ps)));
		// done
		return configuredTarget;
	}

	private static final String APPLICATION_FORM_URLENCODED_MEDIA_TYPE = MediaType.APPLICATION_FORM_URLENCODED
			.toString();

	/**
	 * Build a jax-rs {@link Entity} from given request entity
	 * @param requestEntity Request entity
	 * @return jax-rs Entity
	 */
	protected Optional<Entity<?>> buildRequestEntity(RequestEntity<?> requestEntity) {
		if (requestEntity != null) {
			boolean form = requestEntity.getMediaType().map(m -> APPLICATION_FORM_URLENCODED_MEDIA_TYPE.equals(m))
					.orElse(Boolean.FALSE);
			return requestEntity.getPayload().map(p -> form ? Entity.form(convert(HttpUtils.getAsMultiMap(p)))
					: Entity.entity(p, requestEntity.getMediaType().orElse(null)));
		}
		return Optional.empty();
	}

	/**
	 * Convert the given map into {@link MultivaluedMap}.
	 * @param data Map to convert
	 * @return Converted map
	 */
	private static MultivaluedMap<String, String> convert(Map<String, List<String>> data) {
		if (data != null) {
			MultivaluedMap<String, String> mvm = new MultivaluedHashMap<>();
			for (Entry<String, List<String>> entry : data.entrySet()) {
				mvm.put(entry.getKey(), entry.getValue());
			}
			return mvm;
		}
		return null;
	}

}
