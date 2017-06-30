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
package com.holonplatform.jaxrs.server.internal.auth;

import java.lang.reflect.AnnotatedElement;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import com.holonplatform.auth.annotations.Authenticate;

/**
 * A {@link DynamicFeature} to add an {@link AuthenticationFilter} to {@link Authenticate} annotated JAX-RS resource
 * classes and methods.
 *
 * @since 5.0.0
 */
public class AuthenticationDynamicFeature implements DynamicFeature {

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.container.DynamicFeature#configure(javax.ws.rs.container.ResourceInfo,
	 * javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		// check method
		if (!registerAuthenticationFilters(context, resourceInfo.getResourceMethod())) {
			// check class
			registerAuthenticationFilters(context, resourceInfo.getResourceClass());
		}
	}

	/**
	 * Checks if given <code>element</code> has the {@link Authenticate} annotation and if so registers the required
	 * authentication filters.
	 * @param context Feature context
	 * @param element Annotated element
	 * @return <code>true</code> if the {@link Authenticate} annotation was found and the authentication filters were
	 *         registered, <code>false</code> otherwise
	 */
	private static boolean registerAuthenticationFilters(FeatureContext context, AnnotatedElement element) {
		if (element.isAnnotationPresent(Authenticate.class)) {
			// AuthContext setup for SecurityContext
			context.register(AuthContextFilter.class, Priorities.AUTHENTICATION - 10);
			// Authenticator
			context.register(new AuthenticationFilter(element.getAnnotation(Authenticate.class).schemes()),
					Priorities.AUTHENTICATION);
			return true;
		}
		return false;
	}

}
