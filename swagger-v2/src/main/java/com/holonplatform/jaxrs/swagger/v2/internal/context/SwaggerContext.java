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
package com.holonplatform.jaxrs.swagger.v2.internal.context;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import io.swagger.models.Model;
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
	private static final ThreadLocal<Map<String, Model>> _models = new ThreadLocal<>();

	/**
	 * Get the current model.
	 * @return Optional current model
	 */
	public static Optional<Map<String, Model>> getModels() {
		return Optional.ofNullable(_models.get());
	}

	/**
	 * Get the current {@link Swagger} instance.
	 * @return Optional current {@link Swagger} instance.
	 */
	public static Optional<Swagger> getSwagger() {
		return Optional.ofNullable(_swagger.get());
	}

	/**
	 * Set the current {@link Swagger} instance.
	 * @param swagger the current {@link Swagger} instance
	 */
	public static void setSwagger(Swagger swagger) {
		_swagger.set(swagger);
		_models.set(new LinkedHashMap<>());
	}

	/**
	 * Removes the current {@link Swagger} instance.
	 */
	public static void removeOpenAPI() {
		_swagger.set(null);
		_models.set(new LinkedHashMap<>());
	}

	/**
	 * Include current models in current Swagger, if any.
	 */
	public static void includeModels() {
		getSwagger().ifPresent(swagger -> {
			getModels().ifPresent(models -> {
				if (swagger.getDefinitions() == null) {
					swagger.setDefinitions(new LinkedHashMap<>());
				}
				for (Entry<String, Model> entry : models.entrySet()) {
					if (!swagger.getDefinitions().containsKey(entry.getKey())) {
						swagger.getDefinitions().put(entry.getKey(), entry.getValue());
					}
				}
			});
		});
	}

}
