/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.jaxrs.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.jaxrs.LogConfig;
import com.holonplatform.test.JerseyTest5;

public class TestFormData extends JerseyTest5 {

	public static enum TestEnum {
		A, B;
	}

	static final Property<String> STR1 = PathProperty.create("str", String.class);
	static final Property<Integer> INT = PathProperty.create("itg", Integer.class);
	static final Property<Double> DBL = PathProperty.create("dbl", Double.class);
	static final Property<Boolean> BLN = PathProperty.create("bln", Boolean.class);
	static final Property<TestEnum> ENM = PathProperty.create("enm", TestEnum.class);
	static final Property<Date> DAT1 = PathProperty.create("dat1", Date.class);
	static final Property<Date> DAT2 = PathProperty.create("dat2", Date.class).temporalType(TemporalType.DATE);

	public static final PropertySet<?> SET = PropertySet.of(STR1, INT, DBL, BLN, ENM, DAT1, DAT2);

	@BeforeAll
	static void setup() {
		LogConfig.setupLogging();
	}

	@Path("test")
	public static class TestResource {

		@GET
		@Path("get/{id}")
		@Produces(MediaType.APPLICATION_FORM_URLENCODED)
		public PropertyBox getBox(@PathParam("id") int id) {
			return PropertyBox.builder(SET).set(INT, id).set(STR1, "" + id).set(DBL, (id + 0.5)).build();
		}

		@POST
		@Path("post")
		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		@Produces(MediaType.APPLICATION_FORM_URLENCODED)
		public Response postBox(@PropertySetRef(TestFormData.class) PropertyBox box) {
			return Response.ok().entity(box).build();
		}

	}

	@Override
	protected Application configure() {
		return new ResourceConfig().register(LoggingFeature.class).register(TestResource.class);
		// .register(FormDataPropertyBoxFeature.class); // using auto-config
	}

	@Override
	protected void configureClient(ClientConfig config) {
		// config.register(FormDataPropertyBoxFeature.class); // using auto-config
	}

	@Test
	public void testMethods() {

		PropertyBox box = SET.execute(() -> target("/test/get/1").request().get(PropertyBox.class));
		assertNotNull(box);
		assertEquals(Integer.valueOf(1), box.getValue(INT));
		assertEquals(Double.valueOf(1.5), box.getValue(DBL));
		assertEquals("1", box.getValue(STR1));

		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 30);
		c.set(Calendar.HOUR_OF_DAY, 18);
		c.set(Calendar.DAY_OF_MONTH, 3);
		c.set(Calendar.MONTH, 2);
		c.set(Calendar.YEAR, 2020);

		box = PropertyBox.builder(SET).set(INT, 1).set(STR1, "value1").set(DBL, 7.5).set(BLN, true).set(ENM, TestEnum.B)
				.set(DAT1, c.getTime()).set(DAT2, c.getTime()).build();

		try (Response response = target("/test/post").request()
				.post(Entity.entity(box, MediaType.APPLICATION_FORM_URLENCODED))) {
			assertEquals(200, response.getStatus());
			assertNotNull(response.getEntity());

			box = SET.execute(() -> response.readEntity(PropertyBox.class));
			assertEquals(Integer.valueOf(1), box.getValue(INT));
			assertEquals(Double.valueOf(7.5), box.getValue(DBL));
			assertEquals("value1", box.getValue(STR1));
			assertTrue(box.getValue(BLN));
			assertEquals(TestEnum.B, box.getValue(ENM));

			c = Calendar.getInstance();
			c.set(Calendar.MILLISECOND, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.HOUR_OF_DAY, 0);

			c.setTime(box.getValue(DAT1));

			assertEquals(30, c.get(Calendar.MINUTE));
			assertEquals(18, c.get(Calendar.HOUR_OF_DAY));
			assertEquals(3, c.get(Calendar.DAY_OF_MONTH));
			assertEquals(2, c.get(Calendar.MONTH));
			assertEquals(2020, c.get(Calendar.YEAR));

			Calendar.getInstance();
			c.set(Calendar.MILLISECOND, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.HOUR_OF_DAY, 0);

			c.setTime(box.getValue(DAT2));

			assertEquals(0, c.get(Calendar.MINUTE));
			assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
			assertEquals(3, c.get(Calendar.DAY_OF_MONTH));
			assertEquals(2, c.get(Calendar.MONTH));
			assertEquals(2020, c.get(Calendar.YEAR));

		}

	}

}
