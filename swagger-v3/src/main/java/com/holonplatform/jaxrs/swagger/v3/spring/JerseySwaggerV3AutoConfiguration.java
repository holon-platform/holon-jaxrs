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
package com.holonplatform.jaxrs.swagger.v3.spring;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.v3.internal.spring.AbstractSwaggerV3AutoConfiguration;

import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;

/**
 * TODO
 *
 */
@Configuration
@ConditionalOnClass(name = { "org.glassfish.jersey.server.ResourceConfig", "io.swagger.v3.oas.models.OpenAPI" })
@ConditionalOnBean(type = "org.glassfish.jersey.server.ResourceConfig")
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 1000)
@AutoConfigureAfter(JerseyAutoConfiguration.class)
@EnableConfigurationProperties(SwaggerConfigurationProperties.class)
public class JerseySwaggerV3AutoConfiguration extends AbstractSwaggerV3AutoConfiguration<ResourceConfig> {

	@Value("${spring.jersey.application-path}")
	private String applicationPath;

	public JerseySwaggerV3AutoConfiguration(SwaggerConfigurationProperties configurationProperties,
			ResourceConfig application, ObjectProvider<OpenAPIConfiguration> openAPIConfigurations) {
		super(configurationProperties, application, openAPIConfigurations);
	}

	@PostConstruct
	public void configure() {
		configureEndpoints();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v3.internal.spring.AbstractSwaggerV3AutoConfiguration#getDefaultApplicationPath()
	 */
	@Override
	protected Optional<String> getDefaultApplicationPath() {
		return Optional.ofNullable(applicationPath);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v3.internal.spring.AbstractSwaggerV3AutoConfiguration#registerEndpoint(java.lang.
	 * Class)
	 */
	@Override
	protected void registerEndpoint(Class<?> endpoint) {
		getApplication().register(endpoint);
	}

}
