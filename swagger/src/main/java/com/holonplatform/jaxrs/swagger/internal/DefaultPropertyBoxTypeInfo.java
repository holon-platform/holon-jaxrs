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
package com.holonplatform.jaxrs.swagger.internal;

/**
 * Default {@link PropertyBoxTypeInfo} implementation.
 *
 * @since 5.0.0
 */
public class DefaultPropertyBoxTypeInfo implements PropertyBoxTypeInfo {

	private static final long serialVersionUID = -3643333268584552728L;

	private boolean containerType;
	private boolean map;
	private boolean uniqueItems;
	private boolean optional;

	public DefaultPropertyBoxTypeInfo() {
		super();
	}

	public DefaultPropertyBoxTypeInfo(boolean containerType) {
		super();
		this.containerType = containerType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.PropertyBoxTypeInfo#isContainerType()
	 */
	@Override
	public boolean isContainerType() {
		return containerType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.PropertyBoxTypeInfo#isMap()
	 */
	@Override
	public boolean isMap() {
		return map;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.PropertyBoxTypeInfo#isUniqueItems()
	 */
	@Override
	public boolean isUniqueItems() {
		return uniqueItems;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.PropertyBoxTypeInfo#isOptional()
	 */
	@Override
	public boolean isOptional() {
		return optional;
	}

	/**
	 * Set whether this type is a container (array, list, set, map) type.
	 * @param containerType <code>true</code> if container type
	 */
	public void setContainerType(boolean containerType) {
		this.containerType = containerType;
	}

	/**
	 * Set whether this type is a Map type.
	 * @param map <code>true</code> if Map type
	 */
	public void setMap(boolean map) {
		this.map = map;
	}

	/**
	 * Set whether this type allows unique items only (set).
	 * @param uniqueItems code>true</code> if allows unique items only
	 */
	public void setUniqueItems(boolean uniqueItems) {
		this.uniqueItems = uniqueItems;
	}

	/**
	 * Set whether the type is wrapped in a Optional.
	 * @param optional <code>true</code> if type is wrapped in a Optional
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

}
