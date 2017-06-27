/*
 * Copyright 2000-2016 Holon TDCN.
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
package com.holonplatform.jaxrs.client.test;

import static com.holonplatform.core.property.PathProperty.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.http.HttpResponse;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.http.RequestEntity;
import com.holonplatform.http.RestClient;
import com.holonplatform.jaxrs.client.JaxrsRestClient;

public class TestJaxrsClient extends JerseyTest {

	public TestJaxrsClient() {
		super();
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	public static final PathProperty<Integer> CODE = create("code", int.class);
	public static final PathProperty<String> VALUE = create("value", String.class);

	public static final PropertySet<?> PROPERTIES = PropertySet.of(CODE, VALUE);

	@Path("test")
	public static class TestResource {

		@GET
		@Path("data/{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public TestData getData(@PathParam("id") int id) {
			return new TestData(id, "value" + id);
		}

		@GET
		@Path("box/{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public PropertyBox getBox(@PathParam("id") int id) {
			return PropertyBox.builder(PROPERTIES).set(CODE, id).set(VALUE, "value" + id).build();
		}

		@GET
		@Path("boxes")
		@Produces(MediaType.APPLICATION_JSON)
		public List<PropertyBox> getBoxes() {
			List<PropertyBox> boxes = new LinkedList<>();
			boxes.add(PropertyBox.builder(PROPERTIES).set(CODE, 1).set(VALUE, "value" + 1).build());
			boxes.add(PropertyBox.builder(PROPERTIES).set(CODE, 2).set(VALUE, "value" + 2).build());
			return boxes;
		}

		@POST
		@Path("postdata")
		@Produces(MediaType.APPLICATION_JSON)
		public TestData postData(@QueryParam("id") int id) {
			return new TestData(id, "value" + id);
		}

		@POST
		@Path("formParams")
		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		public Response formParams(@FormParam("one") String one, @FormParam("two") Integer two) {
			assertNotNull(one);
			assertNotNull(two);
			if (!two.toString().equals(one)) {
				return Response.status(Status.BAD_REQUEST).build();
			}
			return Response.ok().build();
		}

		@PUT
		@Path("data/save")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response saveData(TestData data) {
			assertNotNull(data);
			return Response.accepted().build();
		}

		@POST
		@Path("box/post")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response postBox(PropertyBox box) {
			assertNotNull(box);
			return Response.accepted().build();
		}

		@PUT
		@Path("box/save")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response saveBox(PropertyBox box) {
			assertNotNull(box);
			return Response.accepted().build();
		}

	}

	@Override
	protected Application configure() {
		return new ResourceConfig(TestResource.class).register(LoggingFeature.class);
	}

	@Override
	protected void configureClient(ClientConfig config) {
		config.register(LoggingFeature.class);
	}

	@Test
	public void testClient() {

		final RestClient client = JaxrsRestClient.create(getClient()).defaultTarget(getBaseUri());

		TestData td = client.request().path("test").path("data/{id}").resolve("id", 1)
				.accept(MediaType.APPLICATION_JSON).get(TestData.class).orElse(null);

		assertEquals(1, td.getCode());

		HttpResponse<TestData> rsp = client.request().path("test").path("data/{id}").resolve("id", 1)
				.getResponse(TestData.class);
		assertEquals(HttpStatus.OK, rsp.getStatus());
		assertEquals(Integer.valueOf(1), rsp.getPayload().map(p -> p.getCode()).orElse(null));

		PropertyBox box = client.request().path("test").path("box/{id}").resolve("id", 1).propertySet(PROPERTIES)
				.get(PropertyBox.class).orElse(null);
		assertNotNull(box);
		assertEquals(new Integer(1), box.getValue(CODE));
		assertEquals("value1", box.getValue(VALUE));

		HttpResponse<PropertyBox> rsp2 = client.request().path("test").path("box/{id}").resolve("id", 1)
				.propertySet(PROPERTIES).getResponse(PropertyBox.class);
		assertEquals(HttpStatus.OK, rsp2.getStatus());
		box = rsp2.getPayload().orElse(null);
		assertNotNull(box);
		assertEquals(new Integer(1), box.getValue(CODE));
		assertEquals("value1", box.getValue(VALUE));

		List<PropertyBox> boxes = client.request().path("test").path("boxes").propertySet(PROPERTIES)
				.getAsList(PropertyBox.class);
		assertNotNull(boxes);
		assertEquals(2, boxes.size());

		box = boxes.get(0);
		assertNotNull(box);
		assertEquals(new Integer(1), box.getValue(CODE));
		assertEquals("value1", box.getValue(VALUE));

		box = boxes.get(1);
		assertNotNull(box);
		assertEquals(new Integer(2), box.getValue(CODE));
		assertEquals("value2", box.getValue(VALUE));

		List<Integer> codes = client.request().path("test").path("boxes").propertySet(PROPERTIES)
				.getAsList(PropertyBox.class).stream().map(p -> p.getValue(CODE)).collect(Collectors.toList());
		assertNotNull(codes);
		assertEquals(2, codes.size());

		List<String> values = client.request().path("test").path("boxes").propertySet(PROPERTIES)
				.getAsList(PropertyBox.class).stream().map(p -> p.getValue(VALUE)).collect(Collectors.toList());
		assertNotNull(values);
		assertEquals(2, values.size());

		HttpResponse<TestData> prsp = client.request().path("test").path("postdata").queryParameter("id", 1)
				.postForResponse(RequestEntity.EMPTY, TestData.class);
		assertEquals(HttpStatus.OK, prsp.getStatus());
		assertTrue(prsp.getPayload().isPresent());

	}

	@Test
	public void testMethods() {
		final RestClient client = JaxrsRestClient.create(getClient()).defaultTarget(getBaseUri());

		HttpResponse<?> rsp = client.request().path("test").path("data/save")
				.put(RequestEntity.json(new TestData(7, "testPost")));
		assertNotNull(rsp);
		assertEquals(HttpStatus.ACCEPTED, rsp.getStatus());

		rsp = client.request().path("test").path("formParams")
				.post(RequestEntity.form(RequestEntity.formBuilder().set("one", "1").set("two", "1").build()));
		assertNotNull(rsp);
		assertEquals(HttpStatus.OK, rsp.getStatus());

	}

	public static class TestData {

		private int code;
		private String value;

		public TestData() {
			super();
		}

		public TestData(int code, String value) {
			super();
			this.code = code;
			this.value = value;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + code;
			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestData other = (TestData) obj;
			if (code != other.code)
				return false;
			return true;
		}

	}

}
