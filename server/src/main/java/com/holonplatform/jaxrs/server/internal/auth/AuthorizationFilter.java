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
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;

/**
 * Filter to perform authorization control relying on specified roles.
 * 
 * @since 5.0.0
 */
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

	/**
	 * Allowed roles
	 */
	private final String[] roles;

	/**
	 * Deny all
	 */
	private final boolean denyAll;

	/**
	 * Constructor for DenyAll
	 */
	public AuthorizationFilter() {
		super();
		this.denyAll = true;
		this.roles = null;
	}

	/**
	 * Constructor
	 * @param roles Allowed roles
	 */
	public AuthorizationFilter(String[] roles) {
		super();
		this.denyAll = false;
		this.roles = (roles != null) ? roles : new String[] {};
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (!denyAll) {
			if (roles.length > 0 && !isAuthenticated(requestContext)) {
				throw new ForbiddenException("Access denied to requested resource");
			}

			for (final String role : roles) {
				if (requestContext.getSecurityContext().isUserInRole(role)) {
					return;
				}
			}
		}

		throw new ForbiddenException("Access denied to requested resource");

	}

	/**
	 * Checks whether the {@link SecurityContext} is authenticated, i.e. {@link SecurityContext#getUserPrincipal()} is
	 * not null.
	 * @param requestContext Request context
	 * @return <code>true</code> if the {@link SecurityContext} is authenticated
	 */
	private static boolean isAuthenticated(final ContainerRequestContext requestContext) {
		return requestContext.getSecurityContext().getUserPrincipal() != null;
	}

}
