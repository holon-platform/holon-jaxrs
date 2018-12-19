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
package com.holonplatform.jaxrs.swagger.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.holonplatform.jaxrs.swagger.ApiDefaults;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;

/**
 * Annotation which can be used on a API configuration class to setup the API listing endpoint.
 *
 * @since 5.2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiConfiguration {

	/**
	 * Get the API context id.
	 * @return the API context id
	 */
	String contextId() default ApiDefaults.DEFAULT_CONTEXT_ID;

	/**
	 * Get the API listing endpoint path.
	 * <p>
	 * Default is {@link ApiDefaults#DEFAULT_API_ENDPOINT_PATH}.
	 * </p>
	 * @return the API listing endpoint path
	 */
	String path() default ApiDefaults.DEFAULT_API_ENDPOINT_PATH;

	/**
	 * Get the API listing endpoint type.
	 * <p>
	 * Default is {@link ApiEndpointType#QUERY_PARAMETER}.
	 * </p>
	 * @return the API listing endpoint type
	 */
	ApiEndpointType endpointType() default ApiEndpointType.QUERY_PARAMETER;

	/**
	 * Get the scanner type to use to detect API resource classes.
	 * <p>
	 * Default is {@link JaxrsScannerType#DEFAULT}.
	 * </p>
	 * @return the API resource classes scanner type
	 */
	JaxrsScannerType scannerType() default JaxrsScannerType.DEFAULT;

}
