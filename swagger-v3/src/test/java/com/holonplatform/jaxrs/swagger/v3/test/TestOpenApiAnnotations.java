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

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.jaxrs.swagger.annotations.ApiContextId;
import com.holonplatform.jaxrs.swagger.v3.SwaggerV3;
import com.holonplatform.jaxrs.swagger.v3.test.model.Model1;
import com.holonplatform.jaxrs.swagger.v3.test.model.ModelOne;
import com.holonplatform.jaxrs.swagger.v3.test.utils.OpenApiValidation;

import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;

public class TestOpenApiAnnotations {

	private static final String CONTEXT = "com.holonplatform.jaxrs.swagger.v3.test.TestOpenApiAnnotations";
	
	@ApiContextId(CONTEXT)
	@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "Test annotations", version = "0.1.2"))
	@Path("resource")
	private static class TestResource {

		@Operation(summary = "Op1", description = "The op1")
		@ApiResponses({ @ApiResponse(responseCode = "400", description = "Bad request"),
				@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PropertyBox.class))) })
		@GET
		@Path("test1")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Model1.class) PropertyBox test1() {
			return PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build();
		}

		@Operation(summary = "Op2", description = "The op2")
		@ApiResponses({ @ApiResponse(responseCode = "403", description = "Forbidden"),
				@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = PropertyBox.class)))) })
		@GET
		@Path("test2")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Model1.class) PropertyBox[] test2() {
			return new PropertyBox[] { PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build() };
		}

		@Operation(summary = "Op3", description = "The op3")
		@ApiResponses(@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PropertyBox.class))))
		@GET
		@Path("test3")
		@Produces(MediaType.APPLICATION_JSON)
		public @ModelOne PropertyBox test3() {
			return PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build();
		}

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testOpenApiAnnotations() {

		final SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration.setOpenAPI(new OpenAPI().info(new Info().title("Test annotations").version("1")));

		Set<Class<?>> classes = new HashSet<>();
		classes.add(TestResource.class);
		classes.add(SwaggerV3.CONTEXT_READER_LISTENER);

		Reader reader = new Reader(configuration);
		final OpenAPI api = reader.read(classes);

		// validate
		assertNotNull(api);
		assertNotNull(api.getInfo());
		assertEquals("Test annotations", api.getInfo().getTitle());
		assertEquals("0.1.2", api.getInfo().getVersion());

		// test1
		PathItem item = api.getPaths().get("/resource/test1");
		assertNotNull(item);
		io.swagger.v3.oas.models.Operation op = item.getGet();
		assertNotNull(op);
		assertEquals("Op1", op.getSummary());
		assertEquals("The op1", op.getDescription());
		assertNotNull(op.getResponses());
		assertEquals(2, op.getResponses().size());
		io.swagger.v3.oas.models.responses.ApiResponse resp = op.getResponses().get("400");
		assertNotNull(resp);
		assertEquals("Bad request", resp.getDescription());

		resp = op.getResponses().get("200");
		assertEquals("OK", resp.getDescription());
		assertNotNull(resp.getContent());
		assertEquals(1, resp.getContent().size());
		io.swagger.v3.oas.models.media.MediaType mt = resp.getContent().get(MediaType.APPLICATION_JSON);
		assertNotNull(mt);
		io.swagger.v3.oas.models.media.Schema<?> schema = mt.getSchema();
		OpenApiValidation.validateModel1(schema);

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
		assertNotNull(resp.getContent());
		assertEquals(1, resp.getContent().size());
		mt = resp.getContent().get(MediaType.APPLICATION_JSON);
		assertNotNull(mt);
		schema = mt.getSchema();

		assertNotNull(schema);
		assertEquals("array", schema.getType());
		assertTrue(schema instanceof io.swagger.v3.oas.models.media.ArraySchema);
		io.swagger.v3.oas.models.media.Schema<?> items = ((io.swagger.v3.oas.models.media.ArraySchema) schema)
				.getItems();
		OpenApiValidation.validateModel1(items);

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
		assertNotNull(resp.getContent());
		assertEquals(1, resp.getContent().size());
		mt = resp.getContent().get(MediaType.APPLICATION_JSON);
		assertNotNull(mt);
		schema = mt.getSchema();
		assertNotNull(schema);
		assertNotNull(schema.get$ref());
		assertTrue(schema.get$ref().endsWith("ModelOne"));
		assertNotNull(api.getComponents());
		assertNotNull(api.getComponents().getSchemas());
		Map<String, io.swagger.v3.oas.models.media.Schema> schemas = api.getComponents().getSchemas();
		assertTrue(schemas.containsKey("ModelOne"));
		OpenApiValidation.validateModel1(schemas.get("ModelOne"), "ModelOne");
	}

}
