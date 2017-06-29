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
package com.holonplatform.jaxrs.media;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.jaxrs.internal.JaxrsLogger;
import com.holonplatform.jaxrs.internal.media.PropertyBoxFormDataProvider;

/**
 * JAX-RS feature to register a {@link Provider} to handle {@link PropertyBox} data type using
 * <code>application/x-www-form-urlencoded</code> media type.
 * 
 * @since 5.0.0
 */
public class FormDataPropertyBoxFeature implements Feature {

	private final static Logger LOGGER = JaxrsLogger.create();

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.Feature#configure(javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public boolean configure(FeatureContext context) {
		if (!context.getConfiguration().isRegistered(PropertyBoxFormDataProvider.class)) {
			LOGGER.debug(() -> "<Runtime: " + context.getConfiguration().getRuntimeType() + "> Registering provider ["
					+ PropertyBoxFormDataProvider.class.getName() + "]");
			context.register(PropertyBoxFormDataProvider.class);
		}
		return false;
	}

}
