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
package com.holonplatform.jaxrs.swagger.internal.endpoints;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

import jakarta.ws.rs.core.Application;

import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;

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
	 * Get the root packages for API resources scan, if available.
	 * @return Root API resources package, empty if none
	 */
	Set<String> getRootResourcePackages();

	/**
	 * Get the API endpoint type.
	 * @return Optional API endpoint type
	 */
	Optional<ApiEndpointType> getType();

	/**
	 * Get the API scanner type.
	 * @return Optional API scanner type
	 */
	Optional<JaxrsScannerType> getScannerType();

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

	// ------- builder

	/**
	 * Get a builder to create a {@link ApiEndpointConfiguration}.
	 * @param <C> API configuration type
	 * @return A new {@link Builder}
	 */
	static <C> Builder<C> builder() {
		return new DefaultApiEndpointConfiguration.DefaultBuilder<>();
	}

	/**
	 * {@link ApiEndpointConfiguration} builder.
	 *
	 * @param <C> API configuration type
	 */
	public interface Builder<C> {

		/**
		 * Set the API context id to use.
		 * @param contextId the API context id to set
		 * @return this
		 */
		Builder<C> contextId(String contextId);

		/**
		 * Set the API endpoint path.
		 * @param path the API endpoint path to set
		 * @return this
		 */
		Builder<C> path(String path);

		/**
		 * Set the ClassLoader to use for API endpoints generation.
		 * @param classLoader The ClassLoader to set
		 * @return this
		 */
		Builder<C> classLoader(ClassLoader classLoader);

		/**
		 * Set the endpoint type.
		 * @param type The endpoint type to set
		 * @return this
		 */
		Builder<C> type(ApiEndpointType type);

		/**
		 * Set the scanner type.
		 * @param scannerType The scanner type to set
		 * @return this
		 */
		Builder<C> scannerType(JaxrsScannerType scannerType);

		/**
		 * Set the JAX-RS {@link Application} to use.
		 * @param application the JAX-RS {@link Application} to set
		 * @return this
		 */
		Builder<C> application(Application application);

		/**
		 * Set the root packages for API resources scan.
		 * @param rootResourcePackages the API resources root packages
		 * @return this
		 */
		Builder<C> rootResourcePackages(Set<String> rootResourcePackages);

		/**
		 * Set the location to use to load the API definition.
		 * @param configurationLocation the API configuration location to set
		 * @return this
		 */
		Builder<C> configurationLocation(String configurationLocation);

		/**
		 * Set the API configuration.
		 * @param configuration the API configuration to set
		 * @return this
		 */
		Builder<C> configuration(C configuration);

		/**
		 * Build the {@link ApiEndpointConfiguration}.
		 * @return A new {@link ApiEndpointConfiguration} instance
		 */
		ApiEndpointConfiguration<C> build();

	}

}
