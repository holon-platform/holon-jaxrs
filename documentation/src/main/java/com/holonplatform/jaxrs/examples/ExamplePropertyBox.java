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

import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;

@SuppressWarnings("unused")
public class ExamplePropertyBox {

	private static final Property<Integer> A_PROPERTY = PathProperty.create("a_property", Integer.class);

	private static final PropertySet<?> PROPERTY_SET = PropertySet.of(A_PROPERTY);

	// tag::json[]
	@Path("propertybox")
	public static class JsonEndpoint {

		@GET
		@Path("get")
		@Produces(MediaType.APPLICATION_JSON)
		public PropertyBox getPropertyBox() { // <1>
			return PropertyBox.builder(PROPERTY_SET).set(A_PROPERTY, 1).build();
		}

		@GET
		@Path("getList")
		@Produces(MediaType.APPLICATION_JSON)
		public List<PropertyBox> getPropertyBoxList() { // <2>
			return Collections.singletonList(PropertyBox.builder(PROPERTY_SET).set(A_PROPERTY, 1).build());
		}

		@PUT
		@Path("put")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response putPropertyBox(@PropertySetRef(ExamplePropertyBox.class) PropertyBox data) { // <3>
			return Response.accepted().build();
		}

	}
	// end::json[]

	// tag::form[]
	@Path("propertybox")
	public static class FormDataEndpoint {

		@POST
		@Path("post")
		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		public Response postPropertyBox(@PropertySetRef(ExamplePropertyBox.class) PropertyBox data) { // <1>
			return Response.ok().build();
		}

	}
	// end::form[]

}
