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
package com.holonplatform.jaxrs.swagger.spring.internal;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationException;

import io.swagger.jaxrs.config.SwaggerConfigLocator;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

/**
 * Base class for API listing endpoints.
 * 
 * @since 5.0.0
 */
public class AbstractApiListingResource {

	/**
	 * Get the Swagger config id. By default, if the {@link ApiGroupId} annotation is present, the API group id is
	 * returned.
	 * @return The Swagger config id
	 */
	protected String getConfigId() {
		if (getClass().isAnnotationPresent(ApiGroupId.class)) {
			return AnnotationUtils.getStringValue(getClass().getAnnotation(ApiGroupId.class).value());
		}
		return null;
	}

	/**
	 * Get the Swagger API listing as JSON
	 * @return Swagger API JSON response
	 * @throws SwaggerConfigurationException Error processing JSON output
	 */
	protected Response asJson() throws SwaggerConfigurationException {
		Swagger swagger = getSwagger(getConfigId());
		if (swagger != null) {
			try {
				return Response.ok().entity(Json.mapper().writeValueAsString(swagger))
						.type(MediaType.APPLICATION_JSON_TYPE).build();
			} catch (JsonProcessingException e) {
				throw new SwaggerConfigurationException("Failed to process Swagger as JSON", e);
			}
		} else {
			return Response.status(404).build();
		}
	}

	/**
	 * Get the Swagger API listing as YAML
	 * @return Swagger API YAML response
	 * @throws SwaggerConfigurationException Error processing YAML output
	 */
	protected Response asYaml() throws SwaggerConfigurationException {
		Swagger swagger = getSwagger(getConfigId());
		try {
			if (swagger != null) {
				String yaml = Yaml.mapper().writeValueAsString(swagger);
				StringBuilder b = new StringBuilder();
				String[] parts = yaml.split("\n");
				for (String part : parts) {
					b.append(part);
					b.append("\n");
				}
				return Response.ok().entity(b.toString()).type("application/yaml").build();
			}
		} catch (Exception e) {
			throw new SwaggerConfigurationException("Failed to process Swagger as YAML", e);
		}
		return Response.status(404).build();
	}

	/**
	 * Get the Swagger definition using given configuration id.
	 * @param configId Configuration is
	 * @return Swagger definition, <code>null</code> if not available
	 */
	protected Swagger getSwagger(String configId) {
		return SwaggerConfigLocator.getInstance().getSwagger(configId);
	}

}
