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
package com.holonplatform.jaxrs.swagger.v2.internal.context;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.v2.SwaggerReader;
import com.holonplatform.jaxrs.swagger.v2.internal.DefaultSwaggerConfiguration;
import com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration;

import io.swagger.config.SwaggerConfig;
import io.swagger.jaxrs.config.JaxrsScanner;

/**
 * {@link JaxrsSwaggerApiContext} builder.
 *
 * @since 5.2.0
 */
public interface JaxrsSwaggerApiContextBuilder {

	/**
	 * Set the context id.
	 * @param contextId the context id to set
	 * @return this
	 */
	JaxrsSwaggerApiContextBuilder contextId(String contextId);

	/**
	 * Set the API configuration to use.
	 * @param configuration The configuration to set
	 * @return this
	 */
	JaxrsSwaggerApiContextBuilder configuration(SwaggerConfiguration configuration);

	/**
	 * Set the API configuration to use.
	 * @param configuration The configuration to set
	 * @return this
	 */
	default JaxrsSwaggerApiContextBuilder configuration(SwaggerConfig configuration) {
		return configuration(new DefaultSwaggerConfiguration(configuration));
	}

	/**
	 * Set the packages to scan to detect the API resources to include in the API definition.
	 * <p>
	 * This packages will be used to detect the API resources if no resource package/class is provide by the
	 * configuration.
	 * </p>
	 * @param resourcePackages the packages to scan to detect the API resources to include in the API definition
	 * @return this
	 */
	JaxrsSwaggerApiContextBuilder resourcePackages(Set<String> resourcePackages);

	/**
	 * Set the packages to scan to detect the API resources to include in the API definition.
	 * <p>
	 * This packages will be used to detect the API resources if no resource package/class is provide by the
	 * configuration.
	 * </p>
	 * @param resourcePackages the packages to scan to detect the API resources to include in the API definition
	 * @return this
	 */
	default JaxrsSwaggerApiContextBuilder resourcePackages(String... resourcePackages) {
		return resourcePackages(new HashSet<>(Arrays.asList(resourcePackages)));
	}

	/**
	 * Set the scanner type to use.
	 * @param scannerType The scanner type to set
	 * @return this
	 */
	JaxrsSwaggerApiContextBuilder scannerType(JaxrsScannerType scannerType);

	/**
	 * Set the {@link JaxrsScanner} to use.
	 * @param scanner The scanner to set
	 * @return this
	 */
	JaxrsSwaggerApiContextBuilder scanner(JaxrsScanner scanner);

	/**
	 * Set the {@link SwaggerReader} to use.
	 * @param reader The reader to set
	 * @return this
	 */
	JaxrsSwaggerApiContextBuilder reader(SwaggerReader reader);

	/**
	 * Set the JAX-RS {@link Application} to use to detect the API resources to use to build the API definitions.
	 * @param application The JAX-RS {@link Application} to set
	 * @return this
	 */
	JaxrsSwaggerApiContextBuilder application(Application application);

	/**
	 * Build the Api context.
	 * @param initialize Whether to initialize the context, which included the registration in the context locators
	 *        registry
	 * @return the API context instance
	 * @throws ApiConfigurationException If an error occurred
	 */
	JaxrsSwaggerApiContext build(boolean initialize) throws ApiConfigurationException;

	/**
	 * Get a builder to create and configure an {@link JaxrsSwaggerApiContext}.
	 * @return A new {@link JaxrsSwaggerApiContextBuilder}
	 */
	static JaxrsSwaggerApiContextBuilder create() {
		return new DefaultJaxrsSwaggerApiContextBuilder();
	}

}
