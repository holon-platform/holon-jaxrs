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
package com.holonplatform.jaxrs.swagger.v2.spring;

import java.util.Optional;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfig;
import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfigCustomizer;
import com.holonplatform.jaxrs.swagger.ApiDefaults;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.annotations.ApiConfiguration;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.v2.internal.spring.AbstractSwaggerV2AutoConfiguration;

import io.swagger.config.SwaggerConfig;
import io.swagger.jaxrs.config.BeanConfig;

/**
 * Spring Boot Swagger v2 auto-configuration class for Resteasy runtime.
 * <p>
 * This class configures API listing endpoints according to one or more API configuration definitions. An API
 * configuration endpoint can be decalred and configured using either:
 * <ul>
 * <li>One or more {@link SwaggerConfig} type beans (using for example the {@link BeanConfig} implementation). The
 * {@link ApiConfiguration} annotation can be used on each API configuration bean to setup the API listing endpoint, for
 * example to declare the JAX-RS endpoint path. When more then one {@link SwaggerConfig} type bean is declared, the
 * {@link ApiConfiguration#contextId()} attribute should be used to declare a different API context id for each
 * configuration.</li>
 * <li>When no {@link SwaggerConfig} type bean is defined, the {@link SwaggerConfigurationProperties} application
 * properties can be used to configure the API definition and the API listing endpoints. To declare more than one API
 * definition subset, a set of API group can be declared. Each group id will be used as API context id.</li>
 * </ul>
 * <p>
 * By default, the {@link ApiDefaults#DEFAULT_API_ENDPOINT_PATH} path is used as JAX-RS API listing endpoint path if not
 * configured otherwise.
 * </p>
 * <p>
 * A Holon <code>ResteasyConfig</code> type bean must be available in context to enable the API listing endpoints
 * auto-configuration.
 * </p>
 *
 * @since 5.2.0
 */
@Configuration
@ConditionalOnClass(name = { "com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfig",
		"io.swagger.models.Swagger" })
@ConditionalOnBean(type = "com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfig")
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 1000)
@AutoConfigureAfter(name = "com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyAutoConfiguration")
@EnableConfigurationProperties(SwaggerConfigurationProperties.class)
public class ResteasySwaggerV2AutoConfiguration extends AbstractSwaggerV2AutoConfiguration<ResteasyConfig> {

	@Value("${holon.resteasy.application-path:/}")
	private String applicationPath;

	public ResteasySwaggerV2AutoConfiguration(SwaggerConfigurationProperties configurationProperties,
			ObjectProvider<SwaggerConfig> openAPIConfigurations) {
		super(configurationProperties, openAPIConfigurations);
	}

	@Override
	protected JaxrsScannerType getDefaultScannerType() {
		return JaxrsScannerType.APPLICATION_AND_ANNOTATION;
	}

	@Bean
	public ResteasyConfigCustomizer resteasySwaggerV3ResourceConfigCustomizer() {
		return application -> this.configure(application);
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
	 * com.holonplatform.jaxrs.swagger.v3.internal.spring.AbstractSwaggerV3AutoConfiguration#registerEndpoint(javax.ws.
	 * rs.core.Application, java.lang.Class)
	 */
	@Override
	protected void registerEndpoint(ResteasyConfig application, Class<?> endpoint) {
		application.register(endpoint);
	}

}
