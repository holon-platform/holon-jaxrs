/*
 * Copyright 2000-2016 Holon TDCN.
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
package com.holonplatform.jaxrs.server.utils;

import java.io.Serializable;
import java.util.Optional;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import com.holonplatform.core.Context;
import com.holonplatform.core.internal.utils.ObjectUtils;

/**
 * Utility class to handle generic resource within the JAX-RS server context.
 *
 * @since 5.0.0
 */
public final class ResourceUtils implements Serializable {

	private static final long serialVersionUID = -6271475972085523510L;

	private ResourceUtils() {
	}

	/**
	 * Lookup a resource of given <code>resourceType</code> type, either using a suitable {@link ContextResolver} if
	 * <code>providers</code> is not <code>null</code> and a {@link ContextResolver} for given <code>resourceType</code>
	 * is available, or trying to obtain the resource from {@link Context} using given <code>resourceType</code> as
	 * context resource key.
	 * @param <R> Resource type
	 * @param caller Caller class
	 * @param resourceType Resource type to lookup (not null)
	 * @param providers JAX-RS {@link Providers}, if available
	 * @return Resource instance, or an empty optional if not available
	 */
	public static <R> Optional<R> lookupResource(Class<?> caller, Class<R> resourceType, Providers providers) {
		ObjectUtils.argumentNotNull(resourceType, "Resource type must be not null");

		R resource = null;

		// try to use a ContextResolver, if available
		ContextResolver<R> resolver = providers.getContextResolver(resourceType, null);
		if (resolver != null) {
			resource = resolver.getContext(caller);
		}

		if (resource == null) {
			// lookup in context
			resource = Context.get().resource(resourceType).orElse(null);
		}

		return Optional.ofNullable(resource);
	}

}
