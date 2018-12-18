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
package com.holonplatform.jaxrs.swagger.v3;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.jaxrs.swagger.ApiReader;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.v3.internal.DefaultApiReader;
import com.holonplatform.jaxrs.swagger.v3.internal.context.OpenApiContextAdapter;
import com.holonplatform.jaxrs.swagger.v3.internal.context.OpenApiContextListener;
import com.holonplatform.jaxrs.swagger.v3.internal.context.OpenApiReaderAdapter;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.jaxrs2.ReaderListener;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * Entrypoint interface for Swagger/OpenAPI V3 readers and adapters.
 * 
 * @since 5.2.0
 */
public interface SwaggerV3 {

	/**
	 * A {@link ReaderListener} class to include in the OpenAPI classes to enable a consistent API definition generation
	 * when the {@link PropertyBox} type is used.
	 */
	public static Class<? extends ReaderListener> CONTEXT_READER_LISTENER = OpenApiContextListener.class;

	/**
	 * Create a new {@link ApiReader} to create an OpenAPI definition from a set of API resource classes.
	 * @param configuration The API configuration to use
	 * @return The {@link ApiReader} instance
	 */
	static ApiReader<OpenAPI> reader(OpenAPIConfiguration configuration) {
		return new DefaultApiReader(configuration);
	}

	/**
	 * Convert given API definition in JSON format.
	 * @param pretty Whether to pretty format the output
	 * @return The API definition as JSON
	 */
	static String asJson(OpenAPI api, boolean pretty) {
		try {
			return pretty ? Json.pretty(api) : Json.mapper().writeValueAsString(api);
		} catch (Exception e) {
			throw new ApiConfigurationException(e);
		}
	}

	/**
	 * Convert given API definition in JSON format.
	 * @return The API definition as JSON
	 */
	static String asJson(OpenAPI api) {
		return asJson(api, false);
	}

	/**
	 * Convert given API definition in YAML format.
	 * @param pretty Whether to pretty format the output
	 * @return The API definition as YAML
	 */
	static String asYaml(OpenAPI api, boolean pretty) {
		try {
			return pretty ? Yaml.pretty(api) : Yaml.mapper().writeValueAsString(api);
		} catch (Exception e) {
			throw new ApiConfigurationException(e);
		}
	}

	/**
	 * Convert given API definition in YAML format.
	 * @return The API definition as YAML
	 */
	static String asYaml(OpenAPI api) {
		return asYaml(api, false);
	}

	/**
	 * Adapt given {@link OpenApiReader} to ensure the {@link SwaggerV3#CONTEXT_READER_LISTENER} class inclusion in the
	 * classes to read.
	 * <p>
	 * The {@link SwaggerV3#CONTEXT_READER_LISTENER} class is required to provide a consistent API definition when the
	 * {@link PropertyBox} type is used.
	 * </p>
	 * @param reader The reader to adapt (not null)
	 * @return The adapted {@link OpenApiReader}
	 */
	static OpenApiReader adapt(OpenApiReader reader) {
		return new OpenApiReaderAdapter(reader);
	}

	/**
	 * Adapt given {@link OpenApiReader} to ensure the {@link SwaggerV3#CONTEXT_READER_LISTENER} class inclusion in the
	 * classes to read, using an adapted {@link OpenApiReader}.
	 * @param context The context to adapt (not null)
	 * @return The adapted {@link OpenApiContext}
	 * @see #adapt(OpenApiReader)
	 */
	static OpenApiContext adapt(OpenApiContext context) {
		return new OpenApiContextAdapter(context);
	}

}
