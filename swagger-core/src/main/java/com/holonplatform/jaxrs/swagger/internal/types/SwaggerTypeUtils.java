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
package com.holonplatform.jaxrs.swagger.internal.types;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;

/**
 * Type handling utility class.
 *
 * @since 5.2.0
 */
public final class SwaggerTypeUtils implements Serializable {

	private static final long serialVersionUID = -2319588973652574569L;

	private static final Logger LOGGER = SwaggerLogger.create();

	private SwaggerTypeUtils() {
	}

	/**
	 * Try to obtain the generic type argument, if given class is a parametrized type.
	 * @param cls The class
	 * @return Optional generic type argument
	 */
	public static Optional<Class<?>> getParametrizedType(Class<?> cls) {
		try {
			for (Type gt : cls.getGenericInterfaces()) {
				if (gt instanceof ParameterizedType) {
					final Type[] types = ((ParameterizedType) gt).getActualTypeArguments();
					if (types != null && types.length > 0) {
						return getClassFromType(types[0]);
					}
				}
			}
			if (cls.getGenericSuperclass() instanceof ParameterizedType) {
				final Type[] types = ((ParameterizedType) cls.getGenericSuperclass()).getActualTypeArguments();
				if (types != null && types.length > 0) {
					return getClassFromType(types[0]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("Failed to detect parametrized type for class [" + cls + "]", e);
		}
		return Optional.empty();
	}

	/**
	 * Get the Class which corresponds to given type, if available.
	 * @param type The type
	 * @return Optional type class
	 */
	public static Optional<Class<?>> getClassFromType(Type type) {
		if (type instanceof Class<?>) {
			return Optional.of((Class<?>) type);
		}
		try {
			return Optional.ofNullable(Class.forName(type.getTypeName()));
		} catch (Exception e) {
			LOGGER.warn("Failed to obtain a Class from Type name [" + type.getTypeName() + "]: " + e.getMessage());
		}
		return Optional.empty();
	}

}
