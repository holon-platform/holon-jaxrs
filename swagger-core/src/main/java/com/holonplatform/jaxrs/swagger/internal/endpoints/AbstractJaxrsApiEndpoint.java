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
package com.holonplatform.jaxrs.swagger.internal.endpoints;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.jaxrs.swagger.ApiDefaults;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;

/**
 * Base JAX-RS resource to implement an API documentation endpoint.
 * 
 * @param <M> API model type
 *
 * @since 5.2.0
 */
public abstract class AbstractJaxrsApiEndpoint<M> {

	/**
	 * Logger
	 */
	protected static final Logger LOGGER = SwaggerLogger.create();

	/**
	 * API definition output types enumeration.
	 */
	protected enum OutputType {

		/**
		 * Json
		 */
		JSON("application/json"),

		/**
		 * Yaml
		 */
		YAML("application/yaml");

		private final String mediaType;

		private OutputType(String mediaType) {
			this.mediaType = mediaType;
		}

		/**
		 * Get the media type.
		 * @return the media type
		 */
		public String getMediaType() {
			return mediaType;
		}

	}

	/**
	 * API definition.
	 *
	 * @param <M> API model type
	 */
	protected interface ApiDefinition<M> {

		/**
		 * Get the API definition.
		 * @return the API definition (may be <code>null</code>)
		 */
		M getApi();

		/**
		 * Whether to pretty format the API output.
		 * @return Whether to pretty format the API output
		 */
		boolean isPretty();

		/**
		 * Create a new {@link ApiDefinition}.
		 * @param <M> API model type
		 * @param api The API model
		 * @param pretty Whether to pretty format the API output
		 * @return A new {@link ApiDefinition}
		 */
		static <M> ApiDefinition<M> create(M api, boolean pretty) {
			return new DefaultApiDefinition<>(api, pretty);
		}

	}

	/**
	 * Get the API definition.
	 * @param contextId The API context id (not null)
	 * @param application JAX-RS Application reference
	 * @param headers JAX-RS Headers reference
	 * @param uriInfo JAX-RS URI info reference
	 * @return The API definition
	 * @throws Exception If an error occurred
	 */
	protected abstract ApiDefinition<M> getApi(String contextId, Application application, HttpHeaders headers,
			UriInfo uriInfo) throws Exception;

	/**
	 * Serialize the API definition model.
	 * @param outputType Output type
	 * @param api The API model
	 * @param pretty Whether to pretty format the API output
	 * @return The serialized API definition model
	 * @throws Exception If an error occurred
	 */
	protected abstract String getApiOutput(OutputType outputType, M api, boolean pretty) throws Exception;

	/**
	 * Get the API response.
	 * @param application JAX-RS Application reference
	 * @param headers JAX-RS Headers reference
	 * @param uriInfo JAX-RS URI info reference
	 * @param type API definition output type (<code>json</code> or <code>yaml</code>)
	 * @return The API response
	 */
	protected Response getApi(Application application, HttpHeaders headers, UriInfo uriInfo, String type) {
		return getApi(application, headers, uriInfo, getOutputType(type));
	}

