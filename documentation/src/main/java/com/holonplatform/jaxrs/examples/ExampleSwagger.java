/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.jaxrs.examples;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.jaxrs.swagger.ApiReader;
import com.holonplatform.jaxrs.swagger.annotations.ApiPropertySetModel;
import com.holonplatform.jaxrs.swagger.v2.SwaggerV2;
import com.holonplatform.jaxrs.swagger.v3.SwaggerV3;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;

@SuppressWarnings("unused")
public class ExampleSwagger {

	// tag::model[]
	public interface SubjectModel {

		static final NumericProperty<Integer> ID = NumericProperty.integerType("id");
		static final StringProperty NAME = StringProperty.create("name");

		static final PropertySet<?> SUBJECT = PropertySet.of(ID, NAME); // <1>

	}
	// end::model[]

	// tag::propertyset[]
	@Path("subjects")
	public class Subjects {

		@GET
		@Path("{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public @PropertySetRef(SubjectModel.class) PropertyBox getById(@PathParam("id") int id) { // <1>
			return getSubjectById(id);
		}

		@PUT
		@Path("")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response create(@PropertySetRef(SubjectModel.class) PropertyBox subject) { // <2>
			createSubject(subject);
			return Response.accepted().build();
		}

	}
	// end::propertyset[]

	// tag::apimodel1[]
	@PropertySetRef(SubjectModel.class) // <1>
	@ApiPropertySetModel("Subject") // <2>
	@Target({ ElementType.PARAMETER, ElementType.TYPE, ElementType.TYPE_USE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Subject {

	}
	// end::apimodel1[]

	// tag::apimodel2[]
	@Path("subjects")
	public class Subjects2 {

		@GET
		@Path("{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public @Subject PropertyBox getById(@PathParam("id") int id) { // <1>
			return null;
		}

		@PUT
		@Path("")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response create(@Subject PropertyBox subject) { // <2>
			return Response.accepted().build();
		}

	}
	// end::apimodel2[]

	public void apireaderv2() {
		// tag::apireaderv2[]
		BeanConfig configuration = new BeanConfig();
		configuration.setInfo(new Info().title("The title").version("1"));

		ApiReader<Swagger> reader = SwaggerV2.reader(configuration); // <1>

		Swagger api = reader.read(ApiEndpoint1.class, ApiEndpoint2.class); // <2>

		String json = SwaggerV2.asJson(api); // <3>
		String yaml = SwaggerV2.asYaml(api); // <4>
		// end::apireaderv2[]
	}

	public void apireaderv3() {
		// tag::apireaderv3[]
		SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration.setOpenAPI(
				new OpenAPI().info(new io.swagger.v3.oas.models.info.Info().title("The title").version("1")));

		ApiReader<OpenAPI> reader = SwaggerV3.reader(configuration); // <1>

		OpenAPI api = reader.read(ApiEndpoint1.class, ApiEndpoint2.class); // <2>

		String json = SwaggerV3.asJson(api); // <3>
		String yaml = SwaggerV3.asYaml(api); // <4>
		// end::apireaderv3[]
	}

	private static PropertyBox getSubjectById(int id) {
		return null;
	}

	private static void createSubject(PropertyBox subject) {
	}

	private static class ApiEndpoint1 {

	}

	private static class ApiEndpoint2 {

	}

}
