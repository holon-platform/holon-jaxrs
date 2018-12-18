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
package com.holonplatform.jaxrs.swagger.v2.test.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.jaxrs.swagger.annotations.ApiPropertySetModel;

public abstract class AbstractTestResource {

	@GET
	@Path("test1")
	@Produces(MediaType.APPLICATION_JSON)
	public @PropertySetRef(Model1.class) PropertyBox test1() {
		return PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build();
	}

	@PUT
	@Path("test2")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test2(@SuppressWarnings("unused") @PropertySetRef(value = Model1.class) PropertyBox propertyBox) {
		return Response.accepted().build();
	}

	@GET
	@Path("test3")
	@Produces(MediaType.APPLICATION_JSON)
	public @PropertySetRef(Model1.class) PropertyBox[] test3() {
		return new PropertyBox[] { PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build() };
	}

	@GET
	@Path("test4")
	@Produces(MediaType.APPLICATION_JSON)
	public @PropertySetRef(Model1.class) List<PropertyBox> test4() {
		return Collections.singletonList(PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build());
	}

	@GET
	@Path("test5")
	@Produces(MediaType.APPLICATION_JSON)
	public @PropertySetRef(Model1.class) Set<PropertyBox> test5() {
		return Collections.singleton(PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build());
	}

	@GET
	@Path("test6")
	@Produces(MediaType.APPLICATION_JSON)
	public @PropertySetRef(Model2.class) PropertyBox test6() {
		return PropertyBox.builder(Model2.PROPERTIES).set(Model2.ID, 1).build();
	}

	@PUT
	@Path("test7")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test7(@SuppressWarnings("unused") @PropertySetRef(value = Model2.class) PropertyBox propertyBox) {
		return Response.accepted().build();
	}

	@GET
	@Path("test8")
	@Produces(MediaType.APPLICATION_JSON)
	public TestData test8() {
		TestData d = new TestData();
		d.setId(1L);
		d.setName("test");
		return d;
	}

	@PUT
	@Path("test9")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test9(@SuppressWarnings("unused") TestData data) {
		return Response.accepted().build();
	}

	@GET
	@Path("test10")
	@Produces(MediaType.TEXT_PLAIN)
	public String test10() {
		return "test";
	}

	@GET
	@Path("test11")
	@Produces(MediaType.APPLICATION_JSON)
	public @ModelOne PropertyBox test11() {
		return PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build();
	}

	@PUT
	@Path("test12")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test12(@SuppressWarnings("unused") @ModelOne PropertyBox propertyBox) {
		return Response.accepted().build();
	}

	@GET
	@Path("test13")
	@Produces(MediaType.APPLICATION_JSON)
	public @ModelOne PropertyBox[] test13() {
		return new PropertyBox[] { PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build() };
	}

	@GET
	@Path("test14")
	@Produces(MediaType.APPLICATION_JSON)
	public @ModelOne List<PropertyBox> test14() {
		return Collections.singletonList(PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build());
	}

	@GET
	@Path("test15")
	@Produces(MediaType.APPLICATION_JSON)
	public @ModelOne Set<PropertyBox> test15() {
		return Collections.singleton(PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build());
	}

	@GET
	@Path("test16")
	@Produces(MediaType.APPLICATION_JSON)
	public @ModelTwo PropertyBox test16() {
		return PropertyBox.builder(Model2.PROPERTIES).set(Model2.ID, 1).build();
	}

	@PUT
	@Path("test17")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test17(@SuppressWarnings("unused") @ModelTwo PropertyBox propertyBox) {
		return Response.accepted().build();
	}

	@GET
	@Path("test18")
	@Produces(MediaType.APPLICATION_JSON)
	public @PropertySetRef(value = ModelTest.class, field = "SET1") PropertyBox test18() {
		return PropertyBox.builder(ModelTest.SET1).set(ModelTest.STR, "test").build();
	}

	@PUT
	@Path("test19")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test19(
			@SuppressWarnings("unused") @ApiPropertySetModel("Set1") @PropertySetRef(value = ModelTest.class, field = "SET1") PropertyBox propertyBox) {
		return Response.accepted().build();
	}

	@GET
	@Path("test20")
	@Produces(MediaType.APPLICATION_JSON)
	public @PropertySetRef(value = ModelTest.class, field = "SET2") PropertyBox test20() {
		return PropertyBox.builder(ModelTest.SET2).set(ModelTest.STR, "test").build();
	}

	@GET
	@Path("test21")
	@Produces(MediaType.APPLICATION_JSON)
	public @PropertySetRef(value = ModelTest.class, field = "SET3") PropertyBox test21() {
		return PropertyBox.builder(ModelTest.SET3).set(ModelTest.STR, "test").build();
	}

	@GET
	@Path("test22")
	@Produces(MediaType.APPLICATION_JSON)
	public @PropertySetRef(value = ModelTest.class, field = "SET4") PropertyBox test22() {
		return PropertyBox.builder(ModelTest.SET4).set(ModelTest.STR, "test").build();
	}

	@GET
	@Path("test23")
	@Produces(MediaType.APPLICATION_JSON)
	public @PropertySetRef(value = ModelTest.class, field = "SET5") PropertyBox test23() {
		return PropertyBox.builder(ModelTest.SET5).set(ModelTest.STR, "test").build();
	}

}
