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
package com.holonplatform.jaxrs.swagger;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;

/**
 * API definitions reader.
 * 
 * @param <M> API model type
 *
 * @since 5.2.0
 */
public interface ApiReader<M> {

	/**
	 * Read the given API resource class set and build the API definition model.
	 * @param classes The API resource classes from which to create the API definition model
	 * @return The API definition model
	 * @throws ApiConfigurationException If an error occurred
	 */
	M read(Set<Class<?>> classes) throws ApiConfigurationException;

	/**
	 * Read the given API resource class set and build the API definition model.
	 * @param classes The API resource classes from which to create the API definition model
	 * @return The API definition model
	 * @throws ApiConfigurationException If an error occurred
	 */
	default M read(Class<?>... classes) throws ApiConfigurationException {
		return read(Arrays.asList(classes).stream().filter(c -> c != null).collect(Collectors.toSet()));
	}

	/**
	 * Convert given API definition in JSON format.
	 * @param pretty Whether to pretty format the output
	 * @return The API definition as JSON
	 */
	String asJson(M api, boolean pretty);

	/**
	 * Convert given API definition in JSON format.
	 * @return The API definition as JSON
	 */
	default String asJson(M api) {
		return asJson(api, false);
	}

	/**
	 * Convert given API definition in YAML format.
	 * @param pretty Whether to pretty format the output
	 * @return The API definition as YAML
	 */
	String asYaml(M api, boolean pretty);

	/**
	 * Convert given API definition in YAML format.
	 * @return The API definition as YAML
	 */
	default String asYaml(M api) {
		return asYaml(api, false);
	}

}
