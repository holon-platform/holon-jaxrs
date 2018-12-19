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
package com.holonplatform.jaxrs.swagger.v2.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.jaxrs.swagger.v2.SwaggerV2;
import com.holonplatform.jaxrs.swagger.v2.test.model.Model1;
import com.holonplatform.jaxrs.swagger.v2.test.model.ModelOne;
import com.holonplatform.jaxrs.swagger.v2.test.utils.SwaggerValidation;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.ArrayModel;
import io.swagger.models.Info;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.properties.Property;

public class TestSwaggerAnnotations {

	@SwaggerDefinition(info = @io.swagger.annotations.Info(title = "Test annotations", version = "0.1.2"))
	@Path("resource")
	private static class TestResource {

		@ApiOperation(value = "Op1", notes = "The op1", produces = MediaType.APPLICATION_JSON)
		@ApiResponses({ @ApiResponse(code = 400, message = "Bad request"),
				@ApiResponse(code = 200, message = "OK", response = PropertyBox.class) })
		@GET
		@Path("test1")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Model1.class) PropertyBox test1() {
			return PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build();
		}

		@ApiOperation(value = "Op2", notes = "The op2", produces = MediaType.APPLICATION_JSON)
		@ApiResponses({ @ApiResponse(code = 403, message = "Forbidden"),
				@ApiResponse(code = 200, message = "OK", response = PropertyBox[].class) })
		@GET
		@Path("test2")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Model1.class) PropertyBox[] test2() {
			return new PropertyBox[] { PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build() };
		}

		@ApiOperation(value = "Op3", notes = "The op3", produces = MediaType.APPLICATION_JSON)
		@ApiResponses(@ApiResponse(code = 200, message = "OK", response = PropertyBox.class))
		@GET
		@Path("test3")
		@Produces(MediaType.APPLICATION_JSON)
		public @ModelOne PropertyBox test3() {
			return PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build();
		}

		@ApiOperation(value = "Op4", notes = "The op4", produces = MediaType.APPLICATION_JSON)
		@ApiResponses(@ApiResponse(code = 200, message = "OK", responseContainer = "List", response = PropertyBox.class))
		@GET
		@Path("test4")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Model1.class) List<PropertyBox> test4() {
			return Collections.singletonList(PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build());
		}

	}

	@Test
	public void testOpenApiAnnotations() {

		final BeanConfig configuration = new BeanConfig();
		configuration.setInfo(new Info().title("Test annotations").version("0.1.2"));

		Swagger api = SwaggerV2.reader(configuration).read(Collections.singleton(TestResource.class));

		// validate
		assertNotNull(api);
		assertNotNull(api.getInfo());
		assertEquals("Test annotations", api.getInfo().getTitle());
		assertEquals("0.1.2", api.getInfo().getVersion());

		// test1
		io.swagger.models.Path item = api.getPaths().get("/resource/test1");
		assertNotNull(item);
		Operation op = item.getGet();
		assertNotNull(op);
		assertEquals("Op1", op.getSummary());
		assertEquals("The op1", op.getDescription());
		assertNotNull(op.getResponses());
		assertEquals(2, op.getResponses().size());
		Response resp = op.getResponses().get("400");
		assertNotNull(resp);
		assertEquals("Bad request", resp.getDescription());

		resp = op.getResponses().get("200");
		assertEquals("OK", resp.getDescription());
		assertNotNull(resp.getResponseSchema());
		Model schema = resp.getResponseSchema();
		SwaggerValidation.validateModel1(schema);

		// test2
		item = api.getPaths().get("/resource/test2");
		assertNotNull(item);
		op = item.getGet();
		assertNotNull(op);
		assertEquals("Op2", op.getSummary());
		assertEquals("The op2", op.getDescription());
		assertNotNull(op.getResponses());
		assertEquals(2, op.getResponses().size());
		resp = op.getResponses().get("403");
		assertNotNull(resp);
		assertEquals("Forbidden", resp.getDescription());
		resp = op.getResponses().get("200");
		assertEquals("OK", resp.getDescription());
		assertNotNull(resp.getResponseSchema());
		schema = resp.getResponseSchema();
		assertTrue(schema instanceof ArrayModel);
		Property items = ((ArrayModel) schema).getItems();
		assertNotNull(items);
		SwaggerValidation.validateModel1(SwaggerValidation.propertyToModel(items));

		// test3
		item = api.getPaths().get("/resource/test3");
		assertNotNull(item);
		op = item.getGet();
		assertNotNull(op);
		assertEquals("Op3", op.getSummary());
		assertEquals("The op3", op.getDescription());
		assertNotNull(op.getResponses());
		assertEquals(1, op.getResponses().size());
		resp = op.getResponses().get("200");
		assertEquals("OK", resp.getDescription());
		assertNotNull(resp.getResponseSchema());
		schema = resp.getResponseSchema();
		assertTrue(schema instanceof RefModel);
		assertNotNull(((RefModel) schema).get$ref());
		assertTrue(((RefModel) schema).get$ref().endsWith("ModelOne"));

		assertNotNull(api.getDefinitions());
		assertTrue(api.getDefinitions().containsKey("ModelOne"));
		assertNotNull(api.getDefinitions().get("ModelOne"));
		SwaggerValidation.validateModel1(api.getDefinitions().get("ModelOne"), "ModelOne");

		// test4
		item = api.getPaths().get("/resource/test4");
		assertNotNull(item);
		op = item.getGet();
		assertNotNull(op);
		assertEquals("Op4", op.getSummary());
		assertEquals("The op4", op.getDescription());
		assertNotNull(op.getProduces());
		assertFalse(op.getProduces().isEmpty());
		assertEquals(MediaType.APPLICATION_JSON, op.getProduces().get(0));
		assertNotNull(op.getResponses());
		assertEquals(1, op.getResponses().size());
		resp = op.getResponses().get("200");
		assertEquals("OK", resp.getDescription());
		assertNotNull(resp.getResponseSchema());
		schema = resp.getResponseSchema();
		assertTrue(schema instanceof ArrayModel);
		items = ((ArrayModel) schema).getItems();
		assertNotNull(items);
		SwaggerValidation.validateModel1(SwaggerValidation.propertyToModel(items));

	}

}
