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
package com.holonplatform.jaxrs.swagger.v3;

import java.util.Optional;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import com.holonplatform.jaxrs.swagger.v3.internal.scanner.JaxrsApplicationResourcesScanner;

import io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner;
import io.swagger.v3.jaxrs2.integration.JaxrsApplicationAndAnnotationScanner;
import io.swagger.v3.jaxrs2.integration.api.JaxrsOpenApiScanner;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;

/**
 * OpenAPI JAX-RS scanner types enumeration.
 * 
 * @since 5.2.0
 */
public enum JaxrsScannerType {

	/**
	 * Default OpenAPI JAX-RS scanner.
	 */
	DEFAULT(null),

	/**
	 * JAX-RS {@link Application} scanner: only the JAX-RS {@link Application} resource classes will be taken into
	 * account for the API definition.
	 * <p>
	 * If an {@link OpenAPIConfiguration} is provided, the configuration resource classes and/or resource packages will
	 * be used to filter the JAX-RS application classes to include in the API definition.
	 * </p>
	 */
	APPLICATION(JaxrsApplicationResourcesScanner.class),

	/**
	 * JAX-RS annotation scanner: the classes annotated with {@link Path} and/or {@link OpenAPIDefinition} will included
	 * in the API definition.
	 * <p>
	 * If an {@link OpenAPIConfiguration} is provided, the configuration resource classes and/or resource packages will
	 * be used to filter the detected classes to include in the API definition.
	 * </p>
	 */
	ANNOTATION(JaxrsAnnotationScanner.class),

	/**
	 * JAX-RS {@link Application} and annotation scanner: both the JAX-RS {@link Application} resource classes and the
	 * classes annotated with {@link Path} and/or {@link OpenAPIDefinition} will included in the API definition.
	 * <p>
	 * If an {@link OpenAPIConfiguration} is provided, the configuration resource classes and/or resource packages will
	 * be used to filter the detected classes to include in the API definition.
	 * </p>
	 */
	APPLICATION_AND_ANNOTATION(JaxrsApplicationAndAnnotationScanner.class);

	private final Class<? extends JaxrsOpenApiScanner> scannerClass;

	private JaxrsScannerType(Class<? extends JaxrsOpenApiScanner> scannerClass) {
		this.scannerClass = scannerClass;
	}

	/**
	 * Get the scanner class, if available.
	 * @return Optional scanner class
	 */
	public Optional<Class<? extends JaxrsOpenApiScanner>> getScannerClass() {
		return Optional.ofNullable(scannerClass);
	}

}
