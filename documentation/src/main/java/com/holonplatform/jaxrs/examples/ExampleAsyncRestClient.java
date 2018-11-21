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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.core.MediaType;

import com.holonplatform.async.http.AsyncRestClient;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.ResponseEntity;
import com.holonplatform.jaxrs.client.JaxrsAsyncRestClient;

@SuppressWarnings("unused")
public class ExampleAsyncRestClient {

	public static class TestData {

	}

	public void restClient() throws URISyntaxException {
		// tag::restclient[]

		final PathProperty<Integer> ID = PathProperty.create("id", Integer.class);
		final PathProperty<String> NAME = PathProperty.create("name", String.class);

		final PropertySet<?> PROPERTY_SET = PropertySet.of(ID, NAME);

		AsyncRestClient client = JaxrsAsyncRestClient.create() // <1>
				.defaultTarget(new URI("https://host/api")); // <2>

		client = AsyncRestClient.create(JaxrsAsyncRestClient.class.getName()); // <3>

		client = AsyncRestClient.create(); // <4>

		client = AsyncRestClient.forTarget("https://host/api"); // <5>

		CompletionStage<Optional<TestData>> testData = client.request().path("data/{id}").resolve("id", 1) // <6>
				.accept(MediaType.APPLICATION_JSON).getForEntity(TestData.class);

		CompletionStage<Optional<PropertyBox>> box = client.request().path("getbox") // <7>
				.propertySet(PROPERTY_SET).getForEntity(PropertyBox.class);

		CompletionStage<ResponseEntity<PropertyBox>> response = client.request().path("getbox") // <8>
				.propertySet(PROPERTY_SET).get(PropertyBox.class);

		CompletionStage<List<PropertyBox>> boxes = client.request().path("getboxes") // <9>
				.propertySet(PROPERTY_SET).getAsList(PropertyBox.class);

		PropertyBox postBox = PropertyBox.builder(PROPERTY_SET).set(ID, 1).set(NAME, "Test").build();

		CompletionStage<ResponseEntity<Void>> postResponse = client.request().path("postbox") // <10>
				.post(RequestEntity.json(postBox));
		// end::restclient[]
	}

}
