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
package com.holonplatform.jaxrs.swagger.v3.internal.context;

import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.jaxrs2.ReaderListener;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * A {@link ReaderListener} to provide the current {@link OpenAPI} resolution context.
 *
 * @since 5.2.0
 */
public class OpenApiContextListener implements ReaderListener {

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.jaxrs2.ReaderListener#beforeScan(io.swagger.v3.jaxrs2.Reader,
	 * io.swagger.v3.oas.models.OpenAPI)
	 */
	@Override
	public void beforeScan(Reader reader, OpenAPI openAPI) {
		OpenApiResolutionContext.setOpenAPI(openAPI);
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.jaxrs2.ReaderListener#afterScan(io.swagger.v3.jaxrs2.Reader, io.swagger.v3.oas.models.OpenAPI)
	 */
	@Override
	public void afterScan(Reader reader, OpenAPI openAPI) {
		OpenApiResolutionContext.includeSchemas();
		OpenApiResolutionContext.removeOpenAPI();
	}

}
