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
package com.holonplatform.jaxrs.client.test;

import static com.holonplatform.core.property.PathProperty.create;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.holonplatform.async.http.AsyncRestClient;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.http.exceptions.UnsuccessfulResponseException;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.jaxrs.client.JaxrsAsyncRestClient;
import com.holonplatform.jaxrs.client.test.TestJaxrsClient.ApiError;
import com.holonplatform.jaxrs.client.test.TestJaxrsClient.TestData;
import com.holonplatform.test.JerseyTest5;

public class TestAsyncJaxrsClient extends JerseyTest5 {

	private static ExecutorService executorService;

	public static final PathProperty<Integer> CODE = create("code", int.class);
	public static final PathProperty<String> VALUE = create("value", String.class);

	public static final PropertySet<?> PROPERTIES = PropertySet.of(CODE, VALUE);

	public TestAsyncJaxrsClient() {
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
		public void postBox(@Suspended AsyncResponse ar, @PropertySetRef(TestAsyncJaxrsClient.class) PropertyBox box) {
			assertNotNull(box);
			ar.resume(Response.accepted().build());
		}

		@PUT
		@Path("box/save")
		@Consumes(MediaType.APPLICATION_JSON)
		public void saveBox(@Suspended AsyncResponse ar, @PropertySetRef(TestAsyncJaxrsClient.class) PropertyBox box) {
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
	public void testPing() throws InterruptedException, ExecutionException {

		final AsyncRestClient client = JaxrsAsyncRestClient.create(getClient()).defaultTarget(getBaseUri());

		client.request().path("test").path("ping").get(String.class).thenAccept(response -> {
			assertEquals(HttpStatus.OK, response.getStatus());
			final String entity = response.getPayload().orElse(null);
			assertNotNull(entity);
			assertEquals("PONG", entity);
		}).toCompletableFuture().get();

	}

	@Test
	public void testClient() {

		final AsyncRestClient client = JaxrsAsyncRestClient.create(getClient()).defaultTarget(getBaseUri());

		final HttpStatus status = client.request().path("test").path("data/{id}").resolve("id", 1)
				.accept(MediaType.APPLICATION_JSON).getForEntity(TestData.class).thenAccept(td -> {
					assertTrue(td.isPresent());
					assertEquals(1, td.get().getCode());
				})
				.thenCompose(x -> client.request().path("test").path("data/{id}").resolve("id", 1).get(TestData.class))
				.thenAccept(rsp -> {
					assertEquals(HttpStatus.OK, rsp.getStatus());
					assertEquals(Integer.valueOf(1), rsp.getPayload().map(p -> p.getCode()).orElse(null));
				})
				.thenCompose(x -> client.request().path("test").path("box/{id}").resolve("id", 1)
						.propertySet(PROPERTIES).getForEntity(PropertyBox.class))
				.thenApply(r -> r.orElse(null)).thenAccept(box -> {
					assertNotNull(box);
					assertEquals(Integer.valueOf(1), box.getValue(CODE));
					assertEquals("value1", box.getValue(VALUE));
				}).thenCompose(x -> client.request().path("test").path("box/{id}").resolve("id", 1)
						.propertySet(PROPERTIES).get(PropertyBox.class))
				.thenAccept(rsp2 -> {
					assertEquals(HttpStatus.OK, rsp2.getStatus());
					PropertyBox box = rsp2.getPayload().orElse(null);
					assertNotNull(box);
					assertEquals(Integer.valueOf(1), box.getValue(CODE));
					assertEquals("value1", box.getValue(VALUE));
				}).thenCompose(x -> client.request().path("test").path("boxes").propertySet(PROPERTIES)
						.getAsList(PropertyBox.class))
				.thenAccept(boxes -> {
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
				.thenCompose(x -> client.request().path("test").path("boxes").propertySet(PROPERTIES)
						.getAsList(PropertyBox.class))
				.thenApply(r -> r.stream().map(p -> p.getValue(CODE)).collect(Collectors.toList()))
				.thenAccept(codes -> {
					assertNotNull(codes);
					assertEquals(2, codes.size());
				})
				.thenCompose(x -> client.request().path("test").path("boxes").propertySet(PROPERTIES)
						.getAsList(PropertyBox.class))
				.thenApply(r -> r.stream().map(p -> p.getValue(VALUE)).collect(Collectors.toList()))
				.thenAccept(values -> {
					assertNotNull(values);
					assertEquals(2, values.size());
				}).thenCompose(x -> client.request().path("test").path("postdata").queryParameter("id", 1)
						.post(RequestEntity.EMPTY, TestData.class))
				.thenAccept(prsp -> {
					assertEquals(HttpStatus.OK, prsp.getStatus());
					assertTrue(prsp.getPayload().isPresent());
				})
				.thenCompose(x -> client.request().path("test").path("box/post").post(
						RequestEntity.json(PropertyBox.builder(PROPERTIES).set(CODE, 100).set(VALUE, "post").build())))
				.thenApply(r -> r.getStatus()).toCompletableFuture().join();

		assertEquals(HttpStatus.ACCEPTED, status);

	}

	@Test
	public void testMethods() {
		final AsyncRestClient client = AsyncRestClient.forTarget(getBaseUri());

		final HttpStatus status = client.request().path("test").path("data/save")
				.put(RequestEntity.json(new TestData(7, "testPost"))).thenAccept(rsp -> {
					assertNotNull(rsp);
					assertEquals(HttpStatus.ACCEPTED, rsp.getStatus());
				})
				.thenCompose(x -> client.request().path("test").path("formParams")
						.post(RequestEntity.form(RequestEntity.formBuilder().set("one", "1").set("two", "1").build())))
				.thenApply(r -> r.getStatus()).toCompletableFuture().join();

		assertEquals(HttpStatus.OK, status);

	}

	@SuppressWarnings("resource")
	@Test
	public void testStream() throws IOException {

		final AsyncRestClient client = AsyncRestClient.create(JaxrsAsyncRestClient.class.getName())
				.defaultTarget(getBaseUri());

		InputStream s = client.request().path("test").path("stream").getForStream().toCompletableFuture().join();
		assertNotNull(s);

		byte[] bytes = ConversionUtils.convertInputStreamToBytes(s);
		assertNotNull(s);
		assertTrue(Arrays.equals(new byte[] { 1, 2, 3 }, bytes));
	}

	@Test
	public void testMultipleReads() {
		final AsyncRestClient client = AsyncRestClient.create(JaxrsAsyncRestClient.class.getName())
				.defaultTarget(getBaseUri());

		client.request().path("test").path("data2/{id}").resolve("id", 1).get(TestData.class).thenAccept(res -> {
			assertNotNull(res);

			TestData td = res.getPayload().orElse(null);
			assertNotNull(td);

			String asString = res.as(String.class).orElse(null);
			assertNotNull(asString);

			td = res.as(TestData.class).orElse(null);
			assertNotNull(td);
		}).toCompletableFuture().join();

	}

	@Test
	public void testErrors() {
		final AsyncRestClient client = JaxrsAsyncRestClient.create(getClient()).defaultTarget(getBaseUri());

		client.request().path("test").path("data/save").put(RequestEntity.json(new TestData(-1, "testErr")))
				.thenAccept(rsp -> {
					assertNotNull(rsp);
					assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatus());

					ApiError error = rsp.as(ApiError.class).orElse(null);
					assertNotNull(error);
					assertEquals("ERR000", error.getCode());
				})
				.thenCompose(
						x -> client.request().path("test").path("data2/{id}").resolve("id", -1).get(TestData.class))
				.thenAccept(r2 -> {
					assertNotNull(r2);
					assertEquals(HttpStatus.BAD_REQUEST, r2.getStatus());

					ApiError error = r2.as(ApiError.class).orElse(null);
					assertNotNull(error);
					assertEquals("ERR000", error.getCode());
				}).toCompletableFuture().join();

		assertThrows(CompletionException.class, () -> {
			client.request().path("test").path("data2/{id}").resolve("id", -1).getForEntity(TestData.class)
					.toCompletableFuture().join();
		});

		final TestData td = client.request().path("test").path("data2/{id}").resolve("id", -1)
				.getForEntity(TestData.class).exceptionally(t -> {
					assertTrue(t instanceof CompletionException);

					final Throwable we = t.getCause();
					assertNotNull(we);

					assertTrue(we instanceof UnsuccessfulResponseException);

					final UnsuccessfulResponseException e = (UnsuccessfulResponseException) we;

					assertEquals(HttpStatus.BAD_REQUEST, e.getStatus().orElse(null));
					assertNotNull(e.getResponse());

					ApiError err = e.getResponse().as(ApiError.class).orElse(null);
					assertNotNull(err);
					assertEquals("ERR000", err.getCode());
					return Optional.of(new TestData(-1, "ERR000"));
				}).thenApply(r -> r.orElse(null)).toCompletableFuture().join();

		assertNotNull(td);
		assertEquals("ERR000", td.getValue());
	}

	@Test
	public void testStatus() {

		final AsyncRestClient client = JaxrsAsyncRestClient.create(getClient()).defaultTarget(getBaseUri());

		client.request().path("test").path("status").path("400").get(Void.class).thenAccept(rsp -> {
			assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatus());
		}).thenCompose(x -> client.request().path("test").path("status").path("401").get(Void.class))
				.thenAccept(rsp -> {
					assertEquals(HttpStatus.UNAUTHORIZED, rsp.getStatus());
				}).thenCompose(x -> client.request().path("test").path("status").path("403").get(Void.class))
				.thenAccept(rsp -> {
					assertEquals(HttpStatus.FORBIDDEN, rsp.getStatus());
				}).thenCompose(x -> client.request().path("test").path("status").path("405").get(Void.class))
				.thenAccept(rsp -> {
					assertEquals(HttpStatus.METHOD_NOT_ALLOWED, rsp.getStatus());
				}).thenCompose(x -> client.request().path("test").path("status").path("406").get(Void.class))
				.thenAccept(rsp -> {
					assertEquals(HttpStatus.NOT_ACCEPTABLE, rsp.getStatus());
				}).thenCompose(x -> client.request().path("test").path("status").path("415").get(Void.class))
				.thenAccept(rsp -> {
					assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, rsp.getStatus());
				}).thenCompose(x -> client.request().path("test").path("status").path("500").get(Void.class))
				.thenAccept(rsp -> {
					assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rsp.getStatus());
				}).thenCompose(x -> client.request().path("test").path("status").path("503").get(Void.class))
				.thenAccept(rsp -> {
					assertEquals(HttpStatus.SERVICE_UNAVAILABLE, rsp.getStatus());
				}).toCompletableFuture().join();

	}

}
