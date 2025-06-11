/*
 * Copyright 2016-2017 Axioma srl.
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

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

import jakarta.ws.rs.core.SecurityContext;

import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.AuthenticationInspector;
import com.holonplatform.auth.Permission;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.server.auth.JaxrsAuthenticationInspector;

/**
 * Default {@link JaxrsAuthenticationInspector} implementation.
 *
 * @since 5.1.0
 */
public class DefaultJaxrsAuthenticationInspector implements JaxrsAuthenticationInspector {

	private final SecurityContext securityContext;

	/**
	 * Constructor.
	 * @param securityContext JAX-RS SecurityContext (not null)
	 */
	public DefaultJaxrsAuthenticationInspector(SecurityContext securityContext) {
		super();
		ObjectUtils.argumentNotNull(securityContext, "SecurityContext must be not null");
		this.securityContext = securityContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthenticationInspector#isAuthenticated()
	 */
	@Override
	public boolean isAuthenticated() {
		return securityContext.getUserPrincipal() != null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthenticationInspector#getAuthentication()
	 */
	@Override
	public Optional<Authentication> getAuthentication() {
		Principal principal = securityContext.getUserPrincipal();
		if (principal != null && Authentication.class.isAssignableFrom(principal.getClass())) {
			return Optional.of((Authentication) principal);
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthenticationInspector#isPermitted(java.util.Collection)
	 */
	@Override
	public boolean isPermitted(Collection<? extends Permission> permissions) {
		return isAuthenticationInspector().map(a -> a.isPermitted(permissions))
				.orElse(isPermitted(getPermissionRoles(permissions)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthenticationInspector#isPermitted(java.lang.String[])
	 */
	@Override
	public boolean isPermitted(String... permissions) {
		return isAuthenticationInspector().map(a -> a.isPermitted(permissions)).orElse(hasAllRoles(permissions));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthenticationInspector#isPermittedAny(java.util.Collection)
	 */
	@Override
	public boolean isPermittedAny(Collection<? extends Permission> permissions) {
		return isAuthenticationInspector().map(a -> a.isPermittedAny(permissions))
				.orElse(isPermittedAny(getPermissionRoles(permissions)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthenticationInspector#isPermittedAny(java.lang.String[])
	 */
	@Override
	public boolean isPermittedAny(String... permissions) {
		return isAuthenticationInspector().map(a -> a.isPermittedAny(permissions)).orElse(hasAnyRole(permissions));
	}

	/**
	 * Checks whether the {@link SecurityContext} is an {@link AuthenticationInspector}.
	 * @return The security context as an {@link AuthenticationInspector} if compatible, an empty optional otherwise
	 */
	protected Optional<AuthenticationInspector> isAuthenticationInspector() {
		if (securityContext instanceof AuthenticationInspector) {
			return Optional.of((AuthenticationInspector) securityContext);
		}
		return Optional.empty();
	}

	/**
	 * Checks if the current security context Principal has all the given roles, using
	 * {@link SecurityContext#isUserInRole(String)}.
	 * @param roles Roles to check
	 * @return <code>true</code> if the current security context Principal has all the given roles, <code>false</code>
	 *         otherwise
	 */
	protected boolean hasAllRoles(String... roles) {
		if (roles != null) {
			for (String role : roles) {
				if (!securityContext.isUserInRole(role)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the current security context Principal has any of the given role, using
	 * {@link SecurityContext#isUserInRole(String)}.
	 * @param roles Roles to check
	 * @return <code>true</code> if the current security context Principal has any of the given role, <code>false</code>
	 *         otherwise
	 */
	protected boolean hasAnyRole(String... roles) {
		if (roles == null || roles.length == 0) {
			return true;
		}
		for (String role : roles) {
			if (securityContext.isUserInRole(role)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get given {@link Permission}s as an array of String permission roles, using {@link Permission#getPermission()}.
	 * @param permissions The permissions to process
	 * @return An array of the String permission roles associated to given permissions, when available
	 */
	private static String[] getPermissionRoles(Collection<? extends Permission> permissions) {
		if (permissions == null || permissions.size() == 0) {
			return new String[0];
		}
		return permissions.stream().filter(p -> p.getPermission().isPresent()).map(p -> p.getPermission().orElse(null))
				.toArray(String[]::new);
	}

}
