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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import com.holonplatform.jaxrs.swagger.v2.test.model.EnumValue;

import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;

public final class SwaggerValidation {

	private SwaggerValidation() {
	}

	public static void validateApi(Swagger api, String title, int pathsCount) {

		assertNotNull(api);

		if (title == null) {
			if (api.getInfo() != null) {
				assertNull(api.getInfo().getTitle());
			}
		} else {
			assertNotNull(api.getInfo());
			assertEquals(title, api.getInfo().getTitle());
		}

		assertNotNull(api.getPaths());
		assertEquals(pathsCount, api.getPaths().size());

	}

	public static void validateModel1Api(Swagger api, String path, String title) {
		assertNotNull(api);

		if (title == null) {
			if (api.getInfo() != null) {
				assertNull(api.getInfo().getTitle());
			}
		} else {
			assertNotNull(api.getInfo());
			assertEquals(title, api.getInfo().getTitle());
		}

		assertNotNull(api.getPaths());
		assertEquals(1, api.getPaths().size());

		validateModel1(validateOperationResponse(api, path));
	}

	public static void validateTestResourceApi(Swagger api, String title) {
		validateTestResourceApi(api, "/resource1", title);
	}

	public static void validateTestResourceApi(Swagger api, String path, String title) {

		assertNotNull(api);

		if (title == null) {
			if (api.getInfo() != null) {
				assertNull(api.getInfo().getTitle());
			}
		} else {
			assertNotNull(api.getInfo());
			assertEquals(title, api.getInfo().getTitle());
		}

		assertNotNull(api.getPaths());
		assertEquals(23, api.getPaths().size());

		validateModel1(validateOperationResponse(api, path + "/test1"));
		validateModel1(validateOperationBody(api, path + "/test2"));
		validateModel1(validateOperationResponseArray(api, path + "/test3", false));
		validateModel1(validateOperationResponseArray(api, path + "/test4", false));
		validateModel1(validateOperationResponseArray(api, path + "/test5", true));
		validateModel2(validateOperationResponse(api, path + "/test6"));
		validateModel2(validateOperationBody(api, path + "/test7"));
		validateTestData(validateOperationResponse(api, path + "/test8"));
		validateTestData(validateOperationBody(api, path + "/test9"));
		validateStringPlainResponse(api, path + "/test10");
		validateModelOne(api, validateOperationResponse(api, path + "/test11"));
		validateModelOne(api, validateOperationBody(api, path + "/test12"));
		validateModelOne(api, validateOperationResponseArray(api, path + "/test13", false));
		validateModelOne(api, validateOperationResponseArray(api, path + "/test14", false));
		validateModelOne(api, validateOperationResponseArray(api, path + "/test15", true));
		validateModelTwo(api, validateOperationResponse(api, path + "/test16"));
		validateModelTwo(api, validateOperationBody(api, path + "/test17"));
		validateSet1(validateOperationResponse(api, path + "/test18"));
		validateSet1Model(api, validateOperationBody(api, path + "/test19"));
		validateSet2(validateOperationResponse(api, path + "/test20"));
		validateSet3(validateOperationResponse(api, path + "/test21"));
		validateSet4(validateOperationResponse(api, path + "/test22"));
		validateSet5(validateOperationResponse(api, path + "/test23"));

	}

	public static Model validateOperationResponse(Swagger api, String path) {
		assertTrue(api.getPaths().containsKey(path));
		Path item = api.getPaths().get(path);
		assertNotNull(item);
		Operation op = item.getGet();
		assertNotNull(op);
		assertNotNull(op.getResponses());
		assertEquals(1, op.getResponses().size());
		Response resp = op.getResponses().values().iterator().next();
		assertNotNull(resp);
		assertNotNull(resp.getResponseSchema());
		return resp.getResponseSchema();
	}

