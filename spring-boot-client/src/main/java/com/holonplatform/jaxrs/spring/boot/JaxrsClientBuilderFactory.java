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
package com.holonplatform.jaxrs.spring.boot;

import jakarta.ws.rs.client.ClientBuilder;

/**
 * Factory interface to provide the JAX-RS {@link ClientBuilder} instance.
 * 
 * @since 5.0.0
 */
public interface JaxrsClientBuilderFactory {

	/**
	 * Create a JAX-RS {@link ClientBuilder} instance.
	 * @return the JAX-RS {@link ClientBuilder} instance
	 */
	ClientBuilder createBuilder();

}
