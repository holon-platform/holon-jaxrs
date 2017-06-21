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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfigCustomizer;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.spring.internal.SwaggerJerseyConfiguration;
import com.holonplatform.jaxrs.swagger.spring.internal.SwaggerResteasyConfiguration;

import io.swagger.models.Swagger;

/**
 * Spring Boot Swagger auto-configuration class.
 * 
 * @since 5.0.0
 */
@Configuration
@ConditionalOnClass(Swagger.class)
@EnableConfigurationProperties(SwaggerConfigurationProperties.class)
public class SwaggerAutoConfiguration {

	private final static Logger LOGGER = SwaggerLogger.create();

	@Configuration
	@ConditionalOnBean(type = "org.glassfish.jersey.server.ResourceConfig")
	static class EnableSwaggerJersey implements InitializingBean {

		@Bean
		public ResourceConfigCustomizer swaggerConfiguration(SwaggerConfigurationProperties configurationProperties) {
			return new SwaggerJerseyConfiguration(configurationProperties);
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			LOGGER.debug(() -> "EnableSwaggerJersey initialized");
		}

	}

	@Configuration
	@ConditionalOnBean(type = "com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfig")
	static class EnableResteasyJersey implements InitializingBean {

		@Bean
		public ResteasyConfigCustomizer swaggerConfiguration(SwaggerConfigurationProperties configurationProperties) {
			return new SwaggerResteasyConfiguration(configurationProperties);
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			LOGGER.debug(() -> "EnableResteasyJersey initialized");
		}

	}

}
