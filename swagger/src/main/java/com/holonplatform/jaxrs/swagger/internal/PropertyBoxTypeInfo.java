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

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Optional;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.property.PropertyBox;

/**
 * {@link PropertyBox} type descriptor.
 *
 * @since 5.0.0
 */
public interface PropertyBoxTypeInfo extends Serializable {

	/**
	 * Whether Jackson databind is available from classpath of current ClassLoader
	 */
	static final boolean JACKSON_DATABIND_PRESENT = ClassUtils.isPresent("com.fasterxml.jackson.databind.JavaType",
			ClassUtils.getDefaultClassLoader());

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

	/**
	 * Check if given type is a {@link PropertyBox} type.
	 * @param type Type to check
	 * @return if given type is a {@link PropertyBox} type, return a {@link PropertyBoxTypeInfo}. An empty optional
	 *         otherwise.
	 */
	static Optional<PropertyBoxTypeInfo> check(Type type) {
		if (type != null) {
			if (PropertyBox.class == type) {
				return Optional.of(new DefaultPropertyBoxTypeInfo());
			}
			if (PropertyBox[].class == type) {
				return Optional.of(new DefaultPropertyBoxTypeInfo(true));
			}
			// check jackson types
			if (JACKSON_DATABIND_PRESENT) {
				if (type instanceof ArrayType
						&& ((ArrayType) type).getContentType().isTypeOrSubTypeOf(PropertyBox.class)) {
					return Optional.of(new DefaultPropertyBoxTypeInfo(true));
				}
				if (type instanceof JavaType) {
					JavaType javaType = (JavaType) type;
					// check optional
					boolean optional = false;
					if (java.util.Optional.class.equals(javaType.getRawClass())) {
						javaType = javaType.containedType(0);
						optional = true;
					}

					if (javaType.isContainerType()) {
						// check map
						if (javaType.getKeyType() != null && javaType.getContentType() != null) {
							if (javaType.getContentType().isTypeOrSubTypeOf(PropertyBox.class)) {
								DefaultPropertyBoxTypeInfo i = new DefaultPropertyBoxTypeInfo(true);
								i.setMap(true);
								i.setOptional(optional);
								return Optional.of(i);
							}
						} else {
							if (javaType.getContentType().isTypeOrSubTypeOf(PropertyBox.class)) {
								DefaultPropertyBoxTypeInfo i = new DefaultPropertyBoxTypeInfo(true);
								i.setUniqueItems(java.util.Set.class.equals(javaType.getRawClass()));
								i.setOptional(optional);
								return Optional.of(i);
							}
						}
					} else {
						if (javaType.isTypeOrSubTypeOf(PropertyBox.class)) {
							DefaultPropertyBoxTypeInfo i = new DefaultPropertyBoxTypeInfo();
							i.setOptional(optional);
							return Optional.of(i);
						}
					}
				}
			}
		}
		return Optional.empty();
	}

}
