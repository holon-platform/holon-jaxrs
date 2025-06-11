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
package com.holonplatform.jaxrs.client.internal;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import com.holonplatform.http.MediaType;
import com.holonplatform.http.internal.HttpUtils;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.RestClientOperations.RequestConfiguration;

/**
 * JAX-RS RestClient operations support.
 *
 * @since 5.2.0
 */
public final class JaxrsRestClientOperations {

	private static final String APPLICATION_FORM_URLENCODED_MEDIA_TYPE = MediaType.APPLICATION_FORM_URLENCODED
			.toString();

	private JaxrsRestClientOperations() {
	}

	/**
	 * Configure a JAX-RS {@link WebTarget} using given request configuration.
	 * @param client JAX-RS client
	 * @param request Request configuration
	 * @return Configured WebTarget
	 */
	public static WebTarget configure(Client client, RequestConfiguration<?> request) {
		WebTarget target = client.target(request.getRequestURI());
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

	/**
	 * Build a jax-rs {@link Entity} from given request entity
	 * @param requestEntity Request entity
	 * @return jax-rs Entity
	 */
	public static Optional<Entity<?>> buildRequestEntity(RequestEntity<?> requestEntity) {
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
