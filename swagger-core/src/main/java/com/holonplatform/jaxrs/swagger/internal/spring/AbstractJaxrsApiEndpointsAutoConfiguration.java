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
package com.holonplatform.jaxrs.swagger.internal.spring;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Application;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.ObjectProvider;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.ApiContext;
import com.holonplatform.jaxrs.swagger.ApiEndpointBuilder;
import com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration;
import com.holonplatform.jaxrs.swagger.ApiEndpointDefinition;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties.ApiGroupConfiguration;

/**
 * Base Spring Boot auto-configuration class for JAX-RS API listing endpoints.
 * 
 * @param <A> JAX-RS Application type
 * @param <C> API configuration type
 * 
 * @since 5.2.0
 */
public abstract class AbstractJaxrsApiEndpointsAutoConfiguration<A extends Application, C>
		implements BeanClassLoaderAware {

	protected static final Logger LOGGER = SwaggerLogger.create();

	private final SwaggerConfigurationProperties configurationProperties;

	private final A application;

	private final ObjectProvider<C> apiConfigurations;

	private final ApiEndpointBuilder<C> apiEndpointBuilder;

	private ClassLoader classLoader;

	/**
	 * Constructor.
	 * @param configurationProperties API configuration properties
	 * @param application JAX-RS application
	 * @param configurations API configurations provider
	 * @param apiEndpointBuilder API endpoint builder
	 */
	public AbstractJaxrsApiEndpointsAutoConfiguration(SwaggerConfigurationProperties configurationProperties,
			A application, ObjectProvider<C> apiConfigurations, ApiEndpointBuilder<C> apiEndpointBuilder) {
		super();
		ObjectUtils.argumentNotNull(apiEndpointBuilder, "ApiEndpointBuilder must be not null");
		this.configurationProperties = configurationProperties;
		this.application = application;
		this.apiConfigurations = apiConfigurations;
		this.apiEndpointBuilder = apiEndpointBuilder;
	}

	/**
	 * Get the JAX-RS Application path.
	 * @return Optional JAX-RS Application path
	 */
	protected abstract Optional<String> getApplicationPath();

	/**
	 * Get the default API configuration to use when no API configuration is defined.
	 * @return The default API configuration
	 */
	protected abstract C getDefaultConfiguration();

	/**
	 * Get the JAX-RS endpoint path.
	 * @return Optional JAX-RS endpoint path
	 */
	protected abstract Optional<String> getEndpointPath(C configuration);

	/**
	 * Get the API context id.
	 * @return Optional API context id
	 */
	protected abstract Optional<String> getApiContextId(C configuration);

	/**
	 * Register given endpoint class in the JAX-RS application.
	 * @param endpoint The endpoint definition to register
	 */
	protected abstract void registerEndpoint(ApiEndpointDefinition endpoint);

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang.ClassLoader)
	 */
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Get the JAX-RS application.
	 * @return the JAX-RS application
	 */
	protected A getApplication() {
		return application;
	}

	/**
	 * Configure the API listing endpoints.
	 */
	protected void configureEndpoints() {
		// check disabled
		if (configurationProperties.isEnabled()) {
			registerEndpoints();
		} else {
			LOGGER.info("Swagger API endpoints configuration is disabled.");
		}
	}

	/**
	 * Register the API listing endpoints.
	 */
	private void registerEndpoints() {
		final List<C> configurations = this.apiConfigurations.stream().collect(Collectors.toList());
		if (configurations.isEmpty()) {
			// default configuration
			configureAndRegisterEndpoint(getDefaultConfiguration());
		} else {
			// use configurations
			for (C configuration : configurations) {
				configureAndRegisterEndpoint(configuration);
			}
		}
	}

	/**
	 * Register an API listing endpoint using given configuration.
	 * @param configuration The API configuration
	 */
	private void configureAndRegisterEndpoint(C configuration) {
		final ApiEndpointConfiguration.Builder<C> builder = ApiEndpointConfiguration.<C>builder()
				.configuration(configuration).application(getApplication()).classLoader(classLoader);
		// context id
		final String contextId = getApiContextId(configuration).orElse(ApiContext.DEFAULT_CONTEXT_ID);
		builder.contextId(getApiContextId(configuration).orElse(ApiContext.DEFAULT_CONTEXT_ID));
		// path
		final String path = getApiEndpointPath(configuration, contextId);
		builder.path(path);
		// type
		final ApiEndpointType type = getApiEndpointType(configuration, contextId);
		builder.type(type);
		final ApiEndpointConfiguration<C> endpointConfiguration = builder.build();
		// register endpoint
		registerEndpoint(apiEndpointBuilder.build(endpointConfiguration, true));
	}

	private String getApiEndpointPath(C configuration, String contextId) {
		// from configuration
		Optional<String> path = getEndpointPath(configuration);
		if (path.isPresent()) {
			return path.get();
		}
		// using configuration properties
		if (ApiContext.DEFAULT_CONTEXT_ID.equals(contextId)) {
			if (configurationProperties.getPath() != null && !configurationProperties.getPath().trim().equals("")) {
				return configurationProperties.getPath().trim();
			}
		} else {
			path = getApiGroupConfiguration(contextId).map(g -> g.getPath())
					.filter(p -> p != null && !p.trim().equals(""));
			if (path.isPresent()) {
				return path.get();
			}
		}
		return ApiContext.DEFAULT_API_ENDPOINT_PATH;
	}

	private ApiEndpointType getApiEndpointType(@SuppressWarnings("unused") C configuration, String contextId) {
		// using configuration properties
		if (ApiContext.DEFAULT_CONTEXT_ID.equals(contextId)) {
			if (configurationProperties.getType() != null) {
				return configurationProperties.getType();
			}
		} else {
			ApiEndpointType type = getApiGroupConfiguration(contextId).map(g -> g.getType()).orElse(null);
			if (type != null) {
				return type;
			}
		}
		return ApiEndpointType.getDefault();
	}

	private Optional<ApiGroupConfiguration> getApiGroupConfiguration(String contextId) {
		List<ApiGroupConfiguration> groups = configurationProperties.getApiGroups();
		if (groups != null) {
			return groups.stream().filter(g -> contextId.equals(g.getGroupId())).findFirst();
		}
		return Optional.empty();
	}

}
