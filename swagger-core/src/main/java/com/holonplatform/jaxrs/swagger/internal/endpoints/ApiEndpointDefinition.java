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
import java.util.concurrent.Callable;

import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;

/**
 * API listing endpoint definition.
 *
 * @since 5.2.0
 */
public interface ApiEndpointDefinition extends Serializable {

	/**
	 * Get the endpoint class.
	 * @return the endpoint class
	 */
	Class<?> getEndpointClass();

	/**
	 * Get the endpoint type.
	 * @return the endpoint type
	 */
	ApiEndpointType getType();

	/**
	 * Get the scanner type.
	 * @return the scanner type
	 */
	JaxrsScannerType getScannerType();

	/**
	 * Get the endpoint path.
	 * @return the endpoint path
	 */
	String getPath();

	/**
	 * Get the API context id to which the endpoint is bound.
	 * @return the API context id to which the endpoint is bound
	 */
	String getContextId();

	/**
	 * Init the endpoint context.
	 * @return <code>true</code> if initialized, <code>false</code> if already initialized
	 */
	boolean init();

	/**
	 * Create a new {@link ApiEndpointDefinition}.
	 * @param endpointClass The endpoint class (not null)
	 * @param type The endpoint type
	 * @param scannerType The scanner type
	 * @param path The endpoint path
	 * @param contextId the API context id to which the endpoint is bound
	 * @param initializer Initializer (not null)
	 * @return A new {@link ApiEndpointDefinition} instance
	 */
	static ApiEndpointDefinition create(Class<?> endpointClass, ApiEndpointType type, JaxrsScannerType scannerType,
			String path, String contextId, Callable<Void> initializer) {
		return new DefaultApiEndpointDefinition(endpointClass, type, scannerType, path, contextId, initializer);
	}

}
