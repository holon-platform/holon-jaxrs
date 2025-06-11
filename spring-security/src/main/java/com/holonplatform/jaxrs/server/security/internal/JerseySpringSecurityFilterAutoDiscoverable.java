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
package com.holonplatform.jaxrs.server.security.internal;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.FeatureContext;

import org.glassfish.jersey.internal.spi.AutoDiscoverable;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.jaxrs.internal.JaxrsLogger;
import com.holonplatform.jaxrs.server.security.SpringSecurityAuthenticationFeature;

/**
 * Jersey {@link AutoDiscoverable} class to register the {@link SpringSecurityAuthContextFilter}.
 *
 * @since 5.1.0
 */
@Priority(AutoDiscoverable.DEFAULT_PRIORITY - 10)
public class JerseySpringSecurityFilterAutoDiscoverable implements AutoDiscoverable {

	private static final Logger LOGGER = JaxrsLogger.create();

	/*
	 * (non-Javadoc)
	 * @see org.glassfish.jersey.internal.spi.AutoDiscoverable#configure(jakarta.ws.rs.core.FeatureContext)
	 */
	@Override
	public void configure(FeatureContext context) {
		if (!context.getConfiguration().isRegistered(SpringSecurityAuthenticationFeature.class)) {
			context.register(SpringSecurityAuthenticationFeature.class);

			LOGGER.debug(() -> SpringSecurityAuthenticationFeature.class.getName() + " registered");
		}
	}

}
