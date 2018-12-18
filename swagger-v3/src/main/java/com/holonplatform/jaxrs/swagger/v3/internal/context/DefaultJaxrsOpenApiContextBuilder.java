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

import javax.ws.rs.core.Application;

import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.v3.internal.scanner.JaxrsScannerProvider;

import io.swagger.v3.jaxrs2.integration.api.JaxrsOpenApiScanner;
import io.swagger.v3.oas.integration.api.OpenApiContext;

/**
 * Default {@link JaxrsOpenApiContextBuilder} implementation.
 *
 * @since 5.2.0
 */
public class DefaultJaxrsOpenApiContextBuilder extends
		AbstractOpenApiContextBuilder<JaxrsOpenApiContextBuilder, io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder<?>>
		implements JaxrsOpenApiContextBuilder {

	public DefaultJaxrsOpenApiContextBuilder() {
		super(new io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder<>());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v3.internal.builders.AbstractOpenApiContextBuilder#getBuilder()
	 */
	@Override
	protected JaxrsOpenApiContextBuilder getBuilder() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v3.builders.JaxrsOpenApiContextBuilder#application(javax.ws.rs.core.Application)
	 */
	@Override
	public JaxrsOpenApiContextBuilder application(Application application) {
		getContextBuilder().application(application);
		return getBuilder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v3.builders.JaxrsOpenApiContextBuilder#scannerType(com.holonplatform.jaxrs.
	 * swagger.v3.JaxrsScannerType)
	 */
	@Override
	public JaxrsOpenApiContextBuilder scannerType(JaxrsScannerType scannerType) {
		final JaxrsScannerType type = (scannerType != null) ? scannerType : JaxrsScannerType.DEFAULT;
		return JaxrsScannerProvider.getScannerClass(type).map(sc -> {
			try {
				return scanner(sc.newInstance());
			} catch (Exception e) {
				throw new ApiConfigurationException("Failed to instantiate the scanner class [" + sc.getName() + "]",
						e);
			}
		}).orElseGet(() -> getBuilder());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v3.internal.builders.AbstractOpenApiContextBuilder#configure(io.swagger.v3.oas.
	 * integration.api.OpenApiContext)
	 */
	@Override
	protected OpenApiContext configure(OpenApiContext context) {
		if (scanner != null && scanner instanceof JaxrsOpenApiScanner) {
			((JaxrsOpenApiScanner) scanner).setApplication(getContextBuilder().getApplication());
		}
		return context;
	}

}
