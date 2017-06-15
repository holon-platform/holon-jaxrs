/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.jaxrs.swagger.spring;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.SwaggerSerializers;

/**
 * Swagger configuration for Jersey using {@link SwaggerConfigurationProperties}.
 * 
 * @since 5.0.0
 */
public class SwaggerJerseyConfiguration implements ResourceConfigCustomizer {

	/**
	 * Logger
	 */
	private final static Logger LOGGER = SwaggerLogger.create();

	@Value("${spring.jersey.application-path:/}")
	private String apiPath;

	/**
	 * Configuration properties
	 */
	private final SwaggerConfigurationProperties configurationProperties;

	public SwaggerJerseyConfiguration(SwaggerConfigurationProperties configurationProperties) {
		super();
		this.configurationProperties = configurationProperties;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer#customize(org.glassfish.jersey.server.
	 * ResourceConfig)
	 */
	@Override
	public void customize(ResourceConfig config) {
		// Serializers
		if (!config.isRegistered(SwaggerSerializers.class)) {
			config.register(SwaggerSerializers.class);
		}

		// Api listing
		String path = configurationProperties.getPath();
		if (path == null || path.trim().equals("")) {
			path = SwaggerConfigurationProperties.DEFAULT_PATH;
		}
		config.registerResources(Resource.builder(SwaggerApiListingResource.class).path(path).build());

		// Config
		BeanConfig swaggerCfg = new BeanConfig();
		if (configurationProperties.getResourcePackage() != null
				&& !configurationProperties.getResourcePackage().trim().equals("")) {
			swaggerCfg.setResourcePackage(configurationProperties.getResourcePackage());
		}
		swaggerCfg.setTitle(configurationProperties.getTitle());
		swaggerCfg.setVersion(configurationProperties.getVersion());
		swaggerCfg.setTermsOfServiceUrl(configurationProperties.getTermsOfServiceUrl());
		swaggerCfg.setContact(configurationProperties.getContact());
		swaggerCfg.setLicense(configurationProperties.getLicense());
		swaggerCfg.setLicenseUrl(configurationProperties.getLicenseUrl());

		swaggerCfg.setBasePath(apiPath);
		swaggerCfg.setHost(configurationProperties.getHost());

		if (configurationProperties.getSchemes() != null && configurationProperties.getSchemes().length > 0) {
			swaggerCfg.setSchemes(configurationProperties.getSchemes());
		}
		swaggerCfg.setPrettyPrint(configurationProperties.isPrettyPrint());

		// scan
		swaggerCfg.setScan(true);

		StringBuilder ap = new StringBuilder();
		if (apiPath != null) {
			if (!apiPath.startsWith("/")) {
				ap.append('/');
			}
			ap.append(apiPath);
		}
		if (path != null) {
			if (!path.startsWith("/")) {
				ap.append('/');
			}
			ap.append(path);
		}

		LOGGER.info("Swagger initialized - API listing path: " + ap.toString());

	}

}
