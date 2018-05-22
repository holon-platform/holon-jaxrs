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

import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.jaxrs.LogConfig;

public class TestFormData extends JerseyTest {

	public static enum TestEnum {
		A, B;
	}

	static final Property<String> STR = PathProperty.create("str", String.class);
	static final Property<Integer> INT = PathProperty.create("itg", Integer.class);
	static final Property<Double> DBL = PathProperty.create("dbl", Double.class);
	static final Property<Boolean> BLN = PathProperty.create("bln", Boolean.class);
	static final Property<TestEnum> ENM = PathProperty.create("enm", TestEnum.class);
	static final Property<Date> DAT1 = PathProperty.create("dat1", Date.class);
	static final Property<Date> DAT2 = PathProperty.create("dat2", Date.class).temporalType(TemporalType.DATE);

	public static final PropertySet<?> SET = PropertySet.of(STR, INT, DBL, BLN, ENM, DAT1, DAT2);

	@BeforeClass
	public static void setup() {
		LogConfig.setupLogging();
	}

	@Path("test")
	public static class TestResource {

		@GET
		@Path("get/{id}")
		@Produces(MediaType.APPLICATION_FORM_URLENCODED)
		public PropertyBox getBox(@PathParam("id") int id) {
			return PropertyBox.builder(SET).set(INT, id).set(STR, "" + id).set(DBL, (id + 0.5)).build();
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
		Assert.assertNotNull(box);
		Assert.assertEquals(Integer.valueOf(1), box.getValue(INT));
		Assert.assertEquals(Double.valueOf(1.5), box.getValue(DBL));
		Assert.assertEquals("1", box.getValue(STR));

		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 30);
		c.set(Calendar.HOUR_OF_DAY, 18);
		c.set(Calendar.DAY_OF_MONTH, 3);
		c.set(Calendar.MONTH, 2);
		c.set(Calendar.YEAR, 2020);

		box = PropertyBox.builder(SET).set(INT, 1).set(STR, "value1").set(DBL, 7.5).set(BLN, true).set(ENM, TestEnum.B)
				.set(DAT1, c.getTime()).set(DAT2, c.getTime()).build();

		try (Response response = target("/test/post").request()
				.post(Entity.entity(box, MediaType.APPLICATION_FORM_URLENCODED))) {
			Assert.assertEquals(200, response.getStatus());
			Assert.assertNotNull(response.getEntity());

			box = SET.execute(() -> response.readEntity(PropertyBox.class));
			Assert.assertEquals(Integer.valueOf(1), box.getValue(INT));
			Assert.assertEquals(Double.valueOf(7.5), box.getValue(DBL));
			Assert.assertEquals("value1", box.getValue(STR));
			Assert.assertTrue(box.getValue(BLN));
			Assert.assertEquals(TestEnum.B, box.getValue(ENM));

			c = Calendar.getInstance();
			c.set(Calendar.MILLISECOND, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.HOUR_OF_DAY, 0);

			c.setTime(box.getValue(DAT1));

			Assert.assertEquals(30, c.get(Calendar.MINUTE));
			Assert.assertEquals(18, c.get(Calendar.HOUR_OF_DAY));
			Assert.assertEquals(3, c.get(Calendar.DAY_OF_MONTH));
			Assert.assertEquals(2, c.get(Calendar.MONTH));
			Assert.assertEquals(2020, c.get(Calendar.YEAR));

			Calendar.getInstance();
			c.set(Calendar.MILLISECOND, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.HOUR_OF_DAY, 0);

			c.setTime(box.getValue(DAT2));

			Assert.assertEquals(0, c.get(Calendar.MINUTE));
			Assert.assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
			Assert.assertEquals(3, c.get(Calendar.DAY_OF_MONTH));
			Assert.assertEquals(2, c.get(Calendar.MONTH));
			Assert.assertEquals(2020, c.get(Calendar.YEAR));

		}

	}

}
