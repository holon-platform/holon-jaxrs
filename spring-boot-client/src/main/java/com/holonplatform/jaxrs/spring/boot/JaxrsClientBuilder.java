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

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

/**
 * Builder that can be used to configure and create a JAX-RS {@link Client}.
 * <p>
 * In a typical auto-configured Spring Boot application this builder is available as a bean and can be injected whenever
 * a JAX-RS {@link Client} is needed.
 * </p>
 * <p>
 * If the {@link JaxrsClientBuilderAutoConfiguration} class in enabled in a auto-configured Spring Boot application,
 * {@link JaxrsClientCustomizer}s beans will be auto-detected and can be used to customize the {@link ClientBuilder}
 * instance used to provide the JAX-RS {@link Client}s. The {@link JaxrsClientBuilderFactory} can be used to replace the
 * default JAX-RS {@link ClientBuilder} instance lookup strategy (i.e. {@link ClientBuilder#newBuilder()} with a custom
 * one.
 * </p>
 * 
 * @since 5.0.0
 */
public interface JaxrsClientBuilder {

	/**
	 * Build a new JAX-RS {@link Client}.
	 * @return The JAX-RS {@link Client} instance
	 */
	Client build();

}
