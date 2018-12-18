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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.v3.SwaggerV3;

import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * {@link OpenApiReader} adapter to ensure the {@link SwaggerV3#CONTEXT_READER_LISTENER} class inclusion in the classes to
 * read.
 *
 * @since 5.2.0
 */
public class OpenApiReaderAdapter implements OpenApiReader {

	private final OpenApiReader reader;

	/**
	 * Constructor.
	 * @param reader The concrete reader (not null)
	 */
	public OpenApiReaderAdapter(OpenApiReader reader) {
		super();
		ObjectUtils.argumentNotNull(reader, "OpenApiReader must be not null");
		this.reader = reader;
	}

	/**
	 * Get the concrete reader.
	 * @return the concrete reader
	 */
	protected OpenApiReader getReader() {
		return reader;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiReader#setConfiguration(io.swagger.v3.oas.integration.api.
	 * OpenAPIConfiguration)
	 */
	@Override
	public void setConfiguration(OpenAPIConfiguration openApiConfiguration) {
		getReader().setConfiguration(openApiConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiReader#read(java.util.Set, java.util.Map)
	 */
	@Override
	public OpenAPI read(Set<Class<?>> classes, Map<String, Object> resources) {
		Set<Class<?>> cs = (classes != null) ? new HashSet<>(classes) : new HashSet<>();
		if (!cs.contains(SwaggerV3.CONTEXT_READER_LISTENER)) {
			cs.add(SwaggerV3.CONTEXT_READER_LISTENER);
		}
		return getReader().read(cs, resources);
	}

}
