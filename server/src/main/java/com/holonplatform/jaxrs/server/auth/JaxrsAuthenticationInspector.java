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
package com.holonplatform.jaxrs.server.auth;

import jakarta.ws.rs.core.SecurityContext;

import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.AuthenticationInspector;
import com.holonplatform.auth.Permission;
import com.holonplatform.jaxrs.server.internal.auth.DefaultJaxrsAuthenticationInspector;

/**
 * An {@link AuthenticationInspector} which can be used to inspect an {@link Authentication} based security context
 * using the standard JAX-RS {@link SecurityContext}.
 *
 * @since 5.1.0
 */
public interface JaxrsAuthenticationInspector extends AuthenticationInspector {

	/**
	 * Create a {@link JaxrsAuthenticationInspector} using given JAX-RS {@link SecurityContext}.
	 * <p>
	 * The default JAX-RS AuthenticationInspector checks if the security context principal is available and it is an
	 * {@link Authentication} compatible instance. Regarding the permission control, if the given security context is an
	 * {@link AuthenticationInspector} itself, the permission check is directly delegated to it. Otherwise, the
	 * {@link Permission#getPermission()} method is used to obtain the permission as a String role representation and
	 * the authorization control is performed using the {@link SecurityContext#isUserInRole(String)} method.
	 * </p>
	 * @param securityContext The SecurityContext (not null)
	 * @return A new {@link JaxrsAuthenticationInspector} for given security context
	 */
	static JaxrsAuthenticationInspector of(SecurityContext securityContext) {
		return new DefaultJaxrsAuthenticationInspector(securityContext);
	}

}
