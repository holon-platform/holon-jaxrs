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

import java.util.Optional;

import javax.ws.rs.Path;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.ApiContext;
import com.holonplatform.jaxrs.swagger.ApiEndpointBuilder;
import com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration;
import com.holonplatform.jaxrs.swagger.ApiEndpointDefinition;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.v3.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.v3.OpenApi;
import com.holonplatform.jaxrs.swagger.v3.annotations.ApiEndpoint;
import com.holonplatform.jaxrs.swagger.v3.builders.JaxrsOpenApiContextBuilder;
import com.holonplatform.jaxrs.swagger.v3.endpoints.AcceptHeaderOpenApiEndpoint;
import com.holonplatform.jaxrs.swagger.v3.endpoints.PathParamOpenApiEndpoint;
import com.holonplatform.jaxrs.swagger.v3.endpoints.QueryParamOpenApiEndpoint;

import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

/**
 * Default {@link ApiEndpointBuilder} implementation.
 *
 * @since 5.2.0
 */
public enum OpenApiEndpointBuilder implements ApiEndpointBuilder<OpenAPIConfiguration> {

	INSTANCE;

	private static final Logger LOGGER = SwaggerLogger.create();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointBuilder#build(com.holonplatform.jaxrs.swagger.
	 * ApiEndpointConfiguration, boolean)
	 */
	@Override
	public ApiEndpointDefinition build(ApiEndpointConfiguration<? extends OpenAPIConfiguration> configuration,
			boolean initContext) throws ApiConfigurationException {
		ObjectUtils.argumentNotNull(configuration, "ApiEndpointConfiguration must be not null");
		// type
		final ApiEndpointType type = configuration.getType().orElse(ApiEndpointType.getDefault());
		final Class<?> endpointClass;
		switch (type) {
		case ACCEPT_HEADER:
			endpointClass = AcceptHeaderOpenApiEndpoint.class;
			break;
		case PATH_PARAMETER:
			endpointClass = PathParamOpenApiEndpoint.class;
			break;
		case QUERY_PARAMETER:
		default:
			endpointClass = QueryParamOpenApiEndpoint.class;
			break;
		}
		// context id
		final String contextId = getContextId(configuration).orElse(ApiContext.DEFAULT_CONTEXT_ID);
		// path
		final String path = getEndpointPath(configuration).orElse(ApiContext.DEFAULT_API_ENDPOINT_PATH);

		// build API context
		configuration.getConfiguration().ifPresent(cfg -> {
			configuration.getApplication().ifPresent(application -> {
				OpenApi.contextBuilder()
						// context id
						.contextId(contextId)
						// JAX-RS Application
						.application(application)
						// config location
						.configLocation(configuration.getConfigurationLocation().orElse(null))
						// scanner type
						.scannerType(JaxrsScannerType.APPLICATION)
						// build and init
						.build(true);
			});
		});

		// build endpoint class
		final DynamicType.Builder<?> builder = new ByteBuddy().subclass(endpointClass)
				.annotateType(AnnotationDescription.Builder.ofType(Path.class).define("value", path).build())
				.annotateType(AnnotationDescription.Builder.ofType(ApiEndpoint.class).define("value", contextId)
						.define("configLocation", configuration.getConfigurationLocation().orElse(""))
						.define("scannerType", configuration.getApplication().isPresent() ? JaxrsScannerType.APPLICATION
								: JaxrsScannerType.DEFAULT)
						.build());
		final Class<?> endpoint = builder.make()
				.load(configuration.getClassLoader().orElseGet(() -> ClassUtils.getDefaultClassLoader()),
						ClassLoadingStrategy.Default.INJECTION)
				.getLoaded();

		// check init
		if (initContext) {
			final JaxrsOpenApiContextBuilder contextBuilder = OpenApi.contextBuilder().contextId(contextId);
			// configuration
			configuration.getConfiguration().ifPresent(c -> contextBuilder.configuration(c));
			// config location
			configuration.getConfigurationLocation().ifPresent(cl -> contextBuilder.configLocation(cl));
			// JAX-RS Application
			configuration.getApplication().ifPresent(a -> {
				contextBuilder.application(a);
				contextBuilder.scannerType(JaxrsScannerType.APPLICATION);
			});
			contextBuilder.build(true);

			LOGGER.debug(() -> "OpenAPI context inited - context id: " + contextId);
		}

		return ApiEndpointDefinition.create(endpoint, type, path, contextId);
	}

	/**
	 * Get the API context id for given configuration, if available.
	 * @param configuration The API configuration
	 * @return Optional API context id
	 */
	public static Optional<String> getContextId(OpenAPIConfiguration configuration) {
		Optional<String> option = getConfigurationOption(configuration, ApiContext.CONFIGURATION_OPTION_CONTEXT_ID);
		if (option.isPresent()) {
			return option;
		}
		return getSwaggerConfigurationContextId(configuration);
	}

	/**
	 * Get the API context id for given configuration, if available.
	 * @param configuration The API endpoint configuration
	 * @return Optional API context id
	 */
	private static Optional<String> getContextId(
			ApiEndpointConfiguration<? extends OpenAPIConfiguration> configuration) {
		Optional<String> contextId = configuration.getContextId();
		if (contextId.isPresent()) {
			return contextId;
		}
		return configuration.getConfiguration().flatMap(c -> getContextId(c));
	}

	/**
	 * Get the API listing endpoint path for given configuration, if available.
	 * @param configuration The API configuration
	 * @return Optional API listing endpoint path
	 */
	public static Optional<String> getEndpointPath(OpenAPIConfiguration configuration) {
		return getConfigurationOption(configuration, ApiContext.CONFIGURATION_OPTION_PATH);
	}

	/**
	 * Get the API listing endpoint path for given configuration, if available.
	 * @param configuration The API endpoint configuration
	 * @return Optional API listing endpoint path
	 */
	private static Optional<String> getEndpointPath(
			ApiEndpointConfiguration<? extends OpenAPIConfiguration> configuration) {
		Optional<String> path = configuration.getPath();
		if (path.isPresent()) {
			return path;
		}
		return configuration.getConfiguration().flatMap(c -> getEndpointPath(c));
	}

	/**
	 * Get the API configuration option value with given name, if available.
	 * @param <T> Option value type
	 * @param configuration API configuration
	 * @param name Option name
	 * @return Optional value
	 */
	@SuppressWarnings("unchecked")
	private static <T> Optional<T> getConfigurationOption(OpenAPIConfiguration configuration, String name) {
		if (configuration != null) {
			if (configuration.getUserDefinedOptions() != null) {
				Object value = configuration.getUserDefinedOptions().get(name);
				if (value != null) {
					try {
						return Optional.of((T) value);
					} catch (Exception e) {
						LOGGER.warn("The API configuration option [" + name + "] "
								+ "is not conistent with the required type", e);
					}
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Get the context id from the API configuration, if it is of {@link SwaggerConfiguration} type.
	 * @param configuration API configuration
	 * @return Optional context id
	 */
	private static Optional<String> getSwaggerConfigurationContextId(OpenAPIConfiguration configuration) {
		if (configuration instanceof SwaggerConfiguration) {
			return Optional.ofNullable(((SwaggerConfiguration) configuration).getId());
		}
		return Optional.empty();
	}

}
