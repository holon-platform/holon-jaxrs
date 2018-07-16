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
package com.holonplatform.jaxrs.swagger.spring.internal;

import org.springframework.beans.factory.annotation.Value;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfig;
import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfigCustomizer;

/**
 * Resteasy implementation of the {@link SwaggerApiListingPostProcessor}.
 * 
 * @since 5.0.0
 */
public class ResteasyApiListingPostProcessor extends SwaggerApiListingPostProcessor
		implements ResteasyConfigCustomizer {

	@Value("${holon.resteasy.application-path:/}")
	private String apiPath;

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfigCustomizer#customize(com.holonplatform.jaxrs.spring.
	 * boot.resteasy.ResteasyConfig)
	 */
	@Override
	public void customize(ResteasyConfig config) {
		if (definitions != null && !definitions.isEmpty()) {
			for (ApiListingDefinition definition : definitions) {
				definition.configureEndpoints(classLoader, apiPath).forEach(e -> {
					config.register(e.getResourceClass());
					LOGGER.info("[Resteasy] [" + e.getGroupId() + "] Swagger API listing configured - Path: "
							+ SwaggerJaxrsUtils.composePath(apiPath, e.getPath()));
				});
			}
		}
	}

}
