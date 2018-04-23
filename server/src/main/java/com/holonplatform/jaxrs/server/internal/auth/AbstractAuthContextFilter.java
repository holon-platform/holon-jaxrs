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

import java.io.IOException;
import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.Realm;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.http.internal.HttpUtils;
import com.holonplatform.jaxrs.internal.JaxrsLogger;
import com.holonplatform.jaxrs.server.ResourceUtils;
import com.holonplatform.jaxrs.server.auth.AuthenticationFeature;

/**
 * Base filter to replace the request {@link SecurityContext} with an {@link AuthContext} compatible implementation,
 * using a {@link Realm} obtained either from a registered {@link ContextResolver} of {@link Realm} type, if available,
 * or as a {@link com.holonplatform.core.Context} resource using {@link Realm#getCurrent()}.
 *
 * @since 5.1.0
 */
public abstract class AbstractAuthContextFilter implements ContainerRequestFilter {

	private final static Logger LOGGER = JaxrsLogger.create();

	@Context
	private Providers providers;

	private final boolean realmRequired;

	/**
	 * Constructor.
	 * @param realmRequired Whether to fail if a {@link Realm} is not available
	 */
	public AbstractAuthContextFilter(boolean realmRequired) {
		super();
		this.realmRequired = realmRequired;
	}

	/**
	 * Get the {@link AuthContext} to use.
	 * @param realm Auth {@link Realm}
	 * @return The {@link AuthContext} instance (not null)
	 */
	protected abstract AuthContext getAuthContext(Realm realm);

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		if (checkRequestAuthContext(requestContext) == null) {

			// Get realm
			Optional<Realm> realm = ResourceUtils.lookupResource(getClass(), Realm.class, providers);

			if (!realm.isPresent() && realmRequired) {
				throw new IOException(
						"AuthContext setup failed: no Realm available from a ContextResolver or as a Context resource");
			}

			// get AuthContext
			final AuthContext authContext = getAuthContext(realm.orElse(null));

			// replace SecurityContext
			requestContext.setSecurityContext(new AuthSecurityContext(authContext,
					HttpUtils.isSecure(requestContext.getUriInfo().getRequestUri())));

			// set request property
			requestContext.setProperty(AuthenticationFeature.AUTH_CONTEXT_PROPERTY_NAME, authContext);

			LOGGER.debug(() -> "Registered [" + AbstractAuthContextFilter.this.getClass().getName() + "] filter");

		} else {
			LOGGER.debug(() -> "AuthContext already available from current request: skip ["
					+ AbstractAuthContextFilter.this.getClass().getName() + "] filter registration");
		}

	}

	/**
	 * Check if an {@link AuthContext} instance is available from current request using
	 * {@link AuthenticationFeature#AUTH_CONTEXT_PROPERTY_NAME} property name.
	 * @param requestContext Request context
	 * @return The {@link AuthContext} instance, or <code>null</code> if not available
	 */
	private static AuthContext checkRequestAuthContext(ContainerRequestContext requestContext) {
		Object value = requestContext.getProperty(AuthenticationFeature.AUTH_CONTEXT_PROPERTY_NAME);
		if (value != null && AuthContext.class.isAssignableFrom(value.getClass())) {
			return (AuthContext) value;
		}
		return null;
	}

}
