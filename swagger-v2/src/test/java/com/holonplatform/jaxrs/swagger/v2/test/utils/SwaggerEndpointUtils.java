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
package com.holonplatform.jaxrs.swagger.v2.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Assertions;

import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

public final class SwaggerEndpointUtils {

	private SwaggerEndpointUtils() {
	}

	public static Swagger readAsJson(Response response) {
		assertNotNull(response);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals("application/json", response.getHeaderString("Content-Type"));
		String openApiString = response.readEntity(String.class);
		assertNotNull(openApiString);
		Swagger api = null;
		try {
			api = Json.mapper().readValue(openApiString, Swagger.class);
		} catch (IOException e) {
			Assertions.fail(e);
		}
		assertNotNull(api);
		return api;
	}

	public static Swagger readAsYaml(Response response) {
		assertNotNull(response);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals("application/yaml", response.getHeaderString("Content-Type"));
		String openApiString = response.readEntity(String.class);
		assertNotNull(openApiString);
		Swagger api = null;
		try {
			api = Yaml.mapper().readValue(openApiString, Swagger.class);
		} catch (IOException e) {
			Assertions.fail(e);
		}
		assertNotNull(api);
		return api;
	}

}
