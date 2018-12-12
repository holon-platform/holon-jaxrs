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

import java.util.Set;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.v3.OpenAPIContextListener;

import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.api.ObjectMapperProcessor;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.integration.api.OpenApiScanner;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * {@link OpenApiContext} adapter to provide {@link OpenAPIContextListener} registration.
 *
 * @since 5.2.0
 */
public class OpenAPIContextAdapter implements OpenApiContext {

	private final OpenApiContext context;

	/**
	 * Constructor
	 * @param context Concrete context
	 */
	public OpenAPIContextAdapter(OpenApiContext context) {
		super();
		ObjectUtils.argumentNotNull(context, "OpenApiContext must be not null");
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#getId()
	 */
	@Override
	public String getId() {
		return this.context.getId();
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#init()
	 */
	@Override
	public OpenApiContext init() throws OpenApiConfigurationException {
		final OpenApiContext ctx = this.context.init();
		if (ctx instanceof GenericOpenApiContext) {
			OpenApiScanner contextScanner = ((GenericOpenApiContext<?>) ctx).getOpenApiScanner();
			if (contextScanner != null) {
				this.context.setOpenApiScanner(new OpenAPIScannerAdapter(contextScanner));
			}
		}
		return ctx;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#read()
	 */
	@Override
	public OpenAPI read() {
		return this.context.read();
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#getOpenApiConfiguration()
	 */
	@Override
	public OpenAPIConfiguration getOpenApiConfiguration() {
		return this.context.getOpenApiConfiguration();
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#getConfigLocation()
	 */
	@Override
	public String getConfigLocation() {
		return this.context.getConfigLocation();
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#getParent()
	 */
	@Override
	public OpenApiContext getParent() {
		return this.context.getParent();
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#setOpenApiScanner(io.swagger.v3.oas.integration.api.
	 * OpenApiScanner)
	 */
	@Override
	public void setOpenApiScanner(OpenApiScanner openApiScanner) {
		this.context.setOpenApiScanner(openApiScanner);
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#setOpenApiReader(io.swagger.v3.oas.integration.api.
	 * OpenApiReader)
	 */
	@Override
	public void setOpenApiReader(OpenApiReader openApiReader) {
		this.context.setOpenApiReader(openApiReader);
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#setObjectMapperProcessor(io.swagger.v3.oas.integration.api.
	 * ObjectMapperProcessor)
	 */
	@Override
	public void setObjectMapperProcessor(ObjectMapperProcessor objectMapperProcessor) {
		this.context.setObjectMapperProcessor(objectMapperProcessor);
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#setModelConverters(java.util.Set)
	 */
	@Override
	public void setModelConverters(Set<ModelConverter> modelConverters) {
		this.context.setModelConverters(modelConverters);
	}

}
