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
package com.holonplatform.jaxrs.swagger;

import com.holonplatform.jaxrs.swagger.internal.SwaggerContext;

import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Swagger;

/**
 * Swagger {@link ReaderListener} to handle current {@link Swagger} instance using a {@link ThreadLocal}.
 *
 * @since 5.0.0
 */
public class SwaggerContextListener implements ReaderListener {

	/*
	 * (non-Javadoc)
	 * @see io.swagger.jaxrs.config.ReaderListener#beforeScan(io.swagger.jaxrs.Reader, io.swagger.models.Swagger)
	 */
	@Override
	public void beforeScan(Reader reader, Swagger swagger) {
		SwaggerContext.setSwagger(swagger);
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.jaxrs.config.ReaderListener#afterScan(io.swagger.jaxrs.Reader, io.swagger.models.Swagger)
	 */
	@Override
	public void afterScan(Reader reader, Swagger swagger) {
		SwaggerContext.setSwagger(null);
	}

}
