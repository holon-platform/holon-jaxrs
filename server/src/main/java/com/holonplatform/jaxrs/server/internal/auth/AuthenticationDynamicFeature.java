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
		if (resourceInfo.getResourceMethod().isAnnotationPresent(Authenticate.class)) {
			context.register(
					new AuthenticationFilter(
							resourceInfo.getResourceMethod().getAnnotation(Authenticate.class).schemes()),
					Priorities.AUTHENTICATION);
			return;
		}
		// check class
		if (resourceInfo.getResourceClass().isAnnotationPresent(Authenticate.class)) {
			context.register(
					new AuthenticationFilter(
							resourceInfo.getResourceClass().getAnnotation(Authenticate.class).schemes()),
					Priorities.AUTHENTICATION);
			return;
		}
	}

}
