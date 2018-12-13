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
package com.holonplatform.jaxrs.swagger.v3.internal.endpoints;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.jaxrs.swagger.ApiContext;
import com.holonplatform.jaxrs.swagger.annotations.ApiEndpoint;
import com.holonplatform.jaxrs.swagger.exceptions.ApiContextConfigurationException;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.v3.OpenApi;

import io.swagger.v3.core.filter.OpenAPISpecFilter;
import io.swagger.v3.core.filter.SpecFilter;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * Base JAX-RS resource to implement an OpenAPI documentation endpoint.
 *
 * @since 5.2.0
 */
public abstract class AbstractOpenApiEndpoint {

	private static final Logger LOGGER = SwaggerLogger.create();

	protected Response getOpenApi(HttpHeaders headers, Application application, UriInfo uriInfo, String type)
			throws Exception {

		// get context id
		final String contextId = getContextIdOrDefault();

		// build context
		final OpenApiContext openApiContext;
		try {
			openApiContext = OpenApi.contextBuilder().contextId(contextId).application(application)
					.configLocation(getConfigLocation().orElse(null)).build(true);
		} catch (ApiContextConfigurationException ce) {
			LOGGER.error("Failed to build the OpenAPI context for context id [" + contextId + "]", ce);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("Failed to build the OpenAPI context for context id [" + contextId + "]").build();
		}

		// read the OpenAPI definitions
		OpenAPI oas = openApiContext.read();

		// check successful
		if (oas == null) {
			return Response.status(Status.NOT_FOUND)
					.entity("No OpenAPI definition available for context id [" + contextId + "]").build();
		}

		// check pretty print
		boolean pretty = false;
		if (openApiContext.getOpenApiConfiguration() != null
				&& Boolean.TRUE.equals(openApiContext.getOpenApiConfiguration().isPrettyPrint())) {
			pretty = true;
		}

		// check filters
		if (openApiContext.getOpenApiConfiguration() != null
				&& openApiContext.getOpenApiConfiguration().getFilterClass() != null) {
			try {
				OpenAPISpecFilter filterImpl = (OpenAPISpecFilter) Class
						.forName(openApiContext.getOpenApiConfiguration().getFilterClass()).newInstance();
				SpecFilter f = new SpecFilter();
				oas = f.filter(oas, filterImpl, getQueryParams(uriInfo.getQueryParameters()), getCookies(headers),
						getHeaders(headers));
			} catch (Exception e) {
				LOGGER.error("failed to load filter", e);
			}
		}

		// check type
		if (StringUtils.isNotBlank(type) && type.trim().equalsIgnoreCase("yaml")) {
			return Response.status(Response.Status.OK)
					.entity(pretty ? Yaml.pretty(oas) : Yaml.mapper().writeValueAsString(oas)).type("application/yaml")
					.build();
		} else {
			return Response.status(Response.Status.OK)
					.entity(pretty ? Json.pretty(oas) : Json.mapper().writeValueAsString(oas))
					.type(MediaType.APPLICATION_JSON_TYPE).build();
		}
	}

	/**
	 * Get the context id to use for this endpoint.
	 * <p>
	 * By default, the {@link ApiEndpoint} annotation is used, if found on the endpoint class.
	 * </p>
	 * @return the context id, or the default {@link ApiContext#DEFAULT_CONTEXT_ID} value if not available
	 */
	protected String getContextIdOrDefault() {
		return getContextId().orElse(ApiContext.DEFAULT_CONTEXT_ID);
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

	// ------- helpers

	private static Map<String, List<String>> getQueryParams(MultivaluedMap<String, String> params) {
		return Collections.unmodifiableMap(params);
	}

	private static Map<String, String> getCookies(HttpHeaders headers) {
		if (headers != null) {
			return headers.getCookies().entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getValue()));
		}
		return Collections.emptyMap();
	}

	private static Map<String, List<String>> getHeaders(HttpHeaders headers) {
		return Collections.unmodifiableMap(headers.getRequestHeaders());
	}

}
