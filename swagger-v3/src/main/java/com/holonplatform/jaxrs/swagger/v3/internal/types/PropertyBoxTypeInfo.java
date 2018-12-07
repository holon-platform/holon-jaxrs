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
package com.holonplatform.jaxrs.swagger.v3.internal.types;

import java.io.Serializable;

import com.holonplatform.core.property.PropertyBox;

/**
 * {@link PropertyBox} type descriptor.
 *
 * @since 5.0.0
 */
public interface PropertyBoxTypeInfo extends Serializable {

	/**
	 * Get whether this type is a container (array, list, set, map) type.
	 * @return <code>true</code> if container type
	 */
	boolean isContainerType();

	/**
	 * Get whether this type is a Map type.
	 * @return <code>true</code> if Map type
	 */
	boolean isMap();

	/**
	 * Get whether this type allows unique items only (set).
	 * @return <code>true</code> if allows unique items only
	 */
	boolean isUniqueItems();

	/**
	 * Get whether the type is wrapped in a Optional.
	 * @return <code>true</code> if type is wrapped in a Optional
	 */
	boolean isOptional();

}
