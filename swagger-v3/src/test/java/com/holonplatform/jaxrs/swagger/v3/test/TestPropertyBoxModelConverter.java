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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import com.holonplatform.jaxrs.swagger.v3.OpenApi;
import com.holonplatform.jaxrs.swagger.v3.test.model.AbstractTestResource;
import com.holonplatform.jaxrs.swagger.v3.test.model.EnumValue;

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
		classes.add(OpenApi.CONTEXT_READER_LISTENER);

		Reader reader = new Reader(configuration);
		OpenAPI api = reader.read(classes);
		validateApi(api);

	}

	public static void validateApi(OpenAPI api) {

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
		validateSet1(validateOperationResponse(api, "/resource1/test18"));
		validateSet1Model(api, validateOperationBody(api, "/resource1/test19"));
		validateSet2(validateOperationResponse(api, "/resource1/test20"));
		validateSet3(validateOperationResponse(api, "/resource1/test21"));
		validateSet4(validateOperationResponse(api, "/resource1/test22"));
		validateSet5(validateOperationResponse(api, "/resource1/test23"));

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

	@SuppressWarnings("rawtypes")
	private static void validateSet1Model(OpenAPI api, Schema<?> schema) {
		assertNotNull(schema);
		assertNotNull(schema.get$ref());
		assertTrue(schema.get$ref().endsWith("Set1"));
		assertNotNull(api.getComponents());
		assertNotNull(api.getComponents().getSchemas());
		Map<String, Schema> schemas = api.getComponents().getSchemas();
		assertTrue(schemas.containsKey("Set1"));
		validateSet1(schemas.get("Set1"), "Set1");
	}

	private static void validateSet1(Schema<?> schema) {
		validateSet1(schema, "PropertyBox");
	}

	private static void validateSet1(Schema<?> schema, String title) {
		assertNotNull(schema);
		assertEquals(title, schema.getTitle());
		assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(24, schema.getProperties().size());
		Schema<?> property = schema.getProperties().get("str");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = schema.getProperties().get("bool");
		assertNotNull(property);
		assertEquals("boolean", property.getType());
		property = schema.getProperties().get("int");
		assertNotNull(property);
		assertEquals("integer", property.getType());
		assertEquals("int32", property.getFormat());
		property = schema.getProperties().get("lng");
		assertNotNull(property);
		assertEquals("integer", property.getType());
		assertEquals("int64", property.getFormat());
		property = schema.getProperties().get("dbl");
		assertNotNull(property);
		assertEquals("number", property.getType());
		assertEquals("double", property.getFormat());
		property = schema.getProperties().get("flt");
		assertNotNull(property);
		assertEquals("number", property.getType());
		assertEquals("float", property.getFormat());
		property = schema.getProperties().get("bgd");
		assertNotNull(property);
		assertEquals("number", property.getType());
		property = schema.getProperties().get("shr");
		assertNotNull(property);
		assertEquals("integer", property.getType());
		assertEquals("int32", property.getFormat());
		property = schema.getProperties().get("byt");
		assertNotNull(property);
		assertEquals("string", property.getType());
		assertEquals("byte", property.getFormat());
		property = schema.getProperties().get("enm");
		assertNotNull(property);
		assertEquals("string", property.getType());
		assertNotNull(property.getEnum());
		assertEquals(3, property.getEnum().size());
		List<String> enums = property.getEnum().stream().filter(o -> o instanceof String).map(o -> (String) o)
				.collect(Collectors.toList());
		assertTrue(enums.contains(EnumValue.FIRST.name()));
		assertTrue(enums.contains(EnumValue.SECOND.name()));
		assertTrue(enums.contains(EnumValue.THIRD.name()));
		property = schema.getProperties().get("dat");
		assertNotNull(property);
		assertEquals("string", property.getType());
		// assertEquals("date", property.getFormat());
		property = schema.getProperties().get("tms");
		assertNotNull(property);
		assertEquals("string", property.getType());
		assertEquals("date-time", property.getFormat());
		property = schema.getProperties().get("ldat");
		assertNotNull(property);
		assertEquals("string", property.getType());
		assertEquals("date", property.getFormat());
		property = schema.getProperties().get("ltms");
		assertNotNull(property);
		assertEquals("string", property.getType());
		assertEquals("date-time", property.getFormat());
		property = schema.getProperties().get("ltm");
		assertNotNull(property);
		assertEquals("string", property.getType());
		assertEquals("time", property.getFormat());
		property = schema.getProperties().get("astr");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArraySchema);
		assertNotNull(((ArraySchema) property).getItems());
		assertEquals("string", ((ArraySchema) property).getItems().getType());
		property = schema.getProperties().get("aint");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArraySchema);
		assertNotNull(((ArraySchema) property).getItems());
		assertEquals("integer", ((ArraySchema) property).getItems().getType());
		property = schema.getProperties().get("aenm");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArraySchema);
		assertNotNull(((ArraySchema) property).getItems());
		assertEquals("string", ((ArraySchema) property).getItems().getType());
		assertNotNull(((ArraySchema) property).getItems().getEnum());
		assertEquals(3, ((ArraySchema) property).getItems().getEnum().size());
		property = schema.getProperties().get("achr");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArraySchema);
		assertNotNull(((ArraySchema) property).getItems());
		assertEquals("string", ((ArraySchema) property).getItems().getType());
		property = schema.getProperties().get("cstr");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArraySchema);
		assertNotNull(((ArraySchema) property).getItems());
		assertEquals("string", ((ArraySchema) property).getItems().getType());
		property = schema.getProperties().get("cint");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArraySchema);
		assertNotNull(((ArraySchema) property).getItems());
		assertEquals("integer", ((ArraySchema) property).getItems().getType());
		assertEquals("int32", ((ArraySchema) property).getItems().getFormat());
		property = schema.getProperties().get("clng");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArraySchema);
		assertNotNull(((ArraySchema) property).getItems());
		assertEquals("integer", ((ArraySchema) property).getItems().getType());
		assertEquals("int64", ((ArraySchema) property).getItems().getFormat());
		property = schema.getProperties().get("cenm");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArraySchema);
		assertNotNull(((ArraySchema) property).getItems());
		assertEquals("string", ((ArraySchema) property).getItems().getType());
		assertNotNull(((ArraySchema) property).getItems().getEnum());
		assertEquals(3, ((ArraySchema) property).getItems().getEnum().size());
		property = schema.getProperties().get("nbl");
		assertNotNull(property);
		assertEquals("boolean", property.getType());
	}

	private static void validateSet2(Schema<?> schema) {
		assertNotNull(schema);
		assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(4, schema.getProperties().size());
		Schema<?> property = schema.getProperties().get("str");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = schema.getProperties().get("enm");
		assertNotNull(property);
		assertEquals("string", property.getType());
		assertNotNull(property.getEnum());
		assertEquals(3, property.getEnum().size());
		List<String> enums = property.getEnum().stream().filter(o -> o instanceof String).map(o -> (String) o)
				.collect(Collectors.toList());
		assertTrue(enums.contains(EnumValue.FIRST.name()));
		assertTrue(enums.contains(EnumValue.SECOND.name()));
		assertTrue(enums.contains(EnumValue.THIRD.name()));
		property = schema.getProperties().get("n1");
		assertNotNull(property);
		assertEquals("object", property.getType());
		@SuppressWarnings("rawtypes")
		Map<String, Schema> nested = property.getProperties();
		assertNotNull(nested);
		assertEquals(3, nested.size());
		property = nested.get("v1");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = nested.get("v2");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = nested.get("v3");
		assertNotNull(property);
		assertEquals("boolean", property.getType());
		property = schema.getProperties().get("n2");
		assertNotNull(property);
		assertEquals("object", property.getType());
		nested = property.getProperties();
		assertNotNull(nested);
		assertEquals(3, nested.size());
		property = nested.get("v1");
		assertNotNull(property);
		assertEquals("integer", property.getType());
		property = nested.get("v2");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = nested.get("n3");
		assertNotNull(property);
		assertEquals("object", property.getType());
		nested = property.getProperties();
		assertNotNull(nested);
		assertEquals(2, nested.size());
		property = nested.get("v1");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = nested.get("v2");
		assertNotNull(property);
		assertEquals("number", property.getType());
	}

	private static void validateSet3(Schema<?> schema) {
		assertNotNull(schema);
		assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(3, schema.getProperties().size());
		Schema<?> property = schema.getProperties().get("str");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = schema.getProperties().get("enm");
		assertNotNull(property);
		assertEquals("string", property.getType());
		assertNotNull(property.getEnum());
		assertEquals(3, property.getEnum().size());
		List<String> enums = property.getEnum().stream().filter(o -> o instanceof String).map(o -> (String) o)
				.collect(Collectors.toList());
		assertTrue(enums.contains(EnumValue.FIRST.name()));
		assertTrue(enums.contains(EnumValue.SECOND.name()));
		assertTrue(enums.contains(EnumValue.THIRD.name()));
		property = schema.getProperties().get("n1");
		assertNotNull(property);
		assertEquals("object", property.getType());
		@SuppressWarnings("rawtypes")
		Map<String, Schema> nested = property.getProperties();
		assertNotNull(nested);
		assertEquals(2, nested.size());
		property = nested.get("v1");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = nested.get("v2");
		assertNotNull(property);
		assertEquals("string", property.getType());
	}

	private static void validateSet4(Schema<?> schema) {
		assertNotNull(schema);
		assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(4, schema.getProperties().size());
		Schema<?> property = schema.getProperties().get("str");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = schema.getProperties().get("enm");
		assertNotNull(property);
		assertEquals("string", property.getType());
		assertNotNull(property.getEnum());
		assertEquals(3, property.getEnum().size());
		List<String> enums = property.getEnum().stream().filter(o -> o instanceof String).map(o -> (String) o)
				.collect(Collectors.toList());
		assertTrue(enums.contains(EnumValue.FIRST.name()));
		assertTrue(enums.contains(EnumValue.SECOND.name()));
		assertTrue(enums.contains(EnumValue.THIRD.name()));
		property = schema.getProperties().get("n1");
		assertNotNull(property);
		assertEquals("object", property.getType());
		@SuppressWarnings("rawtypes")
		Map<String, Schema> nested = property.getProperties();
		assertNotNull(nested);
		assertEquals(2, nested.size());
		property = nested.get("v1");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = nested.get("v2");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = schema.getProperties().get("n2");
		assertNotNull(property);
		assertEquals("object", property.getType());
		nested = property.getProperties();
		assertNotNull(nested);
		assertEquals(3, nested.size());
		property = nested.get("v1");
		assertNotNull(property);
		assertEquals("integer", property.getType());
		property = nested.get("v2");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = nested.get("n3");
		assertNotNull(property);
		assertEquals("object", property.getType());
		nested = property.getProperties();
		assertNotNull(nested);
		assertEquals(2, nested.size());
		property = nested.get("v1");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = nested.get("v2");
		assertNotNull(property);
		assertEquals("number", property.getType());
	}

	private static void validateSet5(Schema<?> schema) {
		assertNotNull(schema);
		assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(2, schema.getProperties().size());
		Schema<?> property = schema.getProperties().get("str");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = schema.getProperties().get("cpbx");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArraySchema);
		ArraySchema array = ((ArraySchema) property);
		assertNotNull(array.getItems());
		assertEquals("object", array.getItems().getType());
		assertNotNull(array.getItems().getProperties());
		assertEquals(2, array.getItems().getProperties().size());
	}

}
