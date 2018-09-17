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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
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
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.holonplatform.async.http.AsyncRestClient;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.jaxrs.client.JaxrsAsyncRestClient;
import com.holonplatform.jaxrs.client.test.TestJaxrsClient.TestData;

public class TestAsyncJaxrsClient extends JerseyTest {

	private static ExecutorService executorService;

	public TestAsyncJaxrsClient() {
		super();
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	@BeforeClass
	public static void initExecutor() {
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
			Assert.assertEquals(HttpStatus.OK, response.getStatus());
			final String entity = response.getPayload().orElse(null);
			Assert.assertNotNull(entity);
			Assert.assertEquals("PONG", entity);
		}).toCompletableFuture().get();

	}

}
