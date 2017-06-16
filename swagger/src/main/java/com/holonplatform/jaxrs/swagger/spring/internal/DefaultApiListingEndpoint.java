/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.jaxrs.swagger.spring.internal;

/**
 * Default {@link ApiListingEndpoint} implementation.
 * 
 * @since 5.0.0
 */
public class DefaultApiListingEndpoint implements ApiListingEndpoint {

	private static final long serialVersionUID = -5097164822189166660L;

	private final String groupId;
	private final String path;
	private final Class<?> resourceClass;

	/**
	 * Constructor
	 * @param groupId Group id
	 * @param path Path
	 * @param resourceClass Resource class
	 */
	public DefaultApiListingEndpoint(String groupId, String path, Class<?> resourceClass) {
		super();
		this.groupId = groupId;
		this.path = path;
		this.resourceClass = resourceClass;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.ApiListingEndpoint#getGroupId()
	 */
	@Override
	public String getGroupId() {
		return groupId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.ApiListingEndpoint#getPath()
	 */
	@Override
	public String getPath() {
		return path;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.ApiListingEndpoint#getResourceClass()
	 */
	@Override
	public Class<?> getResourceClass() {
		return resourceClass;
	}

}
