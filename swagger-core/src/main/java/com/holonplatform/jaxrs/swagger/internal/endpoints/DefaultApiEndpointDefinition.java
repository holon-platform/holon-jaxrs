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
package com.holonplatform.jaxrs.swagger.internal.endpoints;

import java.util.concurrent.Callable;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;

/**
 * Default {@link ApiEndpointDefinition} implementation.
 *
 * @since 5.2.0
 */
public class DefaultApiEndpointDefinition implements ApiEndpointDefinition {

	private static final long serialVersionUID = 4414606362206797736L;

	private final Class<?> endpointClass;
	private final ApiEndpointType type;
	private final JaxrsScannerType scannerType;
	private final String path;
	private final String contextId;
	private final Callable<Void> initializer;

	private boolean initialized = false;

	public DefaultApiEndpointDefinition(Class<?> endpointClass, ApiEndpointType type, JaxrsScannerType scannerType,
			String path, String contextId, Callable<Void> initializer) {
		super();
		ObjectUtils.argumentNotNull(endpointClass, "API endpoint class must be not null");
		ObjectUtils.argumentNotNull(initializer, "Initializer must be not null");
		this.endpointClass = endpointClass;
		this.type = type;
		this.scannerType = scannerType;
		this.path = path;
		this.contextId = contextId;
		this.initializer = initializer;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointDefinition#getEndpointClass()
	 */
	@Override
	public Class<?> getEndpointClass() {
		return endpointClass;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointDefinition#getType()
	 */
	@Override
	public ApiEndpointType getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointDefinition#getScannerType()
	 */
	@Override
	public JaxrsScannerType getScannerType() {
		return scannerType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointDefinition#getPath()
	 */
	@Override
	public String getPath() {
		return path;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointDefinition#getContextId()
	 */
	@Override
	public String getContextId() {
		return contextId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointDefinition#init()
	 */
	@Override
	public boolean init() {
		if (!initialized) {
			try {
				initializer.call();
				initialized = true;
				return true;
			} catch (Exception e) {
				throw new ApiConfigurationException("Failed to initialize context [" + getContextId() + "]", e);
			}
		}
		return false;
	}

}
