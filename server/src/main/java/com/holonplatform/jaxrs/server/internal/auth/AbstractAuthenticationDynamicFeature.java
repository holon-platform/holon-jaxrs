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

import java.lang.reflect.AnnotatedElement;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.jaxrs.internal.JaxrsLogger;

/**
 * Base {@link DynamicFeature} to process {@link Authenticate} annotated JAX-RS resource classes and methods.
 *
 * @since 5.1.0
 */
public abstract class AbstractAuthenticationDynamicFeature implements DynamicFeature {

	private final static Logger LOGGER = JaxrsLogger.create();

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.container.DynamicFeature#configure(javax.ws.rs.container.ResourceInfo,
	 * javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		// check method
		if (!registerAuthenticationFilters(context, resourceInfo.getResourceMethod())) {
			// check class
			if (registerAuthenticationFilters(context, resourceInfo.getResourceClass())) {
				LOGGER.debug(() -> "Authentication Feature registered for class ["
						+ resourceInfo.getResourceClass().getName() + "]");
			}
		} else {
			LOGGER.debug(
					() -> "Authentication Feature registered for method [" + resourceInfo.getResourceMethod().getName()
							+ "] of class [" + resourceInfo.getResourceClass().getName() + "]");
		}
	}

	/**
	 * Checks if given <code>element</code> has the {@link Authenticate} annotation and if so registers the required
	 * authentication filters.
	 * @param context Feature context
	 * @param element Annotated element
	 * @return <code>true</code> if the {@link Authenticate} annotation was found and the authentication filters were
	 *         registered, <code>false</code> otherwise
	 */
	private boolean registerAuthenticationFilters(FeatureContext context, AnnotatedElement element) {
		if (element.isAnnotationPresent(Authenticate.class)) {
			return processElement(context, element, element.getAnnotation(Authenticate.class));
		}
		return false;
	}

	/**
	 * Process the element annotated with {@link Authenticate}.
	 * @param context Feature context
	 * @param element Annotated element
	 * @param authenticate {@link Authenticate} annotation
	 * @return <code>true</code> if a filter was registered, <code>false</code> otherwise
	 */
	protected abstract boolean processElement(FeatureContext context, AnnotatedElement element,
			Authenticate authenticate);

}
