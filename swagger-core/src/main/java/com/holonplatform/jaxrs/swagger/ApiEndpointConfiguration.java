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
package com.holonplatform.jaxrs.swagger;

import java.io.Serializable;
import java.util.Optional;

import javax.ws.rs.core.Application;

/**
 * API listing endpoint configuration.
 *
 * @param <C> API configuration type
 *
 * @since 5.2.0
 */
public interface ApiEndpointConfiguration<C> extends Serializable {

	/**
	 * Get the ClassLoader to use.
	 * @return Optional ClassLoader
	 */
	Optional<ClassLoader> getClassLoader();

	/**
	 * Get the JAX-RS application.
	 * @return Optional JAX-RS application
	 */
	Optional<Application> getApplication();

	/**
	 * Get the API endpoint type.
	 * @return Optional API endpoint type
	 */
	Optional<ApiEndpointType> getType();

	/**
	 * Get the API context id.
	 * @return Optional API context id
	 */
	Optional<String> getContextId();

	/**
	 * Get the API endpoint path.
	 * @return Optional endpoint path
	 */
	Optional<String> getPath();

	/**
	 * Get the API configuration location.
	 * @return Optional API configuration location
	 */
	Optional<String> getConfigurationLocation();

	/**
	 * Get the API configuration.
	 * @return Optional API configuration
	 */
	Optional<C> getConfiguration();

}
