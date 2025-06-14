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
package com.holonplatform.jaxrs.swagger.v3.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Assertions;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;

public final class OpenAPIEndpointUtils {

	private OpenAPIEndpointUtils() {
	}

	public static OpenAPI readAsJson(Response response) {
		assertNotNull(response);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals("application/json", response.getHeaderString("Content-Type"));
		String openApiString = response.readEntity(String.class);
		assertNotNull(openApiString);
		OpenAPI api = null;
		try {
			api = Json.mapper().readValue(openApiString, OpenAPI.class);
		} catch (IOException e) {
			Assertions.fail(e);
		}
		assertNotNull(api);
		return api;
	}

	public static OpenAPI readAsYaml(Response response) {
		assertNotNull(response);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals("application/yaml", response.getHeaderString("Content-Type"));
		String openApiString = response.readEntity(String.class);
		assertNotNull(openApiString);
		OpenAPI api = null;
		try {
			api = Yaml.mapper().readValue(openApiString, OpenAPI.class);
		} catch (IOException e) {
			Assertions.fail(e);
		}
		assertNotNull(api);
		return api;
	}

}
