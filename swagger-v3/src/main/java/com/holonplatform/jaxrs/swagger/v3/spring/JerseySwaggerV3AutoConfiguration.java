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

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.holonplatform.jaxrs.swagger.ApiDefaults;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.annotations.ApiConfiguration;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.v3.internal.spring.AbstractSwaggerV3AutoConfiguration;

import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;

/**
 * Spring Boot Swagger/OpenAPI v3 auto-configuration class for Jersey runtime.
 * <p>
 * This class configures API listing endpoints according to one or more API configuration definitions. An API
 * configuration endpoint can be decalred and configured using either:
 * <ul>
 * <li>One or more {@link OpenAPIConfiguration} type beans (using for example the {@link SwaggerConfiguration}
 * implementation. The {@link ApiConfiguration} annotation can be used on each API configuration bean to setup the API
 * listing endpoint, for example to declare the JAX-RS endpoint path. When more then one {@link OpenAPIConfiguration}
 * type bean is declared, the {@link ApiConfiguration#contextId()} attribute should be used to declare a different API
 * context id for each configuration.</li>
 * <li>When no {@link OpenAPIConfiguration} type bean is defined, the {@link SwaggerConfigurationProperties} application
 * properties can be used to configure the API definition and the API listing endpoints. To declare more than one API
 * definition subset, a set of API group can be declared. Each group id will be used as API context id.</li>
 * </ul>
 * <p>
 * By default, the {@link ApiDefaults#DEFAULT_API_ENDPOINT_PATH} path is used as JAX-RS API listing endpoint path if not
 * configured otherwise.
 * </p>
 * <p>
 * A Jersey <code>ResourceConfig</code> type bean must be available in context to enable the API listing endpoints
 * auto-configuration.
 * </p>
 *
 * @since 5.2.0
 */
@Configuration
@ConditionalOnClass(name = { "org.glassfish.jersey.server.ResourceConfig", "io.swagger.v3.oas.models.OpenAPI" })
@ConditionalOnBean(type = "org.glassfish.jersey.server.ResourceConfig")
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 1000)
@AutoConfigureAfter(name = "com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyAutoConfiguration")
@EnableConfigurationProperties(SwaggerConfigurationProperties.class)
public class JerseySwaggerV3AutoConfiguration extends AbstractSwaggerV3AutoConfiguration<ResourceConfig> {

	@Value("${spring.jersey.application-path:/}")
	private String applicationPath;

	public JerseySwaggerV3AutoConfiguration(SwaggerConfigurationProperties configurationProperties,
			ObjectProvider<OpenAPIConfiguration> apiConfigurations) {
		super(configurationProperties, apiConfigurations);
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#getDefaultScannerType()
	 */
	@Override
	protected JaxrsScannerType getDefaultScannerType() {
		return JaxrsScannerType.APPLICATION;
	}

	@Bean
	public ResourceConfigCustomizer jerseySwaggerV3ResourceConfigCustomizer() {
		return application -> this.configure(application);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v3.internal.spring.AbstractSwaggerV3AutoConfiguration#
	 * getDefaultApplicationPath()
	 */
	@Override
	protected Optional<String> getDefaultApplicationPath() {
		return Optional.ofNullable(applicationPath);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v3.internal.spring.AbstractSwaggerV3AutoConfiguration#registerEndpoint(javax.
	 * ws.rs.core.Application, java.lang.Class)
	 */
	@Override
	protected void registerEndpoint(ResourceConfig application, Class<?> endpoint) {
		application.register(endpoint);
	}

}
