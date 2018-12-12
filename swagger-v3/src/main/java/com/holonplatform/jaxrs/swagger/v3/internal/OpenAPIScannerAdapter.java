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

import java.util.Map;
import java.util.Set;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.v3.OpenAPIContextListener;

import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiScanner;

/**
 * An {@link OpenApiScanner} adapter to automatically include an {@link OpenAPIContextListener}.
 *
 * @since 5.2.0
 */
public class OpenAPIScannerAdapter implements OpenApiScanner {

	private final OpenApiScanner scanner;

	/**
	 * Constructor.
	 * @param scanner Concrete scanner (not null)
	 */
	public OpenAPIScannerAdapter(OpenApiScanner scanner) {
		super();
		ObjectUtils.argumentNotNull(scanner, "Scanner must be not null");
		this.scanner = scanner;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.jaxrs2.integration.JaxrsApplicationAndAnnotationScanner#classes()
	 */
	@Override
	public Set<Class<?>> classes() {
		Set<Class<?>> classes = this.scanner.classes();
		if (!classes.contains(OpenAPIContextListener.class)) {
			classes.add(OpenAPIContextListener.class);
		}
		return classes;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiScanner#setConfiguration(io.swagger.v3.oas.integration.api.
	 * OpenAPIConfiguration)
	 */
	@Override
	public void setConfiguration(OpenAPIConfiguration openApiConfiguration) {
		this.scanner.setConfiguration(openApiConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiScanner#resources()
	 */
	@Override
	public Map<String, Object> resources() {
		return this.scanner.resources();
	}

}
