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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.jaxrs.swagger.v3.OpenAPIContextListener;
import com.holonplatform.jaxrs.swagger.v3.test.TestJerseyPropertyBoxModelConverter.Config;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class TestPropertyBoxModelConverter {

	public static class Properties1 {
		public static final NumericProperty<Integer> ID = NumericProperty.integerType("id1");
		public static final StringProperty NAME = StringProperty.create("name1");
		public static final PropertySet<?> PROPERTIES = PropertySet.of(ID, NAME);
	}

	public static class Properties2 {
		public static final NumericProperty<Integer> ID = NumericProperty.integerType("id2");
		public static final StringProperty NAME = StringProperty.create("name2");
		public static final PropertySet<?> PROPERTIES = PropertySet.of(ID, NAME);
	}

	@SuppressWarnings("unused")
	@Path("resource1")
	public static class TestResource1 {

		@GET
		@Path("test1")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Properties1.class) PropertyBox test1() {
			return PropertyBox.builder(Properties1.PROPERTIES).set(Config.ID, 1).build();
		}

		@PUT
		@Path("test2")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response test2(@PropertySetRef(value = Properties1.class) PropertyBox propertyBox) {
			return Response.accepted().build();
		}

		@GET
		@Path("test3")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Properties1.class) PropertyBox[] test3() {
			return new PropertyBox[] { PropertyBox.builder(Properties1.PROPERTIES).set(Config.ID, 1).build() };
		}

		@GET
		@Path("test4")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Properties1.class) List<PropertyBox> test4() {
			return Collections.singletonList(PropertyBox.builder(Properties1.PROPERTIES).set(Config.ID, 1).build());
		}

		@GET
		@Path("test5")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Properties1.class) Set<PropertyBox> test5() {
			return Collections.singleton(PropertyBox.builder(Properties1.PROPERTIES).set(Config.ID, 1).build());
		}

		@GET
		@Path("test6")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(Properties2.class) PropertyBox test6() {
			return PropertyBox.builder(Properties2.PROPERTIES).set(Config.ID, 1).build();
		}

		@PUT
		@Path("test7")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response test7(@PropertySetRef(value = Properties2.class) PropertyBox propertyBox) {
			return Response.accepted().build();
		}

	}

	@Test
	public void testPropertyBoxConversion() throws JsonProcessingException {

		final SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration.setOpenAPI(new OpenAPI().info(new Info().title("Test PropertyBox")));
		
		Set<Class<?>> classes = new HashSet<>();
		classes.add(TestResource1.class);
		classes.add(OpenAPIContextListener.class);
		
		Reader reader = new Reader(new SwaggerConfiguration());
		OpenAPI openAPI = reader.read(classes);

		assertNotNull(openAPI);

		//System.err.println(Yaml.mapper().writeValueAsString(openAPI));

	}

}
