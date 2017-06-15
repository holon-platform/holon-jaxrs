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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.property.PropertyBox;

/**
 * Swagger integration utility class.
 *
 * @since 5.0.0
 */
public final class SwaggerUtils implements Serializable {

	private static final long serialVersionUID = 1199929442212057271L;
	/**
	 * Whether QueryDSL is available from classpath of current ClassLoader
	 */
	public static final boolean JACKSON_DATABIND_PRESENT = ClassUtils
			.isPresent("com.fasterxml.jackson.databind.JavaType", ClassUtils.getDefaultClassLoader());

	private SwaggerUtils() {
	}

	/**
	 * Checks whether given <code>type</code> is a {@link PropertyBox} type.
	 * @param type Type to check
	 * @return <code>true</code> if given <code>type</code> is a {@link PropertyBox} type
	 */
	public static boolean isPropertyBoxType(Type type) {
		if (type != null) {
			if (PropertyBox.class.equals(type)) {
				return true;
			}
			if (JACKSON_DATABIND_PRESENT && type instanceof JavaType) {
				return ((JavaType) type).isTypeOrSubTypeOf(PropertyBox.class);
			}
		}
		return false;
	}

	/**
	 * Checks whether given <code>type</code> is a {@link PropertyBox} array type.
	 * @param type Type to check
	 * @return <code>true</code> if given <code>type</code> is a {@link PropertyBox} array type
	 */
	public static boolean isPropertyBoxArrayType(Type type) {
		if (type != null) {
			if (PropertyBox[].class.equals(type)) {
				return true;
			}
			if (JACKSON_DATABIND_PRESENT && type instanceof JavaType) {
				return ((JavaType) type).isTypeOrSubTypeOf(PropertyBox[].class);
			}
			if (JACKSON_DATABIND_PRESENT && type instanceof ArrayType) {
				return ((ArrayType) type).getContentType().isTypeOrSubTypeOf(PropertyBox.class);
			}
		}
		return false;
	}

	/**
	 * Checks whether given <code>type</code> is a simple or array {@link PropertyBox} type.
	 * @param type Type to check
	 * @return <code>true</code> if given <code>type</code> is a simple or array {@link PropertyBox} type
	 */
	public static boolean isPropertyBoxOrArrayType(Type type) {
		return isPropertyBoxType(type) || isPropertyBoxArrayType(type);
	}

}
