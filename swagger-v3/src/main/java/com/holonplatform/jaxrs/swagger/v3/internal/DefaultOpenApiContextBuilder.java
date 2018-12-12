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
package com.holonplatform.jaxrs.swagger.v3.internal;

import com.holonplatform.jaxrs.swagger.v3.OpenAPIContextListener;

import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.integration.api.OpenApiContextBuilder;
import io.swagger.v3.oas.integration.api.OpenApiScanner;

/**
 * An {@link OpenApiContextBuilder} which ensures {@link OpenAPIContextListener} registration.
 * 
 * @since 5.2.0
 */
public class DefaultOpenApiContextBuilder extends JaxrsOpenApiContextBuilder<DefaultOpenApiContextBuilder> {

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.GenericOpenApiContextBuilder#buildContext(boolean)
	 */
	@Override
	public OpenApiContext buildContext(boolean init) throws OpenApiConfigurationException {
		OpenApiContext context = super.buildContext(init);
		if (context instanceof GenericOpenApiContext) {
			OpenApiScanner contextScanner = ((GenericOpenApiContext<?>) context).getOpenApiScanner();
			if (contextScanner != null) {
				context.setOpenApiScanner(new OpenAPIScannerAdapter(contextScanner));
			}
		}
		return context;
	}

}
