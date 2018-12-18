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
package com.holonplatform.jaxrs.swagger.spring;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;

/**
 * API listing configuration properties.
 *
 * @since 5.2.0
 */
public interface ApiConfigurationProperties {

	/**
	 * Get whether is a API group configuration.
	 * @return whether is a API group configuration
	 */
	boolean isGroupConfiguration();

	/**
	 * Get whether to include all API resources or to include only the resources with a suitable API definition
	 * annotation.
	 * <p>
	 * If <code>false</code>: For Swagger V2, only the <code>io.swagger.annotations.ApiOperation</code> annotated
	 * resource methods will be incuded. For Swagger/OpenAPI V3, only the
	 * <code>io.swagger.v3.oas.annotations.Operation</code> annotated resource methods will be incuded.
	 * </p>
	 * @return whether to include all API resources
	 */
	boolean isIncludeAll();

	/**
	 * Get the package names to use to filter the API resource classes.
	 * <p>
	 * To specify more than one package name, a comma separator con be used
	 * </p>
	 * @return the package name to use to filter the API resource classes
	 */
	String getResourcePackage();

	/**
	 * Get the package names to use to filter the API resource classes.
	 * @return the package names to use to filter the API resource classes
	 */
	default Set<String> getResourcePackages() {
		if (getResourcePackage() != null) {
			if (getResourcePackage().contains(",")) {
				return Arrays.asList(getResourcePackage().split(",")).stream()
						.filter(n -> n != null && !n.trim().equals("")).map(n -> n.trim()).collect(Collectors.toSet());
			}
			return Collections.singleton(getResourcePackage().trim());
		}
		return Collections.emptySet();
	}

	/**
	 * Get the API listing endpoint path.
	 * @return the API listing endpoint path
	 */
	String getPath();

	/**
	 * Get the API listing endpoint type.
	 * @return the API listing endpoint type
	 */
	ApiEndpointType getType();

	/**
	 * Get the API resource classes scanner type.
	 * @return the API resource classes scanner type
	 */
	JaxrsScannerType getScannerType();

	/**
	 * Get the API context id.
	 * @return The context id
	 */
	String getContextId();

	/**
	 * Get the API title.
	 * @return the API title
	 */
	String getTitle();

	/**
	 * Get the API version.
	 * @return the API version
	 */
	String getVersion();

	/**
	 * Get the API description.
	 * @return the API description
	 */
	String getDescription();

	/**
	 * Get the API terms of service URL.
	 * @return the API terms of service URL
	 */
	String getTermsOfServiceUrl();

	/**
	 * Get the API contact name.
	 * @return the API contact name
	 */
	String getContactName();

	/**
	 * Get the API contact email.
	 * @return the API contact email
	 */
	String getContactEmail();

	/**
	 * Get the API contact URL.
	 * @return the API contact URL
	 */
	String getContactUrl();

	/**
	 * Get the API license name.
	 * @return the API license name
	 */
	String getLicenseName();

	/**
	 * Get the API license URL.
	 * @return the API license URL
	 */
	String getLicenseUrl();

	/**
	 * Get the external documentation URL.
	 * @return the API contact URL
	 */
	String getExternalDocsUrl();

	/**
	 * Get the external documentation description.
	 * @return the API contact URL
	 */
	String getExternalDocsDescription();

	/**
	 * Get the API server URL.
	 * @return the API server URL
	 */
	String getServerUrl();

	/**
	 * Get the API server description.
	 * @return the API server description
	 */
	String getServerDescription();

	/**
	 * Get whether the API output should be formatted.
	 * @return whether the API output should be formatted
	 */
	boolean isPrettyPrint();

}
