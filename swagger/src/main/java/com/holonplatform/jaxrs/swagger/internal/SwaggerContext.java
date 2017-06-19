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
package com.holonplatform.jaxrs.swagger.internal;

import io.swagger.models.Swagger;

/**
 * Context class to set and provide current {@link Swagger} instance using a {@link ThreadLocal}.
 * 
 * @since 5.0.0
 */
public final class SwaggerContext {

	/**
	 * Swagger ThreadLocal instance
	 */
	private static final ThreadLocal<Swagger> _swagger = new ThreadLocal<>();

	/**
	 * Get the current {@link Swagger} instance.
	 * @return The current {@link Swagger} instance.
	 */
	public static Swagger getSwagger() {
		return _swagger.get();
	}

	/**
	 * Set the current {@link Swagger} instance.
	 * @param swagger the current {@link Swagger} instance
	 */
	public static void setSwagger(Swagger swagger) {
		_swagger.set(swagger);
	}

}
