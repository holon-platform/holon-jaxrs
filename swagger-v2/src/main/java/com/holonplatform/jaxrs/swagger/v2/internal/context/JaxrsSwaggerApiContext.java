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

import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration;

import io.swagger.models.Swagger;

/**
 * Swagger API definition context.
 * 
 * @since 5.2.0
 */
public interface JaxrsSwaggerApiContext {

	/**
	 * Get the context id.
	 * @return The context id
	 */
	String getId();

	/**
	 * Get the API configuration.
	 * @return the API configuration
	 */
	SwaggerConfiguration getConfiguration();

	/**
	 * Init the context.
	 * @throws ApiConfigurationException If an error occurred
	 */
	void init() throws ApiConfigurationException;

	/**
	 * Read the API model.
	 * @return the API model
	 */
	Swagger read();

}
