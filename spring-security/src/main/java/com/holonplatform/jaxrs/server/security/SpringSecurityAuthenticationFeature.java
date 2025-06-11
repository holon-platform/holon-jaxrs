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
package com.holonplatform.jaxrs.server.security;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.jaxrs.internal.JaxrsLogger;
import com.holonplatform.jaxrs.server.auth.AuthenticationFeature;
import com.holonplatform.jaxrs.server.security.internal.SpringSecurityAuthContextFilter;

/**
 * A JAX-RS {@link Feature} which can be registered in server application to enable authentication support using
 * {@link Authenticate} annotation and using Spring Security context as authentication handler.
 * 
 * @since 5.1.0
 * 
 * @see AuthenticationFeature
 */
public class SpringSecurityAuthenticationFeature implements Feature {

	private final static Logger LOGGER = JaxrsLogger.create();

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.core.Feature#configure(jakarta.ws.rs.core.FeatureContext)
	 */
	@Override
	public boolean configure(FeatureContext context) {
		// limit to SERVER runtime
		if (RuntimeType.SERVER == context.getConfiguration().getRuntimeType()) {

			// check disabled
			if (context.getConfiguration().getProperties().containsKey(AuthenticationFeature.DISABLE_AUTHENTICATION)) {
				LOGGER.debug(() -> "Skip AuthenticationFeature registration, ["
						+ AuthenticationFeature.DISABLE_AUTHENTICATION + "] property detected");
				return false;
			}

			context.register(SpringSecurityAuthContextFilter.class, Priorities.AUTHENTICATION - 15);

			LOGGER.debug(() -> "SpringSecurityAuthContextFilter registered");

			return true;
		}
		return false;
	}

}
