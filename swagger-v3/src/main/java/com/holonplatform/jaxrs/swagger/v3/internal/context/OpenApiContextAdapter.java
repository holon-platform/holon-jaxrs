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

import java.util.Optional;
import java.util.Set;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.v3.SwaggerV3;
import com.holonplatform.jaxrs.swagger.v3.internal.scanner.OpenApiScannerAdapter;

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
 * {@link OpenApiContext} adapter to ensure the {@link SwaggerV3#CONTEXT_READER_LISTENER} class inclusion in the classes
 * to read.
 *
 * @since 5.2.0
 */
public class OpenApiContextAdapter implements OpenApiContext {

	private static final Logger LOGGER = SwaggerLogger.create();

	private final OpenApiContext context;

	private boolean adapted = false;

	/**
	 * Constructor.
	 * @param context The concrete context (not null)
	 */
	public OpenApiContextAdapter(OpenApiContext context) {
		super();
		ObjectUtils.argumentNotNull(context, "OpenApiContext must be not null");
		this.context = context;
		// check inited
		getContextReader(context).ifPresent(r -> {
			context.setOpenApiReader(SwaggerV3.adapt(r));
		});
		if (context.getId() != null) {
			getContextScanner(context).ifPresent(s -> {
				context.setOpenApiScanner(OpenApiScannerAdapter.adapt(s, context.getId()));
			});
		}
	}

	/**
	 * Get the concrete context.
	 * @return the concrete context
	 */
	protected OpenApiContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#getId()
	 */
	@Override
	public String getId() {
		return getContext().getId();
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#init()
	 */
	@Override
	public OpenApiContext init() throws OpenApiConfigurationException {
		OpenApiContext ctx = getContext().init();
		if (!adapted) {
			// reader
			final OpenApiReader contextReader = getContextReader(getContext()).orElse(null);
			if (contextReader != null) {
				getContext().setOpenApiReader(SwaggerV3.adapt(contextReader));
			} else {
				LOGGER.warn("Failed to obtain the OpenApiReader bound to context [" + getContext()
						+ "]: the reader won't be adapted");
			}
			// scanner
			if (getId() != null) {
				final OpenApiScanner contextScanner = getContextScanner(getContext()).orElse(null);
				if (contextScanner != null) {
					getContext().setOpenApiScanner(OpenApiScannerAdapter.adapt(contextScanner, getId()));
				} else {
					LOGGER.warn("Failed to obtain the OpenApiScanner bound to context [" + getContext()
							+ "]: the scanner won't be adapted");
				}
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
		return getContext().read();
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#getOpenApiConfiguration()
	 */
	@Override
	public OpenAPIConfiguration getOpenApiConfiguration() {
		return getContext().getOpenApiConfiguration();
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#getConfigLocation()
	 */
	@Override
	public String getConfigLocation() {
		return getContext().getConfigLocation();
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#getParent()
	 */
	@Override
	public OpenApiContext getParent() {
		return getContext().getParent();
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#setOpenApiScanner(io.swagger.v3.oas.integration.api.
	 * OpenApiScanner)
	 */
	@Override
	public void setOpenApiScanner(OpenApiScanner openApiScanner) {
		getContext().setOpenApiScanner(openApiScanner);
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#setOpenApiReader(io.swagger.v3.oas.integration.api.
	 * OpenApiReader)
	 */
	@Override
	public void setOpenApiReader(OpenApiReader openApiReader) {
		if (openApiReader != null) {
			getContext().setOpenApiReader(SwaggerV3.adapt(openApiReader));
			adapted = true;
		} else {
			getContext().setOpenApiReader(openApiReader);
			adapted = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#setObjectMapperProcessor(io.swagger.v3.oas.integration.api.
	 * ObjectMapperProcessor)
	 */
	@Override
	public void setObjectMapperProcessor(ObjectMapperProcessor objectMapperProcessor) {
		getContext().setObjectMapperProcessor(objectMapperProcessor);
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiContext#setModelConverters(java.util.Set)
	 */
	@Override
	public void setModelConverters(Set<ModelConverter> modelConverters) {
		getContext().setModelConverters(modelConverters);
	}

	/**
	 * Try to obtain the {@link OpenApiReader} bound to given context.
	 * @param context The context
	 * @return Optional context reader
	 */
	private static Optional<OpenApiReader> getContextReader(OpenApiContext context) {
		if (context instanceof GenericOpenApiContext) {
			return Optional.ofNullable(((GenericOpenApiContext<?>) context).getOpenApiReader());
		}
		return Optional.empty();
	}

	/**
	 * Try to obtain the {@link OpenApiScanner} bound to given context.
	 * @param context The context
	 * @return Optional context scanner
	 */
	private static Optional<OpenApiScanner> getContextScanner(OpenApiContext context) {
		if (context instanceof GenericOpenApiContext) {
			return Optional.ofNullable(((GenericOpenApiContext<?>) context).getOpenApiScanner());
		}
		return Optional.empty();
	}

}