	public static Model validateOperationResponseArray(Swagger api, String path, boolean uniqueItems) {
		assertTrue(api.getPaths().containsKey(path));
		Path item = api.getPaths().get(path);
		assertNotNull(item);
		Operation op = item.getGet();
		assertNotNull(op);
		assertNotNull(op.getResponses());
		assertEquals(1, op.getResponses().size());
		Response resp = op.getResponses().values().iterator().next();
		assertNotNull(resp);
		Model array = resp.getResponseSchema();
		assertNotNull(array);
		assertTrue(array instanceof ArrayModel);
		assertEquals(((ArrayModel)array).getUniqueItems(), uniqueItems);
		return propertyToModel(((ArrayModel) array).getItems());
	}

	public static Model validateOperationBody(Swagger api, String path) {
		assertTrue(api.getPaths().containsKey(path));
		Path item = api.getPaths().get(path);
		assertNotNull(item);
		Operation op = item.getPut();
		assertNotNull(op.getParameters());
		assertEquals(1, op.getParameters().size());
		Parameter pm = op.getParameters().get(0);
		assertNotNull(pm);
		assertTrue(pm instanceof BodyParameter);
		return ((BodyParameter)pm).getSchema();
	}

	public static void validateStringPlainResponse(Swagger api, String path) {
		assertTrue(api.getPaths().containsKey(path));
		Path item = api.getPaths().get(path);
		assertNotNull(item);
		Operation op = item.getGet();
		assertNotNull(op);
		assertNotNull(op.getResponses());
		assertEquals(1, op.getResponses().size());
		Response resp = op.getResponses().values().iterator().next();
		assertNotNull(resp);
		Model schema = resp.getResponseSchema();
		assertNotNull(schema);
		// TODO
		//assertEquals("string", schema.getType());
	}

	public static void validateTestData(Model schema) {
		assertNotNull(schema);
		assertTrue(schema instanceof RefModel);
		assertNotNull(((RefModel)schema).get$ref());
		assertTrue(((RefModel)schema).get$ref().endsWith("TestData"));
	}

	public static void validateModel1(Model schema) {
		validateModel1(schema, "PropertyBox");
	}

	public static void validateModel1(Model schema, String title) {
		assertNotNull(schema);
		assertEquals(title, schema.getTitle());
		//assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(2, schema.getProperties().size());
		assertTrue(schema.getProperties().containsKey("id1"));
		Property property = schema.getProperties().get("id1");
		assertNotNull(property);
		assertEquals("integer", property.getType());
		assertTrue(schema.getProperties().containsKey("name1"));
		property = schema.getProperties().get("name1");
		assertNotNull(property);
		assertEquals("string", property.getType());
	}

	public static void validateModel2(Model schema) {
		validateModel2(schema, "PropertyBox");
	}

	private static void validateModel2(Model schema, String title) {
		assertNotNull(schema);
		assertEquals(title, schema.getTitle());
		//assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(2, schema.getProperties().size());
		assertTrue(schema.getProperties().containsKey("id2"));
		Property property = schema.getProperties().get("id2");
		assertNotNull(property);
		assertEquals("integer", property.getType());
		assertTrue(schema.getProperties().containsKey("name2"));
		property = schema.getProperties().get("name2");
		assertNotNull(property);
		assertEquals("string", property.getType());
	}

	public static void validateModelOne(Swagger api, Model schema) {
		assertNotNull(schema);
		assertTrue(schema instanceof RefModel);
		assertTrue(((RefModel)schema).get$ref().endsWith("ModelOne"));
		assertNotNull(api.getDefinitions());
		Map<String, Model> schemas = api.getDefinitions();
		assertTrue(schemas.containsKey("ModelOne"));
		validateModel1(schemas.get("ModelOne"), "ModelOne");
	}

	public static void validateModelTwo(Swagger api, Model schema) {
		assertNotNull(schema);
		assertTrue(schema instanceof RefModel);
		assertNotNull(((RefModel)schema).get$ref());
		assertTrue(((RefModel)schema).get$ref().endsWith("ModelTwo"));
		assertNotNull(api.getDefinitions());
		Map<String, Model> schemas = api.getDefinitions();
		assertTrue(schemas.containsKey("ModelTwo"));
		validateModel2(schemas.get("ModelTwo"), "ModelTwo");
	}

