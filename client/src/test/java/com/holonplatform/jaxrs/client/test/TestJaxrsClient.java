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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.TestUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.http.HttpResponse;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.http.exceptions.UnsuccessfulResponseException;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.ResponseEntity;
import com.holonplatform.http.rest.RestClient;
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
		@Path("data2/{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getData2(@PathParam("id") int id) {
			if (id < 0) {
				return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
						.entity(new ApiError("ERR000", "Invalid data")).build();
			}
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(new TestData(id, "value" + id)).build();
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

		@GET
		@Path("stream")
		@Produces(MediaType.APPLICATION_OCTET_STREAM)
		public StreamingOutput getStream() {
			return new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					output.write(new byte[] { 1, 2, 3 });
				}
			};
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
			if (data == null || data.getCode() < 0) {
				return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
						.entity(new ApiError("ERR000", "Invalid data")).build();
			}
			return Response.accepted().build();
		}

		@POST
		@Path("box/post")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response postBox(@PropertySetRef(TestJaxrsClient.class) PropertyBox box) {
			assertNotNull(box);
			return Response.accepted().build();
		}

		@PUT
		@Path("box/save")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response saveBox(@PropertySetRef(TestJaxrsClient.class) PropertyBox box) {
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
				.accept(MediaType.APPLICATION_JSON).getForEntity(TestData.class).orElse(null);

		assertEquals(1, td.getCode());

		HttpResponse<TestData> rsp = client.request().path("test").path("data/{id}").resolve("id", 1)
				.get(TestData.class);
		assertEquals(HttpStatus.OK, rsp.getStatus());
		assertEquals(Integer.valueOf(1), rsp.getPayload().map(p -> p.getCode()).orElse(null));

		PropertyBox box = client.request().path("test").path("box/{id}").resolve("id", 1).propertySet(PROPERTIES)
				.getForEntity(PropertyBox.class).orElse(null);
		assertNotNull(box);
		assertEquals(new Integer(1), box.getValue(CODE));
		assertEquals("value1", box.getValue(VALUE));

		HttpResponse<PropertyBox> rsp2 = client.request().path("test").path("box/{id}").resolve("id", 1)
				.propertySet(PROPERTIES).get(PropertyBox.class);
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
				.post(RequestEntity.EMPTY, TestData.class);
		assertEquals(HttpStatus.OK, prsp.getStatus());
		assertTrue(prsp.getPayload().isPresent());

		PropertyBox postBox = PropertyBox.builder(PROPERTIES).set(CODE, 100).set(VALUE, "post").build();
		HttpResponse<Void> postResponse = client.request().path("test").path("box/post")
				.post(RequestEntity.json(postBox));
		assertEquals(HttpStatus.ACCEPTED, postResponse.getStatus());

	}

	@Test
	public void testMethods() {
		final RestClient client = RestClient.forTarget(getBaseUri());

		HttpResponse<?> rsp = client.request().path("test").path("data/save")
				.put(RequestEntity.json(new TestData(7, "testPost")));
		assertNotNull(rsp);
		assertEquals(HttpStatus.ACCEPTED, rsp.getStatus());

		rsp = client.request().path("test").path("formParams")
				.post(RequestEntity.form(RequestEntity.formBuilder().set("one", "1").set("two", "1").build()));
		assertNotNull(rsp);
		assertEquals(HttpStatus.OK, rsp.getStatus());

	}

	@Test
	public void testStream() throws IOException {

		final RestClient client = RestClient.create(JaxrsRestClient.class.getName()).defaultTarget(getBaseUri());

		@SuppressWarnings("resource")
		InputStream s = client.request().path("test").path("stream").getForStream();
		assertNotNull(s);

		byte[] bytes = ConversionUtils.convertInputStreamToBytes(s);
		assertNotNull(s);
		Assert.assertTrue(Arrays.equals(new byte[] { 1, 2, 3 }, bytes));
	}

	@Test
	public void testMultipleReads() {
		final RestClient client = RestClient.create(JaxrsRestClient.class.getName()).defaultTarget(getBaseUri());

		ResponseEntity<TestData> res = client.request().path("test").path("data2/{id}").resolve("id", 1)
				.get(TestData.class);
		assertNotNull(res);

		TestData td = res.getPayload().orElse(null);
		assertNotNull(td);

		String asString = res.as(String.class).orElse(null);
		assertNotNull(asString);

		td = res.as(TestData.class).orElse(null);
		assertNotNull(td);

	}

	@Test
	public void testErrors() {
		final RestClient client = JaxrsRestClient.create(getClient()).defaultTarget(getBaseUri());

		ResponseEntity<?> rsp = client.request().path("test").path("data/save")
				.put(RequestEntity.json(new TestData(-1, "testErr")));

		assertNotNull(rsp);
		assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatus());

		ApiError error = rsp.as(ApiError.class).orElse(null);
		assertNotNull(error);
		assertEquals("ERR000", error.getCode());

		ResponseEntity<TestData> r2 = client.request().path("test").path("data2/{id}").resolve("id", -1)
				.get(TestData.class);
		assertNotNull(r2);
		assertEquals(HttpStatus.BAD_REQUEST, r2.getStatus());

		error = r2.as(ApiError.class).orElse(null);
		assertNotNull(error);
		assertEquals("ERR000", error.getCode());

		TestUtils.expectedException(UnsuccessfulResponseException.class, () -> {
			client.request().path("test").path("data2/{id}").resolve("id", -1).getForEntity(TestData.class)
					.orElse(null);
		});

		try {
			client.request().path("test").path("data2/{id}").resolve("id", -1).getForEntity(TestData.class)
					.orElse(null);
		} catch (UnsuccessfulResponseException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus().orElse(null));
			assertNotNull(e.getResponse());

			ApiError err = e.getResponse().as(ApiError.class).orElse(null);
			assertNotNull(err);
			assertEquals("ERR000", err.getCode());
		}
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

	public static class ApiError {

		private String code;
		private String message;

		public ApiError() {
			super();
		}

		public ApiError(String code, String message) {
			super();
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

}
