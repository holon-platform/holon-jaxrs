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
package com.holonplatform.jaxrs.swagger.spring.internal;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Swagger API listing configuration.
 * 
 * @since 5.2.0
 */
public interface ApiListingConfiguration extends Serializable {

	/**
	 * Get the API group id.
	 * @return The group id
	 */
	String getGroupId();

	/**
	 * Get the API listing endpoint path, using the default path if not available.
	 * @return the API listing endpoint path
	 */
	String getPath();

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

}
