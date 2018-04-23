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

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

import javax.ws.rs.core.SecurityContext;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.Authentication.AuthenticationListener;
import com.holonplatform.auth.AuthenticationToken;
import com.holonplatform.auth.Permission;
import com.holonplatform.auth.exceptions.AuthenticationException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.messaging.Message;

/**
 * A {@link SecurityContext} implementation which uses an {@link AuthContext} to perform authentication, provide
 * authenticated informations and check permissions.
 * <p>
 * If the context is authenticated, {@link #getUserPrincipal()} method always returns an {@link Authentication}
 * principal type.
 * </p>
 * 
 * @since 5.0.0
 */
public class AuthSecurityContext implements AuthContext, SecurityContext {

	/**
	 * Concrete {@link AuthContext}
	 */
	private final AuthContext authContext;

	/**
	 * Whether the authentication context is handled using a secure channel (such as HTTPS)
	 */
	private final boolean secureChannel;

	/**
	 * Construct a new AuthSecurityContext.
	 * @param authContext Concrete {@link AuthContext} (not null)
	 * @param secureChannel Whether the authentication context is handled using a secure channel (such as HTTPS)
	 */
	public AuthSecurityContext(AuthContext authContext, boolean secureChannel) {
		super();
		ObjectUtils.argumentNotNull(authContext, "AuthContext must be not null");
		this.authContext = authContext;
		this.secureChannel = secureChannel;
	}

	/**
	 * Get the concrete {@link AuthContext}.
	 * @return the auth context
	 */
	protected AuthContext getAuthContext() {
		return authContext;
	}

	/**
	 * The {@link Principal} returned is the current {@link Authentication}, if any
	 * @return {@link Authentication} principal, or <code>null</code> if the context is not authenticated
	 */
	@Override
	public Principal getUserPrincipal() {
		return getAuthentication().orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.SecurityContext#isUserInRole(java.lang.String)
	 */
	@Override
	public boolean isUserInRole(String role) {
		return role != null && isPermitted(role);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.SecurityContext#isSecure()
	 */
	@Override
	public boolean isSecure() {
		return secureChannel;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.SecurityContext#getAuthenticationScheme()
	 */
	@Override
	public String getAuthenticationScheme() {
		return getAuthentication().map(a -> a.getScheme().orElse(null)).orElse(null);
	}

	// ------- AuthContext

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.auth.Authentication.AuthenticationNotifier#addAuthenticationListener(com.holonplatform.auth.
	 * Authentication.AuthenticationListener)
	 */
	@Override
	public void addAuthenticationListener(AuthenticationListener authenticationListener) {
		getAuthContext().addAuthenticationListener(authenticationListener);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.auth.Authentication.AuthenticationNotifier#removeAuthenticationListener(com.holonplatform.auth.
	 * Authentication.AuthenticationListener)
	 */
	@Override
	public void removeAuthenticationListener(AuthenticationListener authenticationListener) {
		getAuthContext().removeAuthenticationListener(authenticationListener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthContext#getAuthentication()
	 */
	@Override
	public Optional<Authentication> getAuthentication() {
		return getAuthContext().getAuthentication();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthContext#authenticate(com.holonplatform.auth.AuthenticationToken)
	 */
	@Override
	public Authentication authenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
		return getAuthContext().authenticate(authenticationToken);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthContext#authenticate(com.holonplatform.core.messaging.Message,
	 * java.lang.String[])
	 */
	@Override
	public Authentication authenticate(Message<?, ?> message, String... schemes) throws AuthenticationException {
		return getAuthContext().authenticate(message, schemes);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthContext#unauthenticate()
	 */
	@Override
	public Optional<Authentication> unauthenticate() {
		return getAuthContext().unauthenticate();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthContext#isPermitted(com.holonplatform.auth.Permission[])
	 */
	@Override
	public boolean isPermitted(Permission... permissions) {
		return getAuthContext().isPermitted(permissions);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthContext#isPermitted(java.lang.String[])
	 */
	@Override
	public boolean isPermitted(String... permissions) {
		return getAuthContext().isPermitted(permissions);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthContext#isPermittedAny(com.holonplatform.auth.Permission[])
	 */
	@Override
	public boolean isPermittedAny(Permission... permissions) {
		return getAuthContext().isPermittedAny(permissions);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthContext#isPermittedAny(java.lang.String[])
	 */
	@Override
	public boolean isPermittedAny(String... permissions) {
		return getAuthContext().isPermittedAny(permissions);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthContext#isPermitted(java.util.Collection)
	 */
	@Override
	public boolean isPermitted(Collection<? extends Permission> permissions) {
		return getAuthContext().isPermitted(permissions);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.auth.AuthContext#isPermittedAny(java.util.Collection)
	 */
	@Override
	public boolean isPermittedAny(Collection<? extends Permission> permissions) {
		return getAuthContext().isPermittedAny(permissions);
	}

}