	public static void validateSet1Model(Swagger api, Model schema) {
		assertNotNull(schema);
		assertTrue(schema instanceof RefModel);
		assertNotNull(((RefModel)schema).get$ref());
		assertTrue(((RefModel)schema).get$ref().endsWith("Set1"));
		assertNotNull(api.getDefinitions());
		Map<String, Model> schemas = api.getDefinitions();
		assertTrue(schemas.containsKey("Set1"));
		validateSet1(schemas.get("Set1"), "Set1");
	}

	public static void validateSet1(Model schema) {
		validateSet1(schema, "PropertyBox");
	}

	public static void validateSet1(Model schema, String title) {
		assertNotNull(schema);
		assertEquals(title, schema.getTitle());
		//assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(24, schema.getProperties().size());
		Property property = schema.getProperties().get("str");
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
		assertNotNull(((StringProperty)property).getEnum());
		assertEquals(3, ((StringProperty)property).getEnum().size());
		List<String> enums = ((StringProperty)property).getEnum();
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
		assertTrue(property instanceof ArrayProperty);
		assertNotNull(((ArrayProperty) property).getItems());
		assertEquals("string", ((ArrayProperty) property).getItems().getType());
		property = schema.getProperties().get("aint");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArrayProperty);
		assertNotNull(((ArrayProperty) property).getItems());
		assertEquals("integer", ((ArrayProperty) property).getItems().getType());
		property = schema.getProperties().get("aenm");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArrayProperty);
		assertNotNull(((ArrayProperty) property).getItems());
		assertEquals("string", ((ArrayProperty) property).getItems().getType());
		assertNotNull(((StringProperty) ((ArrayProperty) property).getItems()).getEnum());
		assertEquals(3, ((StringProperty) ((ArrayProperty) property).getItems()).getEnum().size());
		property = schema.getProperties().get("achr");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArrayProperty);
		assertNotNull(((ArrayProperty) property).getItems());
		assertEquals("string", ((ArrayProperty) property).getItems().getType());
		property = schema.getProperties().get("cstr");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArrayProperty);
		assertNotNull(((ArrayProperty) property).getItems());
		assertEquals("string", ((ArrayProperty) property).getItems().getType());
		property = schema.getProperties().get("cint");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArrayProperty);
		assertNotNull(((ArrayProperty) property).getItems());
		assertEquals("integer", ((ArrayProperty) property).getItems().getType());
		assertEquals("int32", ((ArrayProperty) property).getItems().getFormat());
		property = schema.getProperties().get("clng");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArrayProperty);
		assertNotNull(((ArrayProperty) property).getItems());
		assertEquals("integer", ((ArrayProperty) property).getItems().getType());
		assertEquals("int64", ((ArrayProperty) property).getItems().getFormat());
		property = schema.getProperties().get("cenm");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArrayProperty);
		assertNotNull(((ArrayProperty) property).getItems());
		assertEquals("string", ((ArrayProperty) property).getItems().getType());
		assertNotNull(((StringProperty) ((ArrayProperty) property).getItems()).getEnum());
		assertEquals(3, ((StringProperty) ((ArrayProperty) property).getItems()).getEnum().size());
		property = schema.getProperties().get("nbl");
		assertNotNull(property);
		assertEquals("boolean", property.getType());
	}

	public static void validateSet2(Model schema) {
		assertNotNull(schema);
		//assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(4, schema.getProperties().size());
		Property property = schema.getProperties().get("str");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = schema.getProperties().get("enm");
		assertNotNull(property);
		assertEquals("string", property.getType());
		assertNotNull(((StringProperty)property).getEnum());
		assertEquals(3, ((StringProperty)property).getEnum().size());
		List<String> enums = ((StringProperty)property).getEnum();
		assertTrue(enums.contains(EnumValue.FIRST.name()));
		assertTrue(enums.contains(EnumValue.SECOND.name()));
		assertTrue(enums.contains(EnumValue.THIRD.name()));
		property = schema.getProperties().get("n1");
		assertNotNull(property);
		assertEquals("object", property.getType());
		Map<String, Property> nested = ((ObjectProperty)property).getProperties();
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
		nested = ((ObjectProperty)property).getProperties();
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
		nested = ((ObjectProperty)property).getProperties();
		assertNotNull(nested);
		assertEquals(2, nested.size());
		property = nested.get("v1");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = nested.get("v2");
		assertNotNull(property);
		assertEquals("number", property.getType());
	}

	public static void validateSet3(Model schema) {
		assertNotNull(schema);
		//assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(3, schema.getProperties().size());
		Property property = schema.getProperties().get("str");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = schema.getProperties().get("enm");
		assertNotNull(property);
		assertEquals("string", property.getType());
		assertNotNull(((StringProperty)property).getEnum());
		assertEquals(3, ((StringProperty)property).getEnum().size());
		List<String> enums = ((StringProperty)property).getEnum();
		assertTrue(enums.contains(EnumValue.FIRST.name()));
		assertTrue(enums.contains(EnumValue.SECOND.name()));
		assertTrue(enums.contains(EnumValue.THIRD.name()));
		property = schema.getProperties().get("n1");
		assertNotNull(property);
		assertEquals("object", property.getType());
		Map<String, Property> nested = ((ObjectProperty)property).getProperties();
		assertNotNull(nested);
		assertEquals(2, nested.size());
		property = nested.get("v1");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = nested.get("v2");
		assertNotNull(property);
		assertEquals("string", property.getType());
	}

	public static void validateSet4(Model schema) {
		assertNotNull(schema);
		//assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(4, schema.getProperties().size());
		Property property = schema.getProperties().get("str");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = schema.getProperties().get("enm");
		assertNotNull(property);
		assertEquals("string", property.getType());
		assertNotNull(((StringProperty)property).getEnum());
		assertEquals(3, ((StringProperty)property).getEnum().size());
		List<String> enums = ((StringProperty)property).getEnum();
		assertTrue(enums.contains(EnumValue.FIRST.name()));
		assertTrue(enums.contains(EnumValue.SECOND.name()));
		assertTrue(enums.contains(EnumValue.THIRD.name()));
		property = schema.getProperties().get("n1");
		assertNotNull(property);
		assertEquals("object", property.getType());
		Map<String, Property> nested = ((ObjectProperty)property).getProperties();
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
		nested = ((ObjectProperty)property).getProperties();
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
		nested = ((ObjectProperty)property).getProperties();
		assertNotNull(nested);
		assertEquals(2, nested.size());
		property = nested.get("v1");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = nested.get("v2");
		assertNotNull(property);
		assertEquals("number", property.getType());
	}

	public static void validateSet5(Model schema) {
		assertNotNull(schema);
		//assertEquals("object", schema.getType());
		assertNotNull(schema.getProperties());
		assertEquals(2, schema.getProperties().size());
		Property property = schema.getProperties().get("str");
		assertNotNull(property);
		assertEquals("string", property.getType());
		property = schema.getProperties().get("cpbx");
		assertNotNull(property);
		assertEquals("array", property.getType());
		assertTrue(property instanceof ArrayProperty);
		ArrayProperty array = ((ArrayProperty) property);
		assertNotNull(array.getItems());
		assertEquals("object", array.getItems().getType());
		assertNotNull(((ObjectProperty)array.getItems()).getProperties());
		assertEquals(2, ((ObjectProperty)array.getItems()).getProperties().size());
	}

	private static Model propertyToModel(Property property) {
		ModelImpl m = new ModelImpl();
		m.setType(ModelImpl.OBJECT);
		m.setTitle(property.getTitle());
		m.setDescription(property.getDescription());
		if (property instanceof ObjectProperty) {
			m.setProperties(((ObjectProperty)property).getProperties());
		}
		return m;
	}

}
