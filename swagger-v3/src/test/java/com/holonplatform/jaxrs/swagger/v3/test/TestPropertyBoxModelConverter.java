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
package com.holonplatform.jaxrs.swagger.v3.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import com.holonplatform.jaxrs.swagger.v3.OpenAPIContextListener;
import com.holonplatform.jaxrs.swagger.v3.test.model.AbstractTestResource;

import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;

public class TestPropertyBoxModelConverter {

	@Path("resource1")
	private static class TestResource1 extends AbstractTestResource {

	}

	@Test
	public void testPropertyBoxConversion() {

		final SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration.setOpenAPI(new OpenAPI().info(new Info().title("Test PropertyBox").version("1")));

		Set<Class<?>> classes = new HashSet<>();
		classes.add(TestResource1.class);
		classes.add(OpenAPIContextListener.class);

		Reader reader = new Reader(configuration);
		OpenAPI api = reader.read(classes);
		validateApi(api);

	}

	private static void validateApi(OpenAPI api) {

		assertNotNull(api);
		assertNotNull(api.getPaths());
		assertEquals(23, api.getPaths().size());

		validateModel1(validateOperationResponse(api, "/resource1/test1"));
		validateModel1(validateOperationBody(api, "/resource1/test2"));
		validateModel1(validateOperationResponseArray(api, "/resource1/test3", false));
		validateModel1(validateOperationResponseArray(api, "/resource1/test4", false));
		validateModel1(validateOperationResponseArray(api, "/resource1/test5", true));
		validateModel2(validateOperationResponse(api, "/resource1/test6"));
		validateModel2(validateOperationBody(api, "/resource1/test7"));
		validateTestData(validateOperationResponse(api, "/resource1/test8"));
		validateTestData(validateOperationBody(api, "/resource1/test9"));
		validateStringPlainResponse(api, "/resource1/test10");
		validateModelOne(api, validateOperationResponse(api, "/resource1/test11"));
		validateModelOne(api, validateOperationBody(api, "/resource1/test12"));
		validateModelOne(api, validateOperationResponseArray(api, "/resource1/test13", false));
		validateModelOne(api, validateOperationResponseArray(api, "/resource1/test14", false));
		validateModelOne(api, validateOperationResponseArray(api, "/resource1/test15", true));
		validateModelTwo(api, validateOperationResponse(api, "/resource1/test16"));
		validateModelTwo(api, validateOperationBody(api, "/resource1/test17"));

	}

	private static Schema<?> validateOperationResponse(OpenAPI api, String path) {
		assertTrue(api.getPaths().containsKey(path));
		PathItem item = api.getPaths().get(path);
		assertNotNull(item);
		Operation op = item.getGet();
		assertNotNull(op);
		assertNotNull(op.getResponses());
		assertEquals(1, op.getResponses().size());
		ApiResponse resp = op.getResponses().values().iterator().next();
		assertNotNull(resp);
		assertNotNull(resp.getContent());
		assertEquals(1, resp.getContent().size());
		io.swagger.v3.oas.models.media.MediaType mt = resp.getContent().get(MediaType.APPLICATION_JSON);
		assertNotNull(mt);
		return mt.getSchema();
	}

	private static Schema<?> validateOperationResponseArray(OpenAPI api, String path, boolean uniqueItems) {
		assertTrue(api.getPaths().containsKey(path));
		PathItem item = api.getPaths().get(path);
		assertNotNull(item);
		Operation op = item.getGet();
		assertNotNull(op);
		assertNotNull(op.getResponses());
		assertEquals(1, op.getResponses().size());
		ApiResponse resp = op.getResponses().values().iterator().next();
		assertNotNull(resp);
		assertNotNull(resp.getContent());
		assertEquals(1, resp.getContent().size());
		io.swagger.v3.oas.models.media.MediaType mt = resp.getContent().get(MediaType.APPLICATION_JSON);
		assertNotNull(mt);
		Schema<?> array = mt.getSchema();
		assertNotNull(array);
		assertEquals("array", array.getType());
		assertTrue(array instanceof ArraySchema);
		boolean unique = (array.getUniqueItems() != null) ? array.getUniqueItems() : false;
		assertEquals(unique, uniqueItems);
		return ((ArraySchema) array).getItems();
	}

