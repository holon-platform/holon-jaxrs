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
package com.holonplatform.jaxrs.server.auth;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.SecurityContext;

import com.holonplatform.jaxrs.server.internal.auth.AuthorizationDynamicFeature;

/**
 * JAX-RS {@link Feature} to enable authorization control based on <code>javax.annotation.security</code> annotations
 * applied to resource classes and/or methods: {@link RolesAllowed}, {@link PermitAll}, {@link DenyAll}.
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
public class AuthorizationFeature implements Feature {

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.Feature#configure(javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public boolean configure(FeatureContext context) {
		// limit to SERVER runtime
		if (RuntimeType.SERVER == context.getConfiguration().getRuntimeType()) {
			if (!context.getConfiguration().isRegistered(AuthorizationDynamicFeature.class)) {
				context.register(AuthorizationDynamicFeature.class);
			}
			return true;
		}
		return false;
	}

}
