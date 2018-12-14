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
package com.holonplatform.jaxrs.swagger.v3.builders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;

import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.integration.api.OpenApiScanner;

/**
 * Base {@link OpenApiContext} builder.
 * 
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface OpenApiContextBuilder<B extends OpenApiContextBuilder<B>> {

	/**
	 * Set the context id.
	 * @param contextId the context id to set
	 * @return this
	 */
	B contextId(String contextId);

	/**
	 * Set the API configuration to use.
	 * @param configuration The configuration to set
	 * @return this
	 */
	B configuration(OpenAPIConfiguration configuration);

	/**
	 * Set the location to use to load the API context configuration.
	 * @param configLocation the configuration location
	 * @return this
	 */
	B configLocation(String configLocation);

	/**
	 * Set the packages to scan to detect the API resources to include in the API definition.
	 * <p>
	 * This packages will be used to detect the API resources if no resource package/class is provide by the
	 * {@link OpenAPIConfiguration}.
	 * </p>
	 * @param resourcePackages the packages to scan to detect the API resources to include in the API definition
	 * @return this
	 */
	B resourcePackages(Set<String> resourcePackages);

	/**
	 * Set the packages to scan to detect the API resources to include in the API definition.
	 * <p>
	 * This packages will be used to detect the API resources if no resource package/class is provide by the
	 * {@link OpenAPIConfiguration}.
	 * </p>
	 * @param resourcePackages the packages to scan to detect the API resources to include in the API definition
	 * @return this
	 */
	default B resourcePackages(String... resourcePackages) {
		return resourcePackages(new HashSet<>(Arrays.asList(resourcePackages)));
	}

	/**
	 * Set API resources classes to include in the API definition.
	 * <p>
	 * This classes will be used as API resources if no resource package/class is provide by the
	 * {@link OpenAPIConfiguration}.
	 * </p>
	 * @param resourceClasses the API resources classes to include in the API definition
	 * @return this
	 */
	B resourceClasses(Set<Class<?>> resourceClasses);

	/**
	 * Set API resources classes to include in the API definition.
	 * <p>
	 * This classes will be used as API resources if no resource package/class is provide by the
	 * {@link OpenAPIConfiguration}.
	 * </p>
	 * @param resourceClasses the API resources classes to include in the API definition
	 * @return this
	 */
	default B resourceClasses(Class<?>... resourceClasses) {
		return resourceClasses(new HashSet<>(Arrays.asList(resourceClasses)));
	}

	/**
	 * Set the {@link OpenApiScanner} to use.
	 * @param scanner The scanner to set
	 * @return this
	 */
	B scanner(OpenApiScanner scanner);

	/**
	 * Set the {@link OpenApiReader} to use.
	 * @param reader The reader to set
	 * @return this
	 */
	B reader(OpenApiReader reader);

	/**
	 * Build the OpenAPI context.
	 * @param initialize Whether to initialize the context, which included the registration in the context locators
	 *        registry
	 * @return the OpenAPI context instance
	 * @throws ApiConfigurationException If an error occurred
	 */
	OpenApiContext build(boolean initialize) throws ApiConfigurationException;

}
