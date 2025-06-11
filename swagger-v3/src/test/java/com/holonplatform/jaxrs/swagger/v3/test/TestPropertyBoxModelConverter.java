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

import static com.holonplatform.jaxrs.swagger.v3.test.utils.OpenApiValidation.validateTestResourceApi;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.Path;

import org.junit.jupiter.api.Test;

import com.holonplatform.jaxrs.swagger.v3.SwaggerV3;
import com.holonplatform.jaxrs.swagger.v3.test.model.AbstractTestResource;

import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

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
		classes.add(SwaggerV3.CONTEXT_READER_LISTENER);

		Reader reader = new Reader(configuration);
		OpenAPI api = reader.read(classes);
		validateTestResourceApi(api, "Test PropertyBox");

	}

}
