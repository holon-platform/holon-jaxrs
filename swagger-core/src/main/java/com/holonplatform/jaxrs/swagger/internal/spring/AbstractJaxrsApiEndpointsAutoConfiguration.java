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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Application;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.AnnotationUtils;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.ApiContext;
import com.holonplatform.jaxrs.swagger.ApiEndpointBuilder;
import com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration;
import com.holonplatform.jaxrs.swagger.ApiEndpointDefinition;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.annotations.ApiConfiguration;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.spring.ApiConfigurationProperties;
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
	 * Build an API configuration using given configuration properties.
	 * @param configurationProperties The API configuration properties
	 * @return The API configuration
	 */
	protected abstract C buildConfiguration(ApiConfigurationProperties configurationProperties);

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
			// default configurations
			getDefaultConfigurations().entrySet().forEach(e -> {
				configureAndRegisterEndpoint(e.getValue(), e.getKey());
			});
		} else {
			// use configurations
			for (C configuration : configurations) {
				configureAndRegisterEndpoint(configuration, getApiEndpointContextId(configuration));
			}
		}
	}

	/**
	 * Get the default API configurations from configuration properties.
	 * @return The contextId - configuration properties, empty if none
	 */
	private Map<String, ApiConfigurationProperties> getDefaultConfigurations() {
		final Map<String, ApiConfigurationProperties> configurations = new HashMap<>();
		if (configurationProperties.getApiGroups() != null && !configurationProperties.getApiGroups().isEmpty()) {
			// groups
			for (ApiGroupConfiguration group : configurationProperties.getApiGroups()) {
				if (group.getGroupId() != null && !group.getGroupId().trim().equals("")) {
					final String contextId = group.getGroupId().trim();
					if (configurations.containsKey(contextId)) {
						throw new ApiConfigurationException(
								"Duplicate Swagger API configuration group id: " + contextId);
					}
					configurations.put(contextId, group);
				} else {
					LOGGER.warn(
							"At least one API group id definition is missing from Swagger API configuration properties: check the application configuration properties.");
				}
			}
		} else {
			// default
			configurations.put(ApiContext.DEFAULT_CONTEXT_ID, configurationProperties);
		}
		return configurations;
	}

	/**
	 * Register an API listing endpoint using given configuration.
	 * @param configuration The API configuration
	 * @param contextId The API context id
	 */
	private void configureAndRegisterEndpoint(C configuration, String contextId) {
		// register endpoint
		registerEndpoint(apiEndpointBuilder.build(ApiEndpointConfiguration.<C>builder()
				// context id
				.contextId(contextId)
				// API configuration
				.configuration(configuration)
				// JAX-RS application
				.application(getApplication())
				// path
				.path(getApiEndpointPath(configuration, contextId))
				// type
				.type(getApiEndpointType(configuration))
				// classLoader
				.classLoader(classLoader)
				// build
				.build(), true));
	}

	/**
	 * Register an API listing endpoint using given configuration properties.
	 * @param configurationProperties The API configuration properties
	 * @param contextId The API context id
	 */
	private void configureAndRegisterEndpoint(ApiConfigurationProperties configurationProperties, String contextId) {
		// register endpoint
		registerEndpoint(apiEndpointBuilder.build(ApiEndpointConfiguration.<C>builder()
				// context id
				.contextId(contextId)
				// API configuration
				.configuration(buildConfiguration(configurationProperties))
				// JAX-RS application
				.application(getApplication())
				// path
				.path(getApiEndpointPath(configurationProperties, contextId))
				// type
				.type(getApiEndpointType(configurationProperties))
				// classLoader
				.classLoader(classLoader)
				// build
				.build(), true));
	}

	/**
	 * Get the API context id.
	 * @param configuration The API configuration
	 * @return the API context id
	 */
	private String getApiEndpointContextId(C configuration) {
		// check annotation
		final ApiConfiguration annotation = AnnotationUtils.findAnnotation(configuration.getClass(),
				ApiConfiguration.class);
		if (annotation != null && !"".equals(annotation.contextId())) {
			return annotation.contextId();
		}
		// default
		return ApiContext.DEFAULT_CONTEXT_ID;
	}

	/**
	 * Get the API endpoint path.
	 * @param configuration The API configuration
	 * @param contextId The API context id
	 * @return the API endpoint path
	 */
	private String getApiEndpointPath(C configuration, String contextId) {
		// check annotation
		final ApiConfiguration annotation = AnnotationUtils.findAnnotation(configuration.getClass(),
				ApiConfiguration.class);
		if (annotation != null && !"".equals(annotation.path())) {
			return annotation.path();
		}
		// default
		return getDefaultApiEndpointPath(contextId);
	}

	/**
	 * Get the API endpoint type.
	 * @param configuration The API configuration
	 * @return the API endpoint type
	 */
	private ApiEndpointType getApiEndpointType(C configuration) {
		// check annotation
		final ApiConfiguration annotation = AnnotationUtils.findAnnotation(configuration.getClass(),
				ApiConfiguration.class);
		if (annotation != null) {
			return annotation.endpointType();
		}
		// default
		return ApiEndpointType.getDefault();
	}

	/**
	 * Get the API endpoint path.
	 * @param configurationProperties API configuration properties
	 * @param contextId The API context id
	 * @return the API endpoint path
	 */
	private String getApiEndpointPath(ApiConfigurationProperties configurationProperties, String contextId) {
		if (configurationProperties.getPath() != null && !configurationProperties.getPath().trim().equals("")) {
			return configurationProperties.getPath();
		}
		return getDefaultApiEndpointPath(contextId);
	}

	/**
	 * Get the API endpoint type.
	 * @param configurationProperties API configuration properties
	 * @return the API endpoint type
	 */
	private static ApiEndpointType getApiEndpointType(ApiConfigurationProperties configurationProperties) {
		if (configurationProperties.getType() != null) {
			return configurationProperties.getType();
		}
		return ApiEndpointType.getDefault();
	}

	/**
	 * Get the default API listing endpoint path.
	 * @param contextId Optional API context id
	 * @return the default API listing endpoint path
	 */
	protected String getDefaultApiEndpointPath(String contextId) {
		if (contextId != null && !contextId.trim().equals("") && !ApiContext.DEFAULT_CONTEXT_ID.equals(contextId)) {
			return ApiContext.DEFAULT_API_ENDPOINT_PATH + "/" + contextId;
		}
		return ApiContext.DEFAULT_API_ENDPOINT_PATH;
	}

	/**
	 * Get a configuration property value, only if it is not <code>null</code> and not blank.
	 * @param value The value
	 * @return Optional configuration property value
	 */
	protected static Optional<String> getConfigurationProperty(String value) {
		if (value != null && !value.trim().equals("")) {
			return Optional.of(value.trim());
		}
		return Optional.empty();
	}

}
