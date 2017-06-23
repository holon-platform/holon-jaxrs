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
package com.holonplatform.jaxrs.swagger.spring;

/**
 * Exception related to Swagger configuration errors.
 * 
 * @since 5.0.0
 */
public class SwaggerConfigurationException extends RuntimeException {

	private static final long serialVersionUID = -8967770894565415208L;

	/**
	 * Constructor with error message
	 * @param message Error message
	 */
	public SwaggerConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructor with nested exception
	 * @param cause Nested exception
	 */
	public SwaggerConfigurationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor with error message and nested exception
	 * @param message Error message
	 * @param cause Nested exception
	 */
	public SwaggerConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

}
