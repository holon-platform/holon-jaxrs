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
package com.holonplatform.jaxrs.swagger.v3.internal.context;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Current OpenAPI resolution context.
 *
 * @since 5.2.0
 */
public final class OpenApiResolutionContext {

	private static final ThreadLocal<OpenAPI> _openAPI = new ThreadLocal<>();
	private static final ThreadLocal<Map<String, Schema<?>>> _schemas = new ThreadLocal<>();

	private OpenApiResolutionContext() {
	}

	/**
	 * Get the current {@link OpenAPI} instance, if available.
	 * @return Optional current {@link OpenAPI} instance
	 */
	public static Optional<OpenAPI> getOpenAPI() {
		return Optional.ofNullable(_openAPI.get());
	}

	/**
	 * Get the current schemas.
	 * @return Optional current schemas
	 */
	public static Optional<Map<String, Schema<?>>> getSchemas() {
		return Optional.ofNullable(_schemas.get());
	}

	/**
	 * Set the current {@link OpenAPI} instance.
	 * @param openAPI The {@link OpenAPI} instance to set
	 */
	public static void setOpenAPI(OpenAPI openAPI) {
		_openAPI.set(openAPI);
		_schemas.set(new LinkedHashMap<>());
	}

	/**
	 * Removes the current {@link OpenAPI} instance.
	 */
	public static void removeOpenAPI() {
		_openAPI.set(null);
		_schemas.set(new LinkedHashMap<>());
	}

	/**
	 * Include current schemas in current OpenAPI, if any
	 */
	public static void includeSchemas() {
		getOpenAPI().ifPresent(openAPI -> {
			getSchemas().ifPresent(schemas -> {
				if (openAPI.getComponents() == null) {
					openAPI.setComponents(new Components());
				}
				if (openAPI.getComponents().getSchemas() == null) {
					openAPI.getComponents().setSchemas(new LinkedHashMap<>());
				}
				for (Entry<String, Schema<?>> entry : schemas.entrySet()) {
					if (!openAPI.getComponents().getSchemas().containsKey(entry.getKey())) {
						openAPI.getComponents().getSchemas().put(entry.getKey(), entry.getValue());
					}
				}
			});
		});
	}

}
