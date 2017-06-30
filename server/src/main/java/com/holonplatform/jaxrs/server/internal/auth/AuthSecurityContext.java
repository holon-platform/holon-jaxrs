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

import javax.ws.rs.core.SecurityContext;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.Realm;
import com.holonplatform.auth.internal.DefaultAuthContext;

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
public class AuthSecurityContext extends DefaultAuthContext implements SecurityContext {

	/**
	 * Whether the authentication context is handled using a secure channel (such as HTTPS)
	 */
	private final boolean secureChannel;

	/**
	 * Construct a new AuthSecurityContext
	 * @param realm Authentication realm (not null)
	 * @param secureChannel Whether the authentication context is handled using a secure channel (such as HTTPS)
	 */
	public AuthSecurityContext(Realm realm, boolean secureChannel) {
		super(realm);
		this.secureChannel = secureChannel;
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

}
