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
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.holonplatform.jaxrs.swagger.v3.OpenAPIContextListener;
import com.holonplatform.jaxrs.swagger.v3.test.model.AbstractTestResource;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

public class TestPropertyBoxModelConverter {

	@Path("resource1")
	private static class TestResource1 extends AbstractTestResource {

	}

	@Test
	public void testPropertyBoxConversion() throws JsonProcessingException {

		final SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration.setOpenAPI(new OpenAPI().info(new Info().title("Test PropertyBox").version("1")));

		Set<Class<?>> classes = new HashSet<>();
		classes.add(TestResource1.class);
		classes.add(OpenAPIContextListener.class);

		Reader reader = new Reader(configuration);
		OpenAPI api = reader.read(classes);

		assertNotNull(api);
		assertNotNull(api.getPaths());
		assertEquals(17, api.getPaths().size());

		assertTrue(api.getPaths().containsKey("/resource1/test1"));
		PathItem item = api.getPaths().get("/resource1/test1");
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
		Schema<?> schema = mt.getSchema();
		assertNotNull(schema);

		System.err.println(Yaml.mapper().writeValueAsString(api));

	}

}