	/**
	 * Get the API response.
	 * @param application JAX-RS Application reference
	 * @param headers JAX-RS Headers reference
	 * @param uriInfo JAX-RS URI info reference
	 * @param outputType API definition output type
	 * @return The API response
	 */
	protected Response getApi(Application application, HttpHeaders headers, UriInfo uriInfo, OutputType outputType) {

		// API context id
		final String contextId = getContextIdOrDefault();

		try {
			// get the API definition
			final ApiDefinition<M> api = getApi(contextId, application, headers, uriInfo);

			// check not null
			if (api.getApi() == null) {
				return Response.status(Status.NOT_FOUND)
						.entity("No API definition available for context id [" + contextId + "]").build();
			}

			// serialize the API definition
			return Response.status(Response.Status.OK).type(outputType.getMediaType())
					.entity(getApiOutput(outputType, api.getApi(), api.isPretty())).build();

		} catch (Exception e) {
			LOGGER.error("Failed to provide the API definition for context id [" + contextId + "]", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("Failed to provide the API definition for context id [" + contextId + "] - Error: ["
							+ ExceptionUtils.getRootCauseMessage(e) + "]")
					.build();
		}
	}

	/**
	 * Get the context id to use for this endpoint.
	 * <p>
	 * By default, the {@link ApiEndpoint} annotation is used, if found on the endpoint class.
	 * </p>
	 * @return the context id, or the default {@link ApiDefaults#DEFAULT_CONTEXT_ID} value if not available
	 */
	protected String getContextIdOrDefault() {
		return getContextId().orElse(ApiDefaults.DEFAULT_CONTEXT_ID);
	}

	/**
	 * Get the context id to use for this endpoint, if available.
	 * <p>
	 * By default, the {@link ApiEndpoint} annotation is used, if found on the endpoint class.
	 * </p>
	 * @return Optional context id
	 */
	protected Optional<String> getContextId() {
		if (getClass().isAnnotationPresent(ApiEndpoint.class)) {
			return Optional
					.ofNullable(AnnotationUtils.getStringValue(getClass().getAnnotation(ApiEndpoint.class).value()));
		}
		return Optional.empty();
	}

	/**
	 * Get the config location to use for this endpoint, if available.
	 * <p>
	 * By default, the {@link ApiEndpoint} annotation is used, if found on the endpoint class.
	 * </p>
	 * @return Optional config location
	 */
	protected Optional<String> getConfigLocation() {
		if (getClass().isAnnotationPresent(ApiEndpoint.class)) {
			return Optional.ofNullable(
					AnnotationUtils.getStringValue(getClass().getAnnotation(ApiEndpoint.class).configLocation()));
		}
		return Optional.empty();
	}

	/**
	 * Get the {@link JaxrsScannerType} to use for this endpoint, if available.
	 * <p>
	 * By default, the {@link ApiEndpoint} annotation is used, if found on the endpoint class.
	 * </p>
	 * @return Optional {@link JaxrsScannerType}
	 */
	protected Optional<JaxrsScannerType> getJaxrsScannerType() {
		if (getClass().isAnnotationPresent(ApiEndpoint.class)) {
			return Optional.ofNullable(getClass().getAnnotation(ApiEndpoint.class).scannerType());
		}
		return Optional.empty();
	}

	/**
	 * Get a cookie name-value map.
	 * @param headers The headers
	 * @return Cookie name-value map
	 */
	protected static Map<String, String> getCookies(HttpHeaders headers) {
		if (headers != null) {
			return headers.getCookies().entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getValue()));
		}
		return Collections.emptyMap();
	}

	/**
	 * Get the API output type for given type parameter.
	 * @param type The type parameter
	 * @return the API output type
	 */
	protected static OutputType getOutputType(String type) {
		if (type != null && "yaml".equalsIgnoreCase(type.trim())) {
			return OutputType.YAML;
		}
		return OutputType.JSON;
	}

	/**
	 * Default {@link ApiDefinition} implementation.
	 * 
	 * @param <M> API model type
	 */
	protected static class DefaultApiDefinition<M> implements ApiDefinition<M> {

		private final M api;
		private final boolean pretty;

		public DefaultApiDefinition(M api, boolean pretty) {
			super();
			this.api = api;
			this.pretty = pretty;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.jaxrs.swagger.internal.endpoints.AbstractJaxrsApiEndpoint.ApiDefinition#getApi()
		 */
		@Override
		public M getApi() {
			return api;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.jaxrs.swagger.internal.endpoints.AbstractJaxrsApiEndpoint.ApiDefinition#isPretty()
		 */
		@Override
		public boolean isPretty() {
			return pretty;
		}

	}

}
