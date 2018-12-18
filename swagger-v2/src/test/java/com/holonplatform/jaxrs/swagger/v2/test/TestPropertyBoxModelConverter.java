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

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.holonplatform.jaxrs.swagger.v2.SwaggerV2;
import com.holonplatform.jaxrs.swagger.v2.test.model.AbstractTestResource;
import com.holonplatform.jaxrs.swagger.v2.test.utils.SwaggerValidation;

import io.swagger.annotations.Api;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import io.swagger.util.Yaml;

public class TestPropertyBoxModelConverter {

	@Api
	@Path("resource1")
	private static class TestResource1 extends AbstractTestResource {

	}

	@Test
	public void testPropertyBoxConversion() {

		final BeanConfig configuration = new BeanConfig();
		configuration.setInfo(new Info().title("Test PropertyBox").version("1"));

		Set<Class<?>> classes = new HashSet<>();
		classes.add(TestResource1.class);
		classes.add(SwaggerV2.CONTEXT_READER_LISTENER);

		Reader reader = new Reader(configuration.configure(new Swagger()));
		Swagger api = reader.read(classes);
		
		try {
			System.err.println(Yaml.pretty().writeValueAsString(api));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SwaggerValidation.validateTestResourceApi(api, "Test PropertyBox");

	}

}
