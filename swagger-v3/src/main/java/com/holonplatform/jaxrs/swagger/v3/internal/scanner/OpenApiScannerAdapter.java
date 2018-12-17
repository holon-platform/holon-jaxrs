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
package com.holonplatform.jaxrs.swagger.v3.internal.scanner;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.annotations.ApiContextId;
import com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration;

import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiScanner;

/**
 * {@link OpenApiScanner} adapter to filter API resources according to the {@link ApiContextId} annotation.
 *
 * @since 5.2.0
 */
public class OpenApiScannerAdapter implements OpenApiScanner {

	private final OpenApiScanner scanner;
	private final String contextId;

	/**
	 * Constructor.
	 * @param scanner The concrete scanner (not null)
	 * @param contextId API context id
	 */
	public OpenApiScannerAdapter(OpenApiScanner scanner, String contextId) {
		super();
		ObjectUtils.argumentNotNull(scanner, "OpenApiScanner must be not null");
		this.scanner = scanner;
		this.contextId = contextId;
	}

	/**
	 * Get the concrete scanner.
	 * @return the concrete scanner
	 */
	protected OpenApiScanner getScanner() {
		return scanner;
	}

	/**
	 * Get the API context id.
	 * @return the context id
	 */
	protected String getContextId() {
		return contextId;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiReader#setConfiguration(io.swagger.v3.oas.integration.api.
	 * OpenAPIConfiguration)
	 */
	@Override
	public void setConfiguration(OpenAPIConfiguration openApiConfiguration) {
		getScanner().setConfiguration(openApiConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiScanner#classes()
	 */
	@Override
	public Set<Class<?>> classes() {
		Set<Class<?>> classes = getScanner().classes();
		if (classes != null && getContextId() != null) {
			return classes.stream().filter(cls -> cls != null && matches(cls, getContextId()))
					.collect(Collectors.toSet());
		}
		return classes;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiScanner#resources()
	 */
	@Override
	public Map<String, Object> resources() {
		return getScanner().resources();
	}

	/**
	 * Checks whether the given class matches the context id.
	 * @param cls The class
	 * @param contextId The context id
	 * @return <code>true</code> if the class should be included in given context id classes
	 */
	@SuppressWarnings("deprecation")
	private static boolean matches(Class<?> cls, String contextId) {
		// check legacy ApiDefinition
		Optional<String> legacyContextId = AnnotationUtils
				.getAnnotation(cls, com.holonplatform.jaxrs.swagger.annotations.ApiDefinition.class).map(a -> {
					if (!"".equals(a.docsPath())) {
						return AnnotationUtils.getStringValue(a.docsPath());
					}
					return AnnotationUtils.getStringValue(a.value());
				}).flatMap(path -> AbstractJaxrsApiEndpointsAutoConfiguration.getContextIdByPath(cls.getClassLoader(),
						path));
		if (legacyContextId.isPresent()) {
			return legacyContextId.get().equals(contextId);
		}
		// use ApiContextId
		return getResourceContextId(cls).map(ctxId -> ctxId.equals(contextId)).orElse(Boolean.TRUE);
	}

	/**
	 * Get the class context id declaration using the {@link ApiContextId} annotation, if available.
	 * @param cls The class
	 * @return Optional class context id
	 */
	private static Optional<String> getResourceContextId(Class<?> cls) {
		Optional<String> clsContextId = AnnotationUtils.getAnnotation(cls, ApiContextId.class)
				.map(a -> AnnotationUtils.getStringValue(a.value()));
		if (clsContextId.isPresent()) {
			return clsContextId;
		}
		// check package
		Package pkg = cls.getPackage();
		if (pkg != null && pkg.isAnnotationPresent(ApiContextId.class)) {
			return Optional.ofNullable(AnnotationUtils.getStringValue(pkg.getAnnotation(ApiContextId.class).value()));
		}
		return Optional.empty();
	}

}
