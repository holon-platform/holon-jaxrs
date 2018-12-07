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
package com.holonplatform.jaxrs.swagger.v3;

import com.holonplatform.jaxrs.swagger.v3.internal.DefaultOpenAPIContextListener;

import io.swagger.v3.jaxrs2.ReaderListener;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * A {@link ReaderListener} to provide the current {@link OpenAPI} resolution context.
 *
 * @since 5.2.0
 */
public interface OpenAPIContextListener extends ReaderListener {

	/**
	 * Get the default {@link OpenAPIContextListener}.
	 * @return the default {@link OpenAPIContextListener}
	 */
	static OpenAPIContextListener getDefault() {
		return new DefaultOpenAPIContextListener();
	}

}
