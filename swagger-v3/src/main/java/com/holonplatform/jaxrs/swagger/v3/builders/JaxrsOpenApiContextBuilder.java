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
package com.holonplatform.jaxrs.swagger.v3.builders;

import javax.ws.rs.core.Application;

import com.holonplatform.jaxrs.swagger.v3.JaxrsScannerType;

/**
 * JAX-RS {@link OpenApiContextBuilder}.
 *
 * @since 5.2.0
 */
public interface JaxrsOpenApiContextBuilder extends OpenApiContextBuilder<JaxrsOpenApiContextBuilder> {

	/**
	 * Set the JAX-RS {@link Application} to use to detect the API resources to use to build the API definitions.
	 * @param application The JAX-RS {@link Application} to set
	 * @return this
	 */
	JaxrsOpenApiContextBuilder application(Application application);

	/**
	 * Set the scanner type to use.
	 * @param scannerType The scanner type to set
	 * @return this
	 */
	JaxrsOpenApiContextBuilder scannerType(JaxrsScannerType scannerType);

}
