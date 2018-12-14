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
package com.holonplatform.jaxrs.swagger.internal;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.ApiEndpointDefinition;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;

/**
 * Default {@link ApiEndpointDefinition} implementation.
 *
 * @since 5.2.0
 */
public class DefaultApiEndpointDefinition implements ApiEndpointDefinition {

	private static final long serialVersionUID = 4414606362206797736L;

	private final Class<?> endpointClass;
	private final ApiEndpointType type;
	private final String path;
	private final String contextId;

	public DefaultApiEndpointDefinition(Class<?> endpointClass, ApiEndpointType type, String path, String contextId) {
		super();
		ObjectUtils.argumentNotNull(endpointClass, "API endpoint class must be not null");
		this.endpointClass = endpointClass;
		this.type = type;
		this.path = path;
		this.contextId = contextId;
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

}
