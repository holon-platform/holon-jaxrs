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
package com.holonplatform.jaxrs.swagger.v2.internal;

import com.holonplatform.jaxrs.swagger.v2.SwaggerReader;

import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderConfig;
import io.swagger.models.Swagger;

/**
 * Default {@link SwaggerReader} implementation.
 *
 * @since 5.2.0
 */
public class DefaultSwaggerReader extends Reader implements SwaggerReader {

	/**
	 * Constructor.
	 * @param swagger Initial model
	 */
	public DefaultSwaggerReader(Swagger swagger) {
		super(swagger);
	}

	/**
	 * Constructor.
	 * @param swagger Initial model
	 * @param config Reader configuration
	 */
	public DefaultSwaggerReader(Swagger swagger, ReaderConfig config) {
		super(swagger, config);
	}

}
