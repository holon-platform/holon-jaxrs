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
package com.holonplatform.jaxrs.swagger.v3.internal.spring;

import java.util.Optional;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.AnnotationUtils;

import com.holonplatform.jaxrs.swagger.ApiEndpointDefinition;
import com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.v3.internal.endpoints.OpenApiEndpointBuilder;

import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;

/**
 * Base Swagger API listing endpoints auto-configuration.
 * 
 * @param <A> JAX-RS Application type
 * 
 * @since 5.2.0
 */
public abstract class AbstractSwaggerV3AutoConfiguration<A extends Application>
		extends AbstractJaxrsApiEndpointsAutoConfiguration<A, OpenAPIConfiguration> {

	/**
	 * Constructor.
	 * @param configurationProperties API configuration properties
	 * @param application JAX-RS application
	 * @param configurations API configurations provider
	 * @param apiEndpointBuilder API endpoint builder
	 */
	public AbstractSwaggerV3AutoConfiguration(SwaggerConfigurationProperties configurationProperties, A application,
			ObjectProvider<OpenAPIConfiguration> apiConfigurations) {
		super(configurationProperties, application, apiConfigurations, OpenApiEndpointBuilder.INSTANCE);
	}

	/**
	 * Get the default JAX-RS application path, if available.
	 * @return Optional default JAX-RS application path
	 */
	protected abstract Optional<String> getDefaultApplicationPath();

	/**
	 * Register given endpoint class in JAX-RS application.
	 * @param endpoint The endpoint class to register
	 */
	protected abstract void registerEndpoint(Class<?> endpoint);

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#registerEndpoint(com.
	 * holonplatform.jaxrs.swagger.ApiEndpointDefinition)
	 */
	@Override
	protected void registerEndpoint(ApiEndpointDefinition endpoint) {
		registerEndpoint(endpoint.getEndpointClass());
		LOGGER.info("Registered Swagger OpenAPI V3 endpoint type [" + endpoint.getType() + "] to path ["
				+ endpoint.getPath() + "] - API context id: [" + endpoint.getContextId() + "]");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#getApplicationPath()
	 */
	@Override
	protected Optional<String> getApplicationPath() {
		Optional<String> defaultPath = getDefaultApplicationPath().filter(p -> p != null && !p.trim().equals(""));
		if (defaultPath.isPresent()) {
			return defaultPath;
		}
		final ApplicationPath applicationPath = AnnotationUtils.findAnnotation(getApplication().getClass(),
				ApplicationPath.class);
		if (applicationPath != null && !"".equals(applicationPath.value())) {
			return Optional.of(applicationPath.value());
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#
	 * getDefaultConfiguration()
	 */
	@Override
	protected OpenAPIConfiguration getDefaultConfiguration() {
		return new SwaggerConfiguration();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#getEndpointPath(java.
	 * lang.Object)
	 */
	@Override
	protected Optional<String> getEndpointPath(OpenAPIConfiguration configuration) {
		return OpenApiEndpointBuilder.getEndpointPath(configuration);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#getApiContextId(java.
	 * lang.Object)
	 */
	@Override
	protected Optional<String> getApiContextId(OpenAPIConfiguration configuration) {
		return OpenApiEndpointBuilder.getContextId(configuration);
	}

}
