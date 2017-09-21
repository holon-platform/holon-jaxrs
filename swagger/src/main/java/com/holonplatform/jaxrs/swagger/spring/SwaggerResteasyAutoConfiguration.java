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

import java.util.List;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfig;
import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfigCustomizer;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.spring.internal.ApiListingDefinition;
import com.holonplatform.jaxrs.swagger.spring.internal.SwaggerJaxrsUtils;

import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Swagger;

/**
 * Spring Boot Swagger auto-configuration class for Resteasy runtime.
 * 
 * @since 5.0.0
 */
@Configuration
@ConditionalOnClass(Swagger.class)
@ConditionalOnBean(type = "com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfig")
@EnableConfigurationProperties(SwaggerConfigurationProperties.class)
public class SwaggerResteasyAutoConfiguration {

	private final static Logger LOGGER = SwaggerLogger.create();
	
	@Bean
	public ResteasyConfigCustomizer swaggerConfiguration(SwaggerConfigurationProperties configurationProperties) {
		return new SwaggerResteasyConfiguration(configurationProperties);
	}
	
	class SwaggerResteasyConfiguration implements ResteasyConfigCustomizer, BeanClassLoaderAware {

		private ClassLoader classLoader;

		@Value("${holon.resteasy.application-path:/}")
		private String apiPath;

		/**
		 * Configuration properties
		 */
		private final SwaggerConfigurationProperties configurationProperties;

		public SwaggerResteasyConfiguration(SwaggerConfigurationProperties configurationProperties) {
			super();
			this.configurationProperties = configurationProperties;
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang.ClassLoader)
		 */
		@Override
		public void setBeanClassLoader(ClassLoader classLoader) {
			this.classLoader = classLoader;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfigCustomizer#customize(com.holonplatform.jaxrs.spring.
		 * boot.resteasy.ResteasyConfig)
		 */
		@Override
		public void customize(ResteasyConfig config) {
			// Serializers
			if (!config.isRegistered(SwaggerSerializers.class)) {
				config.register(SwaggerSerializers.class);
			}
			// check configuration
			if (configurationProperties.isPrettyPrint()) {
				SwaggerSerializers.setPrettyPrint(true);
			}
			// API listings
			final List<ApiListingDefinition> definitions = SwaggerJaxrsUtils.getApiListings(configurationProperties);
			for (ApiListingDefinition definition : definitions) {
				definition.configureEndpoints(classLoader, apiPath).forEach(e -> {
					config.register(e.getResourceClass());
					LOGGER.info("[" + e.getGroupId() + "] Swagger API listing configured - Path: "
							+ SwaggerJaxrsUtils.composePath(apiPath, e.getPath()));
				});
			}

		}

	}

}
