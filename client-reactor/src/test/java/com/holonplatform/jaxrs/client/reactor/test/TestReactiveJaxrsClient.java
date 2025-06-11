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
package com.holonplatform.jaxrs.client.reactor.test;

import static com.holonplatform.core.property.PathProperty.create;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.http.exceptions.UnsuccessfulResponseException;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.ResponseEntity;
import com.holonplatform.jaxrs.client.reactor.JaxrsReactiveRestClient;
import com.holonplatform.reactor.http.ReactiveRestClient;
import com.holonplatform.test.JerseyTest5;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.StreamingOutput;

public class TestReactiveJaxrsClient extends JerseyTest5 {

	private static ExecutorService executorService;

	public static final PathProperty<Integer> CODE = create("code", int.class);
	public static final PathProperty<String> VALUE = create("value", String.class);

	public static final PropertySet<?> PROPERTIES = PropertySet.of(CODE, VALUE);

	public TestReactiveJaxrsClient() {
		super();
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	@BeforeAll
	static void initExecutor() {
		executorService = Executors.newSingleThreadExecutor();
	}

	@Path("test")
	public static class AsyncTestResource {

		@GET
		@Path("ping")
		@Produces(MediaType.TEXT_PLAIN)
		public void ping(@Suspended AsyncResponse ar) {
			executorService.submit(() -> {
				ar.resume(Response.ok("PONG").build());
			});
		}

		@GET
		@Path("data/{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public void getData(@Suspended AsyncResponse ar, @PathParam("id") int id) {
			executorService.submit(() -> {
				ar.resume(new TestData(id, "value" + id));
			});
		}

		@GET
		@Path("data2/{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public void getData2(@Suspended AsyncResponse ar, @PathParam("id") int id) {
			executorService.submit(() -> {
				if (id < 0) {
					ar.resume(Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
							.entity(new ApiError("ERR000", "Invalid data")).build());
				} else {
					ar.resume(Response.ok().type(MediaType.APPLICATION_JSON).entity(new TestData(id, "value" + id))
							.build());
				}
			});
		}

		@GET
		@Path("box/{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public void getBox(@Suspended AsyncResponse ar, @PathParam("id") int id) {
			executorService.submit(() -> {
				ar.resume(PropertyBox.builder(PROPERTIES).set(CODE, id).set(VALUE, "value" + id).build());
			});
		}

		@GET
		@Path("boxes")
		@Produces(MediaType.APPLICATION_JSON)
		public void getBoxes(@Suspended AsyncResponse ar) {
			executorService.submit(() -> {
				List<PropertyBox> boxes = new LinkedList<>();
				boxes.add(PropertyBox.builder(PROPERTIES).set(CODE, 1).set(VALUE, "value" + 1).build());
				boxes.add(PropertyBox.builder(PROPERTIES).set(CODE, 2).set(VALUE, "value" + 2).build());
				ar.resume(boxes);
			});
		}

		@GET
		@Path("stream")
		@Produces(MediaType.APPLICATION_OCTET_STREAM)
		public void getStream(@Suspended AsyncResponse ar) {
			executorService.submit(() -> {
				ar.resume(new StreamingOutput() {
					@Override
					public void write(OutputStream output) throws IOException, WebApplicationException {
						output.write(new byte[] { 1, 2, 3 });
					}
				});
			});
		}

		@POST
		@Path("postdata")
		@Produces(MediaType.APPLICATION_JSON)
		public void postData(@Suspended AsyncResponse ar, @QueryParam("id") int id) {
			executorService.submit(() -> {
				ar.resume(new TestData(id, "value" + id));
			});
		}

		@POST
		@Path("formParams")
		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		public void formParams(@Suspended AsyncResponse ar, @FormParam("one") String one,
				@FormParam("two") Integer two) {
			executorService.submit(() -> {
				assertNotNull(one);
				assertNotNull(two);
				if (!two.toString().equals(one)) {
					ar.resume(Response.status(Status.BAD_REQUEST).build());
				} else {
					ar.resume(Response.ok().build());
				}
			});
		}

		@PUT
		@Path("data/save")
		@Consumes(MediaType.APPLICATION_JSON)
		public void saveData(@Suspended AsyncResponse ar, TestData data) {
			executorService.submit(() -> {
				if (data == null || data.getCode() < 0) {
					ar.resume(Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
							.entity(new ApiError("ERR000", "Invalid data")).build());
				} else {
					ar.resume(Response.accepted().build());
				}
			});

		}

		@POST
		@Path("box/post")
		@Consumes(MediaType.APPLICATION_JSON)
		public void postBox(@Suspended AsyncResponse ar,
				@PropertySetRef(TestReactiveJaxrsClient.class) PropertyBox box) {
			assertNotNull(box);
			ar.resume(Response.accepted().build());
		}

		@PUT
		@Path("box/save")
		@Consumes(MediaType.APPLICATION_JSON)
		public void saveBox(@Suspended AsyncResponse ar,
				@PropertySetRef(TestReactiveJaxrsClient.class) PropertyBox box) {
			assertNotNull(box);
			ar.resume(Response.accepted().build());
		}

		@GET
		@Path("status/400")
		public void get400(@Suspended AsyncResponse ar) {
			ar.resume(Response.status(Status.BAD_REQUEST).build());
		}

		@GET
		@Path("status/401")
		public void get401(@Suspended AsyncResponse ar) {
			ar.resume(Response.status(Status.UNAUTHORIZED).build());
		}

		@GET
		@Path("status/403")
		public void get403(@Suspended AsyncResponse ar) {
			ar.resume(Response.status(Status.FORBIDDEN).build());
		}

		@GET
		@Path("status/405")
		public void get405(@Suspended AsyncResponse ar) {
			ar.resume(Response.status(Status.METHOD_NOT_ALLOWED).build());
		}

		@GET
		@Path("status/406")
		public void get406(@Suspended AsyncResponse ar) {
			ar.resume(Response.status(Status.NOT_ACCEPTABLE).build());
		}

		@GET
		@Path("status/415")
		public void get415(@Suspended AsyncResponse ar) {
			ar.resume(Response.status(Status.UNSUPPORTED_MEDIA_TYPE).build());
		}

		@GET
		@Path("status/500")
		public void get500(@Suspended AsyncResponse ar) {
			ar.resume(Response.status(Status.INTERNAL_SERVER_ERROR).build());
		}

		@GET
		@Path("status/503")
		public void get503(@Suspended AsyncResponse ar) {
			ar.resume(Response.status(Status.SERVICE_UNAVAILABLE).build());
		}

	}

	@Override
	protected Application configure() {
		return new ResourceConfig(AsyncTestResource.class).register(LoggingFeature.class);
	}

	@Override
	protected void configureClient(ClientConfig config) {
		config.register(LoggingFeature.class);
	}

	@Test
	public void testPing() {

		final ReactiveRestClient client = JaxrsReactiveRestClient.create(getClient()).defaultTarget(getBaseUri());

		final HttpStatus status = client.request().path("test").path("ping").get(String.class).doOnSuccess(response -> {
			assertEquals(HttpStatus.OK, response.getStatus());
			final String entity = response.getPayload().orElse(null);
			assertNotNull(entity);
			assertEquals("PONG", entity);
		}).map(r -> r.getStatus()).block();

		assertEquals(HttpStatus.OK, status);

	}

	@Test
	public void testClient() {

		final ReactiveRestClient client = JaxrsReactiveRestClient.create(getClient()).defaultTarget(getBaseUri());

		final HttpStatus status = client.request().path("test").path("data/{id}").resolve("id", 1)
				.accept(MediaType.APPLICATION_JSON).getForEntity(TestData.class).doOnSuccess(td -> {
					assertEquals(1, td.getCode());
				}).then(client.request().path("test").path("data/{id}").resolve("id", 1).get(TestData.class))
				.doOnSuccess(rsp -> {
					assertEquals(HttpStatus.OK, rsp.getStatus());
					assertEquals(Integer.valueOf(1), rsp.getPayload().map(p -> p.getCode()).orElse(null));
				}).then(client.request().path("test").path("box/{id}").resolve("id", 1).propertySet(PROPERTIES)
						.getForEntity(PropertyBox.class))
				.doOnSuccess(box -> {
					assertNotNull(box);
					assertEquals(Integer.valueOf(1), box.getValue(CODE));
					assertEquals("value1", box.getValue(VALUE));
				}).then(client.request().path("test").path("box/{id}").resolve("id", 1).propertySet(PROPERTIES)
						.get(PropertyBox.class))
				.doOnSuccess(rsp2 -> {
					assertEquals(HttpStatus.OK, rsp2.getStatus());
					PropertyBox box = rsp2.getPayload().orElse(null);
					assertNotNull(box);
					assertEquals(Integer.valueOf(1), box.getValue(CODE));
					assertEquals("value1", box.getValue(VALUE));
				}).then(client.request().path("test").path("boxes").propertySet(PROPERTIES).getAsList(PropertyBox.class)
						.collectList())
				.doOnSuccess(boxes -> {
					assertNotNull(boxes);
					assertEquals(2, boxes.size());

					PropertyBox box = boxes.get(0);
					assertNotNull(box);
					assertEquals(Integer.valueOf(1), box.getValue(CODE));
					assertEquals("value1", box.getValue(VALUE));

					box = boxes.get(1);
					assertNotNull(box);
					assertEquals(Integer.valueOf(2), box.getValue(CODE));
					assertEquals("value2", box.getValue(VALUE));
				})
				.then(client.request().path("test").path("boxes").propertySet(PROPERTIES).getAsList(PropertyBox.class)
						.collectList())
				.map(r -> r.stream().map(p -> p.getValue(CODE)).collect(Collectors.toList())).doOnSuccess(codes -> {
					assertNotNull(codes);
					assertEquals(2, codes.size());
				})
				.then(client.request().path("test").path("boxes").propertySet(PROPERTIES).getAsList(PropertyBox.class)
						.collectList())
				.map(r -> r.stream().map(p -> p.getValue(VALUE)).collect(Collectors.toList())).doOnSuccess(values -> {
					assertNotNull(values);
					assertEquals(2, values.size());
				}).then(client.request().path("test").path("postdata").queryParameter("id", 1).post(RequestEntity.EMPTY,
						TestData.class))
				.doOnSuccess(prsp -> {
					assertEquals(HttpStatus.OK, prsp.getStatus());
					assertTrue(prsp.getPayload().isPresent());
				})
				.then(client.request().path("test").path("box/post").post(
						RequestEntity.json(PropertyBox.builder(PROPERTIES).set(CODE, 100).set(VALUE, "post").build())))
				.map(r -> r.getStatus()).block();

		assertEquals(HttpStatus.ACCEPTED, status);

	}

	@Test
	public void testMethods() {
		final ReactiveRestClient client = ReactiveRestClient.forTarget(getBaseUri());

		final HttpStatus status = client.request().path("test").path("data/save")
				.put(RequestEntity.json(new TestData(7, "testPost"))).doOnSuccess(rsp -> {
					assertNotNull(rsp);
					assertEquals(HttpStatus.ACCEPTED, rsp.getStatus());
				})
				.then(client.request().path("test").path("formParams")
						.post(RequestEntity.form(RequestEntity.formBuilder().set("one", "1").set("two", "1").build())))
				.map(r -> r.getStatus()).block();

		assertEquals(HttpStatus.OK, status);

	}

	@Test
	public void testStream() {

		final ReactiveRestClient client = ReactiveRestClient.create(JaxrsReactiveRestClient.class.getName())
				.defaultTarget(getBaseUri());

		final byte[] bytes = client.request().path("test").path("stream").getForStream().map(s -> {
			try {
				return ConversionUtils.convertInputStreamToBytes(s);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).block();

		assertTrue(Arrays.equals(new byte[] { 1, 2, 3 }, bytes));
	}

	@Test
	public void testMultipleReads() {
		final ReactiveRestClient client = ReactiveRestClient.create(JaxrsReactiveRestClient.class.getName())
				.defaultTarget(getBaseUri());

		final HttpStatus status = client.request().path("test").path("data2/{id}").resolve("id", 1).get(TestData.class)
				.doOnSuccess(res -> {
					assertNotNull(res);

					TestData td = res.getPayload().orElse(null);
					assertNotNull(td);

					String asString = res.as(String.class).orElse(null);
					assertNotNull(asString);

					td = res.as(TestData.class).orElse(null);
					assertNotNull(td);
				}).map(r -> r.getStatus()).block();

		assertEquals(HttpStatus.OK, status);

	}

	@Test
	public void testErrors() {
		final ReactiveRestClient client = JaxrsReactiveRestClient.create(getClient()).defaultTarget(getBaseUri());

		client.request().path("test").path("data/save").put(RequestEntity.json(new TestData(-1, "testErr")))
				.doOnSuccess(rsp -> {
					assertNotNull(rsp);
					assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatus());

					ApiError error = rsp.as(ApiError.class).orElse(null);
					assertNotNull(error);
					assertEquals("ERR000", error.getCode());
				}).then(client.request().path("test").path("data2/{id}").resolve("id", -1).get(TestData.class))
				.doOnSuccess(r2 -> {
					assertNotNull(r2);
					assertEquals(HttpStatus.BAD_REQUEST, r2.getStatus());

					ApiError error = r2.as(ApiError.class).orElse(null);
					assertNotNull(error);
					assertEquals("ERR000", error.getCode());
				}).block();

		assertThrows(UnsuccessfulResponseException.class, () -> {
			client.request().path("test").path("data2/{id}").resolve("id", -1).getForEntity(TestData.class).block();
		});

		final TestData td = client.request().path("test").path("data2/{id}").resolve("id", -1)
				.getForEntity(TestData.class).onErrorReturn(t -> {
					assertTrue(t instanceof UnsuccessfulResponseException);

					final UnsuccessfulResponseException e = (UnsuccessfulResponseException) t;

					assertEquals(HttpStatus.BAD_REQUEST, e.getStatus().orElse(null));
					assertNotNull(e.getResponse());

					ApiError err = e.getResponse().as(ApiError.class).orElse(null);
					assertNotNull(err);
					assertEquals("ERR000", err.getCode());

					return true;
				}, new TestData(-1, "ERR000")).block();

		assertNotNull(td);
		assertEquals("ERR000", td.getValue());
	}

	@Test
	public void testStatus() {

		final ReactiveRestClient client = JaxrsReactiveRestClient.create(getClient()).defaultTarget(getBaseUri());

		final ResponseEntity<Void> response = client.request().path("test").path("status").path("400").get(Void.class)
				.doOnSuccess(rsp -> {
					assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatus());
				}).then(client.request().path("test").path("status").path("401").get(Void.class)).doOnSuccess(rsp -> {
					assertEquals(HttpStatus.UNAUTHORIZED, rsp.getStatus());
				}).then(client.request().path("test").path("status").path("403").get(Void.class)).doOnSuccess(rsp -> {
					assertEquals(HttpStatus.FORBIDDEN, rsp.getStatus());
				}).then(client.request().path("test").path("status").path("405").get(Void.class)).doOnSuccess(rsp -> {
					assertEquals(HttpStatus.METHOD_NOT_ALLOWED, rsp.getStatus());
				}).then(client.request().path("test").path("status").path("406").get(Void.class)).doOnSuccess(rsp -> {
					assertEquals(HttpStatus.NOT_ACCEPTABLE, rsp.getStatus());
				}).then(client.request().path("test").path("status").path("415").get(Void.class)).doOnSuccess(rsp -> {
					assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, rsp.getStatus());
				}).then(client.request().path("test").path("status").path("500").get(Void.class)).doOnSuccess(rsp -> {
					assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rsp.getStatus());
				}).then(client.request().path("test").path("status").path("503").get(Void.class)).doOnSuccess(rsp -> {
					assertEquals(HttpStatus.SERVICE_UNAVAILABLE, rsp.getStatus());
				}).block();

		assertNotNull(response);

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
