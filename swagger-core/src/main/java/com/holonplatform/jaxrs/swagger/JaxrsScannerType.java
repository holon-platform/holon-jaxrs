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
package com.holonplatform.jaxrs.swagger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

/**
 * OpenAPI JAX-RS scanner types enumeration.
 * 
 * @since 5.2.0
 */
public enum JaxrsScannerType {

	/**
	 * Default scanner.
	 */
	DEFAULT,

	/**
	 * JAX-RS {@link Application} scanner: only the JAX-RS {@link Application} resource classes will be taken into
	 * account for the API definition.
	 * <p>
	 * If an explicit API configuration is provided, the configuration resource classes and/or resource packages will be
	 * used to filter the JAX-RS application classes to include in the API definition.
	 * </p>
	 */
	APPLICATION,

	/**
	 * JAX-RS annotation scanner: the classes annotated with {@link Path} will be included in the API definition.
	 * <p>
	 * Concrete implementations may also include classes with additional annotations.
	 * </p>
	 * <p>
	 * If an explicit API configuration is provided, the configuration resource classes and/or resource packages will be
	 * used to filter the detected classes to include in the API definition.
	 * </p>
	 */
	ANNOTATION,

	/**
	 * JAX-RS {@link Application} and annotation scanner: both the JAX-RS {@link Application} resource classes and the
	 * classes annotated with {@link Path} will be included in the API definition.
	 * <p>
	 * Concrete implementations may also include classes with additional annotations.
	 * </p>
	 * <p>
	 * If an explicit API configuration is provided, the configuration resource classes and/or resource packages will be
	 * used to filter the detected classes to include in the API definition.
	 * </p>
	 */
	APPLICATION_AND_ANNOTATION;

}
