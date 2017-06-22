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
import java.util.List;

import com.holonplatform.jaxrs.swagger.internal.ApiGroupId;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;

/**
 * Swagger API listing definition.
 * 
 * @since 5.0.0
 */
public interface ApiListingDefinition extends Serializable {

	/**
	 * Get the API group id
	 * @return
	 */
	String getGroupId();

	/**
	 * Configure the JAX-RS API listing endpoint using this definition.
	 * @param classLoader ClassLoader
	 * @param basePath Base JAX-RS path
	 * @return API listing endpoints
	 */
	List<ApiListingEndpoint> configureEndpoints(ClassLoader classLoader, String basePath);

	/**
	 * Create a new {@link ApiListingDefinition}.
	 * @param groupId API group id
	 * @param properties Swagger configuration properties
	 * @return A new {@link ApiListingDefinition} instance
	 */
	static ApiListingDefinition create(String groupId, SwaggerConfigurationProperties properties) {
		return new DefaultApiListingDefinition(groupId, properties);
	}

	/**
	 * Create the default api group definition.
	 * @param properties Swagger configuration properties
	 * @return Default group {@link ApiListingDefinition} instance
	 */
	static ApiListingDefinition createDefault(SwaggerConfigurationProperties properties) {
		DefaultApiListingDefinition definition = new DefaultApiListingDefinition(ApiGroupId.DEFAULT_GROUP_ID);
		definition.setResourcePackage(properties.getResourcePackage());
		definition.setPath(properties.getPath());
		definition.setSchemes(properties.getSchemes());
		definition.setTitle(properties.getTitle());
		definition.setDescription(properties.getDescription());
		definition.setVersion(properties.getVersion());
		definition.setTermsOfServiceUrl(properties.getTermsOfServiceUrl());
		definition.setContact(properties.getContact());
		definition.setLicense(properties.getLicense());
		definition.setLicenseUrl(properties.getLicenseUrl());
		definition.setHost(properties.getHost());
		definition.setPrettyPrint(properties.isPrettyPrint());
		return definition;
	}

}
