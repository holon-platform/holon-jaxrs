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

import javax.ws.rs.core.MediaType;

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.jaxrs.client.reactor.JaxrsReactiveRestClient;
import com.holonplatform.reactor.http.ReactiveResponseEntity;
import com.holonplatform.reactor.http.ReactiveRestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public class ExampleReactiveRestClient {

	public static class TestData {

	}

	public void restClient() throws URISyntaxException {
		// tag::restclient[]

		final PathProperty<Integer> ID = PathProperty.create("id", Integer.class);
		final PathProperty<String> NAME = PathProperty.create("name", String.class);

		final PropertySet<?> PROPERTY_SET = PropertySet.of(ID, NAME);

		ReactiveRestClient client = JaxrsReactiveRestClient.create() // <1>
				.defaultTarget(new URI("https://host/api")); // <2>

		client = ReactiveRestClient.create(JaxrsReactiveRestClient.class.getName()); // <3>

		client = ReactiveRestClient.create(); // <4>

		client = ReactiveRestClient.forTarget("https://host/api"); // <5>

		Mono<TestData> testData = client.request().path("data/{id}").resolve("id", 1) // <6>
				.accept(MediaType.APPLICATION_JSON).getForEntity(TestData.class);

		Mono<PropertyBox> box = client.request().path("getbox") // <7>
				.propertySet(PROPERTY_SET).getForEntity(PropertyBox.class);

		Mono<ReactiveResponseEntity<PropertyBox>> response = client.request().path("getbox") // <8>
				.propertySet(PROPERTY_SET).get(PropertyBox.class);

		Flux<PropertyBox> boxes = client.request().path("getboxes") // <9>
				.propertySet(PROPERTY_SET).getAsList(PropertyBox.class);

		PropertyBox postBox = PropertyBox.builder(PROPERTY_SET).set(ID, 1).set(NAME, "Test").build();

		Mono<ReactiveResponseEntity<Void>> postResponse = client.request().path("postbox") // <10>
				.post(RequestEntity.json(postBox));
		// end::restclient[]
	}

}
