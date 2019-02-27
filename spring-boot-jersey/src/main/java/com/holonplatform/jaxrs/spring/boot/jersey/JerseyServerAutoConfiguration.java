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
package com.holonplatform.jaxrs.spring.boot.jersey;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.jaxrs.spring.boot.jersey.internal.JerseyResourcesPostProcessor;

/**
 * Jersey JAX-RS server auto configuration.
 * <p>
 * If a {@link ResourceConfig} type bean is not already defined, a standard {@link ResourceConfig} bean is automatically
 * registered.
 * </p>
 * <p>
 * Any bean annotated with {@link Path} or {@link Provider} is detected and automatically registered as a JAX-RS
 * resource. To disable automatic resource scan and registration, the <code>holon.jersey.bean-scan</code> configuration
 * property with a <code>false</code> value can be used.
 * </p>
 * <p>
 * Note that {@link Provider} annotated bean must be <em>singleton</em> scoped.
 * </p>
 * 
 * @since 5.0.0
 */
@Configuration
@ConditionalOnClass(ResourceConfig.class)
@AutoConfigureBefore(JerseyAutoConfiguration.class)
@EnableConfigurationProperties(JerseyConfigurationProperties.class)
public class JerseyServerAutoConfiguration {

	@Configuration
	@ConditionalOnMissingBean(ResourceConfig.class)
	static class JerseyApplicationConfiguration {

		private final JerseyConfigurationProperties configurationProperties;

		public JerseyApplicationConfiguration(JerseyConfigurationProperties configurationProperties) {
			super();
			this.configurationProperties = configurationProperties;
		}

		@Bean
		public ResourceConfig jerseyApplicationConfig() {
			ResourceConfig resourceConfig = new ResourceConfig();
			if (configurationProperties.isForwardOn404()) {
				resourceConfig.property(ServletProperties.FILTER_FORWARD_ON_404, true);
			}
			return resourceConfig;
		}

	}

	@Configuration
	@ConditionalOnProperty(name = "holon.jersey.bean-scan", matchIfMissing = true)
	static class ResourcesAutoConfiguration {

		@Bean
		public static JerseyResourcesPostProcessor jerseyResourcesPostProcessor() {
			return new JerseyResourcesPostProcessor();
		}

	}

}
