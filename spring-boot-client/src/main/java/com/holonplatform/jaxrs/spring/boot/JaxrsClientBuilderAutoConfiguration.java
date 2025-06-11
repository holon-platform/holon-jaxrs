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
package com.holonplatform.jaxrs.spring.boot;

import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import com.holonplatform.jaxrs.spring.boot.internal.DefaultJaxrsClientBuilder;

/**
 * Auto configuration class for {@link JaxrsClientBuilder}.
 * 
 * <p>
 * A {@link JaxrsClientBuilder} bean will be configured in context only if a {@link JaxrsClientBuilder} type bean is not
 * already present.
 * </p>
 * 
 * <p>
 * Any {@link JaxrsClientCustomizer}s bean will be auto-detected and can be used to customize the {@link ClientBuilder}
 * instance used to provide the JAX-RS {@link Client}s.
 * </p>
 * 
 * <p>
 * A {@link JaxrsClientBuilderFactory} bean can be used to replace the default JAX-RS {@link ClientBuilder} instance
 * lookup strategy (i.e. {@link ClientBuilder#newBuilder()} with a custom one.
 * </p>
 * 
 * @since 5.0.0
 */
@AutoConfiguration
public class JaxrsClientBuilderAutoConfiguration {

	@Configuration
	@ConditionalOnClass(ClientBuilder.class)
	public static class ClientBuilderConfiguration {

		private final ObjectProvider<JaxrsClientBuilderFactory> clientBuilderFactory;
		private final ObjectProvider<List<JaxrsClientCustomizer>> customizers;

		public ClientBuilderConfiguration(ObjectProvider<JaxrsClientBuilderFactory> clientBuilderFactory,
				ObjectProvider<List<JaxrsClientCustomizer>> customizers) {
			super();
			this.clientBuilderFactory = clientBuilderFactory;
			this.customizers = customizers;
		}

		@Bean
		@ConditionalOnMissingBean
		public JaxrsClientBuilder jaxrsClientBuilder() {
			DefaultJaxrsClientBuilder builder = new DefaultJaxrsClientBuilder();
			JaxrsClientBuilderFactory factory = this.clientBuilderFactory.getIfUnique();
			if (factory != null) {
				builder.setFactory(factory);
			}
			List<JaxrsClientCustomizer> customizers = this.customizers.getIfAvailable();
			if (customizers != null && !customizers.isEmpty()) {
				AnnotationAwareOrderComparator.sort(customizers);
				customizers.forEach(c -> builder.addCustomizer(c));
			}
			return builder;
		}

	}

}
