/*
 * Copyright 2016-2018 Axioma srl.
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
package com.holonplatform.jaxrs.swagger.v2.internal.endpoints;

import java.util.Set;

import javax.ws.rs.Path;

import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.ApiDefaults;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpoint;
import com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointBuilder;
import com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointConfiguration;
import com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointDefinition;
import com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration;
import com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContext;
import com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContextBuilder;

import io.swagger.config.SwaggerConfig;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

/**
 * Default {@link ApiEndpointBuilder} implementation.
 *
 * @since 5.2.0
 */
public enum SwaggerEndpointBuilder implements ApiEndpointBuilder<SwaggerConfig> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointBuilder#build(com.holonplatform.jaxrs.swagger.
	 * internal.endpoints.ApiEndpointConfiguration)
	 */
	@Override
	public ApiEndpointDefinition build(ApiEndpointConfiguration<? extends SwaggerConfig> configuration)
			throws ApiConfigurationException {

		ObjectUtils.argumentNotNull(configuration, "ApiEndpointConfiguration must be not null");

		// type
		final ApiEndpointType type = configuration.getType().orElse(ApiEndpointType.getDefault());
		final Class<?> endpointClass;
		switch (type) {
		case ACCEPT_HEADER:
			endpointClass = AcceptHeaderSwaggerEndpoint.class;
			break;
		case PATH_PARAMETER:
			endpointClass = PathParamSwaggerEndpoint.class;
			break;
		case QUERY_PARAMETER:
		default:
			endpointClass = QueryParamSwaggerEndpoint.class;
			break;
		}

		// scanner type
		final JaxrsScannerType scannerType = configuration.getScannerType().orElse(JaxrsScannerType.DEFAULT);

		// context id
		final String contextId = configuration.getContextId().orElse(ApiDefaults.DEFAULT_CONTEXT_ID);

		// path
		final String path;
		if (ApiEndpointType.PATH_PARAMETER == type) {
			path = configuration.getPath().orElse(ApiDefaults.DEFAULT_API_ENDPOINT_PATH) + ".{type:json|yaml}";
		} else {
			path = configuration.getPath().orElse(ApiDefaults.DEFAULT_API_ENDPOINT_PATH);
		}

		// build endpoint class
		final DynamicType.Builder<?> builder = new ByteBuddy().subclass(endpointClass)
				.annotateType(AnnotationDescription.Builder.ofType(Path.class).define("value", path).build())
				.annotateType(AnnotationDescription.Builder.ofType(ApiEndpoint.class).define("value", contextId)
						.define("configLocation", configuration.getConfigurationLocation().orElse(""))
						.define("scannerType", scannerType).build());
		final Class<?> endpoint = builder.make()
				.load(configuration.getClassLoader().orElseGet(() -> ClassUtils.getDefaultClassLoader()),
						ClassLoadingStrategy.Default.INJECTION)
				.getLoaded();

		// build context
		final JaxrsSwaggerApiContextBuilder contextBuilder = JaxrsSwaggerApiContextBuilder.create()
				.contextId(contextId);
		// configuration
		configuration.getConfiguration().ifPresent(c -> contextBuilder.configuration(c));
		// scanner
		contextBuilder.scannerType(scannerType);
		// JAX-RS Application
		configuration.getApplication().ifPresent(a -> {
			contextBuilder.application(a);
		});
		// check root package
		if (scannerType == JaxrsScannerType.ANNOTATION || scannerType == JaxrsScannerType.APPLICATION_AND_ANNOTATION) {
			Set<String> pkgs = configuration.getRootResourcePackages();
			if (!pkgs.isEmpty()) {
				SwaggerConfiguration sc = configuration.getConfiguration()
						.filter(c -> c instanceof SwaggerConfiguration).map(c -> (SwaggerConfiguration) c).orElse(null);
				if (sc != null && (sc.getResourcePackages() == null || sc.getResourcePackages().isEmpty())) {
					sc.setResourcePackages(pkgs);
				} else {
					contextBuilder.resourcePackages(pkgs);
				}
			}
		}
		final JaxrsSwaggerApiContext context = contextBuilder.build(false);

		return ApiEndpointDefinition.create(endpoint, type, scannerType, path, contextId, () -> {
			context.init();
			return null;
		});
	}

}
