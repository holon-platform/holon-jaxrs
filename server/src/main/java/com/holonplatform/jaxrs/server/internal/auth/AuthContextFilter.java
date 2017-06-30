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

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.Realm;
import com.holonplatform.http.internal.HttpUtils;
import com.holonplatform.jaxrs.server.ResourceUtils;

/**
 * Filter to replace the request {@link SecurityContext} with an {@link AuthContext} compatible implementation, using a
 * {@link Realm} obtained either from a registered {@link ContextResolver} of {@link Realm} type, if available, or as a
 * {@link com.holonplatform.core.Context} resource using {@link Realm#getCurrent()}.
 * 
 * @since 5.0.0
 */
@Priority(Priorities.AUTHENTICATION - 10)
public class AuthContextFilter implements ContainerRequestFilter {

	@Context
	private Providers providers;

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Get realm
		Realm realm = ResourceUtils.lookupResource(getClass(), Realm.class, providers)
				.orElseThrow(() -> new IOException(
						"AuthContext setup failed: no Realm available from a ContextResolver or as a Context resource"));

		// replace SecurityContext
		requestContext.setSecurityContext(
				new AuthSecurityContext(realm, HttpUtils.isSecure(requestContext.getUriInfo().getRequestUri())));
	}

}
