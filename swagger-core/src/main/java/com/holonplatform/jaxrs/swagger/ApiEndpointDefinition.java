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
package com.holonplatform.jaxrs.swagger;

import java.io.Serializable;

import com.holonplatform.jaxrs.swagger.internal.DefaultApiEndpointDefinition;

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
	 * Create a new {@link ApiEndpointDefinition}.
	 * @param endpointClass The endpoint class (not null)
	 * @param type The endpoint type
	 * @param path The endpoint path
	 * @param contextId the API context id to which the endpoint is bound
	 * @return A new {@link ApiEndpointDefinition} instance
	 */
	static ApiEndpointDefinition create(Class<?> endpointClass, ApiEndpointType type, String path, String contextId) {
		return new DefaultApiEndpointDefinition(endpointClass, type, path, contextId);
	}

}
