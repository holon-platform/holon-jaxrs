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
package com.holonplatform.jaxrs.swagger.v3.internal.types;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.property.PropertyBox;

/**
 * Resolver for {@link PropertyBox} types.
 *
 * @since 5.2.0
 */
public final class PropertyBoxTypeResolver implements Serializable {

	private static final long serialVersionUID = 2742561717417145267L;

	/**
	 * Whether Jackson databind is available from classpath of current ClassLoader
	 */
	static final boolean JACKSON_DATABIND_PRESENT = ClassUtils.isPresent("com.fasterxml.jackson.databind.JavaType",
			ClassUtils.getDefaultClassLoader());

	private PropertyBoxTypeResolver() {
	}

	/**
	 * Check if given type is a {@link PropertyBox} type.
	 * @param type The type to check (may be null)
	 * @return If given type is a {@link PropertyBox} type, returns a {@link PropertyBoxTypeInfo} descriptor. Otherwise,
	 *         an empty Optional is returned.
	 */
	public static Optional<PropertyBoxTypeInfo> resolvePropertyBoxType(Type type) {
		if (type != null) {
			if (JACKSON_DATABIND_PRESENT) {
				return resolveAsJavaType(TypeFactory.defaultInstance().constructType(type));
			}
			return resolveAsType(type);
		}
		return Optional.empty();
	}

	/**
	 * Try to resolve given type as a Java type.
	 * @param type The type to resolve (not null)
	 * @return If given type is a {@link PropertyBox} type, returns a {@link PropertyBoxTypeInfo} descriptor. Otherwise,
	 *         an empty Optional is returned.
	 */
	private static Optional<PropertyBoxTypeInfo> resolveAsType(Type type) {
		// array
		if (PropertyBox[].class == type) {
			return Optional.of(new DefaultPropertyBoxTypeInfo(true));
		}
		// single
		if (PropertyBox.class == type) {
			Class<?> cls = SwaggerTypeUtils.getClassFromType(type).orElse(null);
			if (cls != null) {
				// check container
				Class<?> elementType = null;
				if (List.class.isAssignableFrom(cls) || Set.class.isAssignableFrom(cls)
						|| Optional.class.isAssignableFrom(cls)) {
					elementType = SwaggerTypeUtils.getParametrizedType(cls).orElse(null);
				}
				if (elementType != null && PropertyBox.class.isAssignableFrom(elementType)) {
					if (List.class.isAssignableFrom(cls)) {
						return Optional.of(new DefaultPropertyBoxTypeInfo(true));
					}
					if (Set.class.isAssignableFrom(cls)) {
						DefaultPropertyBoxTypeInfo i = new DefaultPropertyBoxTypeInfo(true);
						i.setUniqueItems(true);
						return Optional.of(i);
					}
					if (Optional.class.isAssignableFrom(cls)) {
						DefaultPropertyBoxTypeInfo i = new DefaultPropertyBoxTypeInfo(false);
						i.setOptional(true);
						return Optional.of(i);
					}
				}
			}
			return Optional.of(new DefaultPropertyBoxTypeInfo(false));
		}
		return Optional.empty();
	}

	/**
	 * Try to resolve given type as a Jackson JavaType, if Jackson databind library is available from classpath.
	 * @param type The type to resolve (not null)
	 * @return If given type is a {@link PropertyBox} type, returns a {@link PropertyBoxTypeInfo} descriptor. Otherwise,
	 *         an empty Optional is returned.
	 */
	private static Optional<PropertyBoxTypeInfo> resolveAsJavaType(JavaType type) {
		// check array type
		if (type instanceof ArrayType && ((ArrayType) type).getContentType().isTypeOrSubTypeOf(PropertyBox.class)) {
			DefaultPropertyBoxTypeInfo i = new DefaultPropertyBoxTypeInfo(true);
			return Optional.of(i);
		}

		// check optional
		JavaType javaType = type;
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
					return Optional.of(i);
				}
			} else {
				if (javaType.getContentType().isTypeOrSubTypeOf(PropertyBox.class)) {
					DefaultPropertyBoxTypeInfo i = new DefaultPropertyBoxTypeInfo(true);
					i.setUniqueItems(java.util.Set.class.equals(javaType.getRawClass()));
					return Optional.of(i);
				}
			}
		} else {
			if (javaType.isTypeOrSubTypeOf(PropertyBox.class)) {
				DefaultPropertyBoxTypeInfo i = new DefaultPropertyBoxTypeInfo(false);
				i.setOptional(optional);
				javaType.getRawClass().getAnnotations();
				return Optional.of(i);
			}
		}
		return Optional.empty();
	}

}
