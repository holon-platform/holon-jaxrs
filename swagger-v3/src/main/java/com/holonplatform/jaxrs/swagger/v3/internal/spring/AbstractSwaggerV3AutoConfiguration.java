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
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.AnnotationUtils;

import com.holonplatform.jaxrs.swagger.ApiEndpointDefinition;
import com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration;
import com.holonplatform.jaxrs.swagger.spring.ApiConfigurationProperties;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.v3.internal.endpoints.OpenApiEndpointBuilder;

import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

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
	 * @param configurations API configurations provider
	 * @param apiEndpointBuilder API endpoint builder
	 */
	public AbstractSwaggerV3AutoConfiguration(SwaggerConfigurationProperties configurationProperties,
			ObjectProvider<OpenAPIConfiguration> apiConfigurations) {
		super(configurationProperties, apiConfigurations, OpenApiEndpointBuilder.INSTANCE);
	}

	/**
	 * Get the default JAX-RS application path, if available.
	 * @return Optional default JAX-RS application path
	 */
	protected abstract Optional<String> getDefaultApplicationPath();

	/**
	 * Register given endpoint class in JAX-RS application.
	 * @param application The JAX-RS application
	 * @param endpoint The endpoint class to register
	 */
	protected abstract void registerEndpoint(A application, Class<?> endpoint);

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#registerEndpoint(javax
	 * .ws.rs.core.Application, com.holonplatform.jaxrs.swagger.ApiEndpointDefinition)
	 */
	@Override
	protected void registerEndpoint(A application, ApiEndpointDefinition endpoint) {
		registerEndpoint(application, endpoint.getEndpointClass());
		// log
		final String path = getApplicationPath(application).map(ap -> {
			StringBuilder sb = new StringBuilder();
			if (!ap.startsWith("/")) {
				sb.append("/");
			}
			sb.append(ap);
			if (!endpoint.getPath().startsWith("/") && !ap.endsWith("/")) {
				sb.append("/");
			}
			if (endpoint.getPath().startsWith("/")) {
				if (endpoint.getPath().length() > 1) {
					sb.append(endpoint.getPath().substring(1));
				}
			} else {
				sb.append(endpoint.getPath());
			}
			return sb.toString();
		}).orElseGet(() -> endpoint.getPath());
		LOGGER.info("Registered Swagger OpenAPI V3 endpoint type [" + endpoint.getType() + "] to path [" + path
				+ "] - API context id: [" + endpoint.getContextId() + "]");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#getApplicationPath(
	 * javax.ws.rs.core.Application)
	 */
	@Override
	protected Optional<String> getApplicationPath(A application) {
		Optional<String> defaultPath = getDefaultApplicationPath().filter(p -> p != null && !p.trim().equals(""));
		if (defaultPath.isPresent()) {
			return defaultPath;
		}
		if (application != null) {
			final ApplicationPath applicationPath = AnnotationUtils.findAnnotation(application.getClass(),
					ApplicationPath.class);
			if (applicationPath != null && !"".equals(applicationPath.value())) {
				return Optional.of(applicationPath.value());
			}
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#buildConfiguration(com
	 * .holonplatform.jaxrs.swagger.spring.ApiConfigurationProperties)
	 */
	// TODO
	@Override
	protected OpenAPIConfiguration buildConfiguration(ApiConfigurationProperties configurationProperties) {
		final SwaggerConfiguration cfg = new SwaggerConfiguration();
		// configuration
		Set<String> packages = configurationProperties.getResourcePackages();
		if (!packages.isEmpty()) {
			cfg.setResourcePackages(packages);
		}
		cfg.setPrettyPrint(configurationProperties.isPrettyPrint());
		// API definition
		final OpenAPI api = new OpenAPI();
		final Info info = new Info();
		api.setInfo(info);
		getConfigurationProperty(configurationProperties.getTitle()).ifPresent(v -> {
			info.setTitle(v);
		});
		getConfigurationProperty(configurationProperties.getVersion()).ifPresent(v -> {
			info.setVersion(v);
		});
		getConfigurationProperty(configurationProperties.getDescription()).ifPresent(v -> {
			info.setDescription(v);
		});
		getConfigurationProperty(configurationProperties.getTermsOfServiceUrl()).ifPresent(v -> {
			info.setTermsOfService(v);
		});
		getConfigurationProperty(configurationProperties.getLicense()).ifPresent(v -> {
			if (info.getLicense() == null) {
				info.setLicense(new License());
			}
			info.getLicense().setName(v);
		});
		getConfigurationProperty(configurationProperties.getLicenseUrl()).ifPresent(v -> {
			if (info.getLicense() == null) {
				info.setLicense(new License());
			}
			info.getLicense().setUrl(v);
		});
		getConfigurationProperty(configurationProperties.getContact()).ifPresent(v -> {
			if (info.getContact() == null) {
				info.setContact(new Contact());
			}
			info.getContact().setName(v);
		});
		getConfigurationProperty(configurationProperties.getContactEmail()).ifPresent(v -> {
			if (info.getContact() == null) {
				info.setContact(new Contact());
			}
			info.getContact().setEmail(v);
		});
		getConfigurationProperty(configurationProperties.getContactUrl()).ifPresent(v -> {
			if (info.getContact() == null) {
				info.setContact(new Contact());
			}
			info.getContact().setUrl(v);
		});
		cfg.setOpenAPI(api);
		return cfg;
	}

}
