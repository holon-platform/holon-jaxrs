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

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;

/**
 * Jersey implementation of the {@link SwaggerApiListingPostProcessor}.
 * 
 * @since 5.0.0
 */
public class JerseyApiListingPostProcessor extends SwaggerApiListingPostProcessor implements ResourceConfigCustomizer {

	@Value("${spring.jersey.application-path:/}")
	private String apiPath;
	
	/* (non-Javadoc)
	 * @see org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer#customize(org.glassfish.jersey.server.ResourceConfig)
	 */
	@Override
	public void customize(ResourceConfig config) {
		if (definitions != null && !definitions.isEmpty()) {
			for (ApiListingDefinition definition : definitions) {
				definition.configureEndpoints(classLoader, apiPath).forEach(e -> {
					config.register(e.getResourceClass());
					LOGGER.info("[Jersey] [" + e.getGroupId() + "] Swagger API listing configured - Path: "
							+ SwaggerJaxrsUtils.composePath(apiPath, e.getPath()));
				});
			}
		}
	}

}