	private static Schema<?> validateOperationBody(OpenAPI api, String path) {
		assertTrue(api.getPaths().containsKey(path));
		PathItem item = api.getPaths().get(path);
		assertNotNull(item);
		Operation op = item.getPut();
		assertNotNull(op);
		RequestBody body = op.getRequestBody();
		assertNotNull(body);
		assertNotNull(body.getContent());
		assertEquals(1, body.getContent().size());
		io.swagger.v3.oas.models.media.MediaType mt = body.getContent().get(MediaType.APPLICATION_JSON);
		assertNotNull(mt);
		return mt.getSchema();
	}

	private static void validateStringPlainResponse(OpenAPI api, String path) {
		assertTrue(api.getPaths().containsKey(path));
		PathItem item = api.getPaths().get(path);
		assertNotNull(item);
		Operation op = item.getGet();
		assertNotNull(op);
		assertNotNull(op.getResponses());
		assertEquals(1, op.getResponses().size());
		ApiResponse resp = op.getResponses().values().iterator().next();
		assertNotNull(resp);
		assertNotNull(resp.getContent());
		assertEquals(1, resp.getContent().size());
		io.swagger.v3.oas.models.media.MediaType mt = resp.getContent().get(MediaType.TEXT_PLAIN);
		assertNotNull(mt);
		Schema<?> schema = mt.getSchema();
		assertNotNull(schema);
		assertEquals("string", schema.getType());
	}

	private static void validateTestData(Schema<?> schema) {
		assertNotNull(schema);
		assertNotNull(schema.get$ref());
		assertTrue(schema.get$ref().endsWith("TestData"));
	}

	private static void validateModel1(Schema<?> schema) {
		validateModel1(schema, "PropertyBox");
	}

	private static void validateModel1(Schema<?> schema, String title) {
		assertNotNull(schema);
		assertEquals(title, schema.getTitle());
		assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(2, schema.getProperties().size());
		assertTrue(schema.getProperties().containsKey("id1"));
		Schema<?> property = schema.getProperties().get("id1");
		assertNotNull(property);
		assertEquals("integer", property.getType());
		assertTrue(schema.getProperties().containsKey("name1"));
		property = schema.getProperties().get("name1");
		assertNotNull(property);
		assertEquals("string", property.getType());
	}

	private static void validateModel2(Schema<?> schema) {
		validateModel2(schema, "PropertyBox");
	}

	private static void validateModel2(Schema<?> schema, String title) {
		assertNotNull(schema);
		assertEquals(title, schema.getTitle());
		assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(2, schema.getProperties().size());
		assertTrue(schema.getProperties().containsKey("id2"));
		Schema<?> property = schema.getProperties().get("id2");
		assertNotNull(property);
		assertEquals("integer", property.getType());
		assertTrue(schema.getProperties().containsKey("name2"));
		property = schema.getProperties().get("name2");
		assertNotNull(property);
		assertEquals("string", property.getType());
	}

	@SuppressWarnings("rawtypes")
	private static void validateModelOne(OpenAPI api, Schema<?> schema) {
		assertNotNull(schema);
		assertNotNull(schema.get$ref());
		assertTrue(schema.get$ref().endsWith("ModelOne"));
		assertNotNull(api.getComponents());
		assertNotNull(api.getComponents().getSchemas());
		Map<String, Schema> schemas = api.getComponents().getSchemas();
		assertTrue(schemas.containsKey("ModelOne"));
		validateModel1(schemas.get("ModelOne"), "ModelOne");
	}

	@SuppressWarnings("rawtypes")
	private static void validateModelTwo(OpenAPI api, Schema<?> schema) {
		assertNotNull(schema);
		assertNotNull(schema.get$ref());
		assertTrue(schema.get$ref().endsWith("ModelTwo"));
		assertNotNull(api.getComponents());
		assertNotNull(api.getComponents().getSchemas());
		Map<String, Schema> schemas = api.getComponents().getSchemas();
		assertTrue(schemas.containsKey("ModelTwo"));
		validateModel2(schemas.get("ModelTwo"), "ModelTwo");
	}

}
