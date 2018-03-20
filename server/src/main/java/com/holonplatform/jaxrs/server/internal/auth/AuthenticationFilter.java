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
import javax.ws.rs.ext.Providers;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.exceptions.AuthenticationException;
import com.holonplatform.auth.exceptions.UnsupportedMessageException;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.jaxrs.internal.JaxrsLogger;
import com.holonplatform.jaxrs.server.internal.JaxrsContainerHttpRequest;
import com.holonplatform.jaxrs.server.internal.ResponseUtils;

/**
 * Filter to check if current {@link AuthContext} security context is authenticated, and if not, perform authentication
 * using current request message and optional allowed authentication schemes.
 * 
 * @since 5.0.0
 */
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	private final static Logger LOGGER = JaxrsLogger.create();

	@Context
	private Providers providers;

	/**
	 * Authentication schemes
	 */
	private final String[] schemes;

	/**
	 * Constructor
	 * @param schemes Authentication schemes to use. If <code>null</code> or empty, any scheme is allowed
	 */
	public AuthenticationFilter(String[] schemes) {
		super();
		this.schemes = schemes;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// check SecurityContext type
		if (!AuthContext.class.isAssignableFrom(requestContext.getSecurityContext().getClass())) {
			throw new IOException("Invalid SecurityContext type: expecting an AuthContext but found ["
					+ requestContext.getSecurityContext().getClass().getName() + "]");
		}

		final AuthContext authContext = (AuthContext) requestContext.getSecurityContext();
		// check authenticated
		if (!authContext.getAuthentication().isPresent()) {

			LOGGER.debug(() -> "Authenticate request using AuthContext");

			// authenticate
			try {
				authContext.authenticate(new JaxrsContainerHttpRequest(requestContext), schemes);
			} catch (UnsupportedMessageException e) {

				LOGGER.debug(() -> "Authentication error: aborting request", e);

				requestContext.abortWith(ResponseUtils.buildAuthenticationErrorResponse(schemes, null, null,
						HttpStatus.UNAUTHORIZED.getCode(), null));
			} catch (AuthenticationException e) {

				LOGGER.debug(() -> "Authentication error: aborting request", e);

				requestContext.abortWith(ResponseUtils.buildAuthenticationErrorResponse(e, null));
			}
		}

	}

}
