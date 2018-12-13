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
package com.holonplatform.jaxrs.swagger.v3.internal.builders;

import java.util.Optional;

import javax.ws.rs.Path;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.ApiContext;
import com.holonplatform.jaxrs.swagger.ApiEndpointBuilder;
import com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.exceptions.ApiContextConfigurationException;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.v3.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.v3.OpenApi;
import com.holonplatform.jaxrs.swagger.v3.annotations.ApiEndpoint;
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
	 * ApiEndpointConfiguration)
	 */
	@Override
	public Class<?> build(ApiEndpointConfiguration<? extends OpenAPIConfiguration> configuration)
			throws ApiContextConfigurationException {
		ObjectUtils.argumentNotNull(configuration, "ApiEndpointConfiguration must be not null");
		// type
		final Class<?> endpointClass;
		switch (configuration.getType().orElse(ApiEndpointType.QUERY_PARAMETER)) {
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
		Optional<String> ctxId = configuration.getContextId();
		if (!ctxId.isPresent()) {
			// check option
			ctxId = getConfigurationOption(configuration, ApiContext.CONFIGURATION_OPTION_CONTEXT_ID);
			if (!ctxId.isPresent()) {
				// check swagger id
				ctxId = getSwaggerConfigurationContextId(configuration);
			}
		}
		final String contextId = ctxId.orElse(ApiContext.DEFAULT_CONTEXT_ID);
		// path
		String path = configuration.getPath().orElse(ApiContext.DEFAULT_API_ENDPOINT_PATH);

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
		DynamicType.Builder<?> builder = new ByteBuddy().subclass(endpointClass)
				.annotateType(AnnotationDescription.Builder.ofType(Path.class).define("value", path).build())
				.annotateType(AnnotationDescription.Builder.ofType(ApiEndpoint.class).define("value", contextId)
						.define("configLocation", configuration.getConfigurationLocation().orElse(""))
						.define("scannerType", JaxrsScannerType.APPLICATION).build());
		return builder.make().load(configuration.getClassLoader().orElseGet(() -> ClassUtils.getDefaultClassLoader()),
				ClassLoadingStrategy.Default.INJECTION).getLoaded();
	}

	/**
	 * Get the API configuration option value with given name, if available.
	 * @param <T> Option value type
	 * @param configuration API endpoint configuration
	 * @param name Option name
	 * @return Optional value
	 */
	@SuppressWarnings("unchecked")
	private static <T> Optional<T> getConfigurationOption(
			ApiEndpointConfiguration<? extends OpenAPIConfiguration> configuration, String name) {
		return configuration.getConfiguration().map(cfg -> {
			if (cfg.getUserDefinedOptions() != null) {
				Object value = cfg.getUserDefinedOptions().get(name);
				if (value != null) {
					try {
						return (T) value;
					} catch (Exception e) {
						LOGGER.warn("The API configuration option [" + name + "] "
								+ "is not conistent with the required type", e);
					}
				}
			}
			return null;
		});
	}

	/**
	 * Get the context id from the API configuration, if it is of {@link SwaggerConfiguration} type.
	 * @param configuration API endpoint configuration
	 * @return Optional context id
	 */
	private static Optional<String> getSwaggerConfigurationContextId(
			ApiEndpointConfiguration<? extends OpenAPIConfiguration> configuration) {
		return configuration.getConfiguration().filter(cfg -> cfg instanceof SwaggerConfiguration)
				.map(cfg -> (SwaggerConfiguration) cfg).map(sc -> sc.getId());
	}

}
