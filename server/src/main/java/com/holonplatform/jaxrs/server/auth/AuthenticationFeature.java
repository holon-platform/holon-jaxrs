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

import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.AuthenticationToken.AuthenticationTokenResolver;
import com.holonplatform.auth.Realm;
import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.http.HttpHeaders;
import com.holonplatform.jaxrs.internal.JaxrsLogger;
import com.holonplatform.jaxrs.server.internal.auth.AuthenticationDynamicFeature;

/**
 * A JAX-RS {@link Feature} which can be registered in server application to enable authentication support using
 * {@link Authenticate} annotation.
 * <p>
 * JAX-RS resource classes and/or methods annotated with {@link Authenticate} will be protected from unauthorized
 * access, performing client authentication using the {@link AuthContext} security context {@link Realm}.
 * </p>
 * <p>
 * Using this feature, the {@link SecurityContext} of the JAX-RS requests bound to {@link Authenticate} annotated
 * resources will be replaced with an {@link AuthContext} compatible implementation and will be used to perform
 * authentication when required.
 * </p>
 * <p>
 * Allowed authentication schemes can be specified using {@link Authenticate#schemes()} annotation attribute. If any
 * scheme is specified, a scheme-matching {@link AuthenticationTokenResolver} must be registered in {@link Realm} to
 * perform authentication with given scheme.
 * </p>
 * <p>
 * The {@link Realm} to use is obtained either from a registered {@link ContextResolver} of {@link Realm} type, if
 * available, or as a {@link com.holonplatform.core.Context} resource using {@link Realm#getCurrent()}.
 * </p>
 * <p>
 * When authentication informations provided by a client according to allowed authentication schemes (if any) for an
 * {@link Authenticate} resource are missing or invalid, a <code>401 - Unauthorized</code> status response is returned,
 * including a {@link HttpHeaders#WWW_AUTHENTICATE} header for each allowed authentication scheme, if any.
 * </p>
 *
 * @since 5.0.0
 */
public class AuthenticationFeature implements Feature {

	private final static Logger LOGGER = JaxrsLogger.create();

	/**
	 * JAX-RS configuration property name to put in the application configuration to disable the
	 * {@link AuthenticationFeature} registration.
	 */
	public static final String DISABLE_AUTHENTICATION = "holon.jaxrs.server.disable-authentication";

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.Feature#configure(javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public boolean configure(FeatureContext context) {
		// limit to SERVER runtime
		if (RuntimeType.SERVER == context.getConfiguration().getRuntimeType()) {

			// check disabled
			if (context.getConfiguration().getProperties().containsKey(DISABLE_AUTHENTICATION)) {
				LOGGER.debug(() -> "Skip AuthenticationFeature registration, [" + DISABLE_AUTHENTICATION
						+ "] property detected");
				return false;
			}

			if (!context.getConfiguration().isRegistered(AuthenticationDynamicFeature.class)) {
				context.register(AuthenticationDynamicFeature.class);
			}
			return true;
		}
		return false;
	}

}
