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
import java.util.Set;

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
	 * Get the package name to scan to detect API endpoints.
	 * @return Optional package name to scan to detect API endpoints
	 */
	Optional<String> getResourcePackage();

	/**
	 * Get the classes to scan.
	 * @return the classes to scan, empty if none
	 */
	Set<Class<?>> getClassesToScan();

	/**
	 * Get the supported protocol schemes
	 * @return the supported protocol schemes, may be <code>null</code>
	 */
	String[] getSchemes();

	/**
	 * Get the API title
	 * @return the API title, may be <code>null</code>
	 */
	String getTitle();

	/**
	 * Get the API version
	 * @return the API version, may be <code>null</code>
	 */
	String getVersion();

	/**
	 * Get the API description
	 * @return the API description, may be <code>null</code>
	 */
	String getDescription();

	/**
	 * Get the <em>Terms of service</em> URL
	 * @return the <em>Terms of service</em> URL, may be <code>null</code>
	 */
	String getTermsOfServiceUrl();

	/**
	 * Get the contact information
	 * @return the contact information, may be <code>null</code>
	 */
	String getContact();

	/**
	 * Get the license information
	 * @return the license information, may be <code>null</code>
	 */
	String getLicense();

	/**
	 * Get the license URL
	 * @return the license URL, may be <code>null</code>
	 */
	String getLicenseUrl();

	/**
	 * Get the API host
	 * @return the API host, may be <code>null</code>
	 */
	String getHost();

	/**
	 * Get whether to <em>pretty</em> format the API listing output
	 * @return whether to <em>pretty</em> format the API listing output
	 */
	boolean isPrettyPrint();

	/**
	 * Get the authentication schemes to use for API listing endpoint protection.
	 * @return the authentication scheme names, if only one <code>*</code> scheme is provided, any supported
	 *         authentication scheme is allowed
	 */
	String[] getAuthSchemes();

	/**
	 * Get the security roles to be used for endpoint access control
	 * @return the security roles, may be <code>null</code>
	 */
	String[] getSecurityRoles();

	/**
	 * Checks whether this definition is mergeable with the given definition.
	 * @param definition The definition to check
	 * @return The merged definition, or empty if the definitions are not mergeable
	 */
	Optional<ApiListingDefinition> isMergeable(ApiListingDefinition definition);

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
