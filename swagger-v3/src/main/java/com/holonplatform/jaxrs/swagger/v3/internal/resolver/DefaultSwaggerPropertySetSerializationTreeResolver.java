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
package com.holonplatform.jaxrs.swagger.v3.internal.resolver;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.json.model.PropertySetSerializationTree;
import com.holonplatform.json.model.PropertySetSerializationTreeResolver;

/**
 * Default {@link SwaggerPropertySetSerializationTreeResolver} implementation.
 *
 * @since 5.2.0
 */
public enum DefaultSwaggerPropertySetSerializationTreeResolver implements SwaggerPropertySetSerializationTreeResolver {

	INSTANCE;

	private final static Predicate<Property<?>> PATH_VALIDATOR = property -> {
		return Path.class.isAssignableFrom(property.getClass());
	};

	/**
	 * Resolver
	 */
	private final static PropertySetSerializationTreeResolver RESOLVER = PropertySetSerializationTreeResolver.builder()
			.validator(PATH_VALIDATOR).build();

	/**
	 * Cache
	 */
	private final static Map<PropertySet<?>, PropertySetSerializationTree> CACHE = new WeakHashMap<>();

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v3.internal.resolver.SwaggerPropertySetSerializationTreeResolver#resolve(com.
	 * holonplatform.core.property.PropertySet)
	 */
	@Override
	public PropertySetSerializationTree resolve(PropertySet<?> propertySet) {
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		return CACHE.computeIfAbsent(propertySet, ps -> RESOLVER.resolve(propertySet));
	}

}
