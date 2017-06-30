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

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.SecurityContext;

/**
 * JAX-RS {@link DynamicFeature} to enable authorization control based on <code>javax.annotation.security</code>
 * annotations applied to resource classes and/or methods: {@link RolesAllowed}, {@link PermitAll}, {@link DenyAll}.
 * <p>
 * The {@link SecurityContext#isUserInRole(String)} method is used to perform authorization controls. If a user is in
 * none of the declared roles then a 403 (Forbidden) response is returned.
 * </p>
 * <p>
 * If the {@link DenyAll} annotation is declared a 403 (Forbidden) response is always returned. If the {@link PermitAll}
 * annotation is declared and is not overridden then the filter will not be applied.
 * </p>
 * 
 * @since 5.0.0
 */
public class AuthorizationDynamicFeature implements DynamicFeature {

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.container.DynamicFeature#configure(javax.ws.rs.container.ResourceInfo,
	 * javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		
		// DenyAll on method take precedence over RolesAllowed and PermitAll
		if (resourceInfo.getResourceMethod().isAnnotationPresent(DenyAll.class)) {
			context.register(new AuthorizationFilter(), Priorities.AUTHORIZATION);
			return;
		}

		// RolesAllowed on method takes precedence over PermitAll
		RolesAllowed ra = resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class);
		if (ra != null) {
			context.register(new AuthorizationFilter(ra.value()), Priorities.AUTHORIZATION);
			return;
		}

		// PermitAll takes precedence over RolesAllowed on the class
		if (resourceInfo.getResourceMethod().isAnnotationPresent(PermitAll.class)) {
			// do not apply filter
			return;
		}

		// RolesAllowed on the class takes precedence over PermitAll. DenyAll can't be attached to classes.
		ra = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
		if (ra != null) {
			context.register(new AuthorizationFilter(ra.value()), Priorities.AUTHORIZATION);
		}
		
	}

}
