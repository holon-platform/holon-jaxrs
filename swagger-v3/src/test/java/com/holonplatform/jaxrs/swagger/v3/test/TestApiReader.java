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

import java.util.Collections;

import javax.ws.rs.Path;

import org.junit.jupiter.api.Test;

import com.holonplatform.jaxrs.swagger.v3.SwaggerV3;
import com.holonplatform.jaxrs.swagger.v3.test.model.AbstractTestResource;

import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class TestApiReader {

	@Path("resource1")
	private static class TestResource1 extends AbstractTestResource {

	}

	@Test
	public void testApiReader() {

		final SwaggerConfiguration configuration = new SwaggerConfiguration()
				.openAPI(new OpenAPI().info(new Info().title("Test PropertyBox").version("1")));

		OpenAPI api = SwaggerV3.reader(configuration).read(Collections.singleton(TestResource1.class));
		validateTestResourceApi(api, "Test PropertyBox");

	}

}
