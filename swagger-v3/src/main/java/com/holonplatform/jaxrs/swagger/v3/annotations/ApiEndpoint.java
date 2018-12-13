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
package com.holonplatform.jaxrs.swagger.v3.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.holonplatform.jaxrs.swagger.ApiContext;
import com.holonplatform.jaxrs.swagger.v3.JaxrsScannerType;

import io.swagger.v3.jaxrs2.integration.api.JaxrsOpenApiScanner;

/**
 * Annotation which can be used on OpenAPI listing endpoints to declare a context id and to setup the OpenAPI context.
 * 
 * @since 5.2.0
 */
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiEndpoint {

	/**
	 * Get the API context id.
	 * <p>
	 * A context id identifies an API subset, each context id can be bound to a different configuration and/or a
	 * different API resources set.
	 * </p>
	 * @return the API context id, {@link ApiContext#DEFAULT_CONTEXT_ID} by default
	 */
	String value() default ApiContext.DEFAULT_CONTEXT_ID;

	/**
	 * Get the location to use to locate the API configuration file.
	 * @return The API configuration file location
	 */
	String configLocation() default "";

	/**
	 * Get the {@link JaxrsOpenApiScanner} scanner type to use to configure the API context for this endpoint.
	 * @return the {@link JaxrsOpenApiScanner} scanner type
	 */
	JaxrsScannerType scannerType() default JaxrsScannerType.DEFAULT;

}