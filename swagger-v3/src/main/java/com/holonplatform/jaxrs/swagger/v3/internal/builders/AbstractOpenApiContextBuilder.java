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
package com.holonplatform.jaxrs.swagger.v3.internal.builders;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.v3.OpenApi;
import com.holonplatform.jaxrs.swagger.v3.builders.OpenApiContextBuilder;

import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.integration.api.OpenApiScanner;

/**
 * Base {@link OpenApiContext} builder implementation.
 * 
 * @param <B> Concrete builder type
 * @param <X> Concrete context builder type
 *
 * @since 5.2.0
 */
public abstract class AbstractOpenApiContextBuilder<B extends OpenApiContextBuilder<B>, X extends GenericOpenApiContextBuilder<?>>
		implements OpenApiContextBuilder<B> {

	protected final X contextBuilder;

	protected OpenApiScanner scanner;
	protected OpenApiReader reader;

	public AbstractOpenApiContextBuilder(X contextBuilder) {
		super();
		ObjectUtils.argumentNotNull(contextBuilder, "Context builder must be not null");
		this.contextBuilder = contextBuilder;
	}

	protected abstract B getBuilder();

	/**
	 * Get the concrete context builder.
	 * @return the concrete context builder
	 */
	protected X getContextBuilder() {
		return contextBuilder;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v3.builders.OpenApiContextBuilder#contextId(java.lang.String)
	 */
	@Override
	public B contextId(String contextId) {
		contextBuilder.ctxId(contextId);
		return getBuilder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v3.builders.OpenApiContextBuilder#configuration(io.swagger.v3.oas.integration.api
	 * .OpenAPIConfiguration)
	 */
	@Override
	public B configuration(OpenAPIConfiguration configuration) {
		contextBuilder.openApiConfiguration(configuration);
		return getBuilder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v3.builders.OpenApiContextBuilder#configLocation(java.lang.String)
	 */
	@Override
	public B configLocation(String configLocation) {
		contextBuilder.configLocation(configLocation);
		return getBuilder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v3.builders.OpenApiContextBuilder#resourcePackages(java.util.Set)
	 */
	@Override
	public B resourcePackages(Set<String> resourcePackages) {
		contextBuilder.resourcePackages(resourcePackages);
		return getBuilder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v3.builders.OpenApiContextBuilder#resourceClasses(java.util.Set)
	 */
	@Override
	public B resourceClasses(Set<Class<?>> resourceClasses) {
		Set<Class<?>> cls = (resourceClasses != null) ? resourceClasses : Collections.emptySet();
		contextBuilder.resourceClasses(cls.stream().map(c -> c.getName()).collect(Collectors.toSet()));
		return getBuilder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v3.builders.OpenApiContextBuilder#scanner(io.swagger.v3.oas.integration.api.
	 * OpenApiScanner)
	 */
	@Override
	public B scanner(OpenApiScanner scanner) {
		this.scanner = scanner;
		return getBuilder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v3.builders.OpenApiContextBuilder#reader(io.swagger.v3.oas.integration.api.
	 * OpenApiReader)
	 */
	@Override
	public B reader(OpenApiReader reader) {
		this.reader = reader;
		return getBuilder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v3.builders.OpenApiContextBuilder#build(boolean)
	 */
	@Override
	public OpenApiContext build(boolean initialize) throws ApiConfigurationException {
		try {
			OpenApiContext context = configure(contextBuilder.buildContext(false));
			if (reader != null) {
				context.setOpenApiReader(reader);
			}
			if (scanner != null) {
				scanner.setConfiguration(contextBuilder.getOpenApiConfiguration());
				context.setOpenApiScanner(scanner);
			}
			if (initialize) {
				context.init();
			}
			return OpenApi.adapt(context);
		} catch (OpenApiConfigurationException e) {
			throw new ApiConfigurationException("Failed to initialize OpenAPI context", e);
		}
	}

	protected OpenApiContext configure(OpenApiContext context) {
		return context;
	}

}
