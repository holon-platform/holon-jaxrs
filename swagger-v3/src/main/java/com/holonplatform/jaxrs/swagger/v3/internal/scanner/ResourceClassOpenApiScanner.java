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
package com.holonplatform.jaxrs.swagger.v3.internal.scanner;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiScanner;

/**
 * An {@link OpenApiScanner} which directly provided a set of classes API resources.
 *
 * @since 5.2.0
 */
public class ResourceClassOpenApiScanner implements OpenApiScanner {

	protected OpenAPIConfiguration openApiConfiguration;

	private final Set<Class<?>> classes;

	/**
	 * Constructor.
	 * @param classes The classes to provide as API resources
	 */
	public ResourceClassOpenApiScanner(Set<Class<?>> classes) {
		super();
		this.classes = (classes != null) ? classes : Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiScanner#setConfiguration(io.swagger.v3.oas.integration.api.
	 * OpenAPIConfiguration)
	 */
	@Override
	public void setConfiguration(OpenAPIConfiguration openApiConfiguration) {
		this.openApiConfiguration = openApiConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiScanner#classes()
	 */
	@Override
	public Set<Class<?>> classes() {
		return classes;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiScanner#resources()
	 */
	@Override
	public Map<String, Object> resources() {
		return Collections.emptyMap();
	}

}
