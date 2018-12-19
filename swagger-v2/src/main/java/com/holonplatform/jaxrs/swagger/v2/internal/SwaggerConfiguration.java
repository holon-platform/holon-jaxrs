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
package com.holonplatform.jaxrs.swagger.v2.internal;

import java.util.Collection;
import java.util.Set;

import io.swagger.config.SwaggerConfig;

/**
 * Extended Swagger configuration.
 *
 * @since 5.2.0
 */
public interface SwaggerConfiguration extends SwaggerConfig {

	/**
	 * Get the package names to use to filter the API resource classes.
	 * @return The resource packages
	 */
	Set<String> getResourcePackages();

	/**
	 * Get the API reader class.
	 * @return the API reader class
	 */
	String getReaderClass();

	/**
	 * Get the API scanner class.
	 * @return the API scanner class
	 */
	String getScannerClass();

	/**
	 * Get the API routes to ignore.
	 * @return the API routes to ignore
	 */
	Collection<String> getIgnoredRoutes();

	/**
	 * Get whether to read all the API resource classes, diregarding the Api annotation.
	 * @return whether to read all the API resource classes
	 */
	boolean isReadAllResources();

	/**
	 * Get whether to pretty print the API output.
	 * @return whether to pretty print the API output
	 */
	boolean isPrettyPrint();

	/**
	 * Set the package names to use to filter the API resource classes.
	 * @param resourcePackages the package names to set
	 */
	void setResourcePackages(Set<String> resourcePackages);

}
