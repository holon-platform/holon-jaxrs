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
package com.holonplatform.jaxrs.swagger.spring.internal;

import java.io.Serializable;
import java.util.Optional;

import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties.ApiGroupConfiguration;

/**
 * Swagger API listing definition.
 * 
 * @since 5.0.0
 */
public interface ApiListingDefinition extends Serializable {

	/**
	 * Get the API group id.
	 * @return The group id
	 */
	String getGroupId();

	/**
	 * Get the API listing path.
	 * @return Optional API listing path
	 */
	Optional<String> getPath();

	/**
	 * Get the API listing endpoint path, using the default path if {@link #getPath()} is not available.
	 * @return the API listing endpoint path
	 */
	String getEndpointPath();

	/**
	 * Configure the JAX-RS API listing endpoint using this definition.
	 * @param classLoader ClassLoader
	 * @param basePath Base JAX-RS path
	 * @return API listing endpoints
	 */
	ApiListingEndpoint configureEndpoint(ClassLoader classLoader, String basePath);

	/**
	 * Create a new {@link ApiListingDefinition}.
	 * @param groupId API group id (not null)
	 * @return A new {@link ApiListingDefinition} instance
	 */
	static ApiListingDefinition create(String groupId) {
		return new DefaultApiListingDefinition(groupId);
	}

	/**
	 * Create a new {@link ApiListingDefinition}.
	 * @param groupId API group id (not null)
	 * @param properties Swagger configuration properties
	 * @return A new {@link ApiListingDefinition} instance
	 */
	static ApiListingDefinition create(String groupId, SwaggerConfigurationProperties properties) {
		final DefaultApiListingDefinition definition = new DefaultApiListingDefinition(groupId);
		definition.init(properties, null);
		return definition;
	}

	/**
	 * Create a new {@link ApiListingDefinition}.
	 * @param groupId API group id (not null)
	 * @param properties Swagger configuration properties
	 * @param groupConfiguration Optional group configuration properties
	 * @return A new {@link ApiListingDefinition} instance
	 */
	static ApiListingDefinition create(String groupId, SwaggerConfigurationProperties properties,
			ApiGroupConfiguration groupConfiguration) {
		final DefaultApiListingDefinition definition = new DefaultApiListingDefinition(groupId);
		definition.init(properties, groupConfiguration);
		return definition;
	}

}
