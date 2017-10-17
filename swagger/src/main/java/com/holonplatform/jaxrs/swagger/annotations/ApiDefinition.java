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
package com.holonplatform.jaxrs.swagger.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which can be used for Swagger API listing auto-configuration to specify the Swagger API definition
 * properties for a package or a JAX-RS endpoint class.
 * 
 * @since 5.0.0
 */
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiDefinition {

	/**
	 * Get the path of the JAX-RS Swagger API listing endpoint.
	 * @return Swagger API listing endpoint path
	 */
	String docsPath() default "";

	/**
	 * API title
	 * @return the title
	 */
	String title() default "";

	/**
	 * API version description
	 * @return the version
	 */
	String version() default "";

	/**
	 * API description
	 * @return the description
	 */
	String description() default "";

	/**
	 * API schemes
	 * @return the schemes
	 */
	String[] schemes() default {};

	/**
	 * API terms of service URL
	 * @return the terms of service URL
	 */
	String termsOfServiceUrl() default "";

	/**
	 * API contact informations
	 * @return the contact informations
	 */
	String contact() default "";

	/**
	 * API license
	 * @return the license
	 */
	String license() default "";

	/**
	 * API license URL
	 * @return the license URL
	 */
	String licenseUrl() default "";

	/**
	 * Whether to <em>pretty</em> format the Swagger output
	 * @return <code>true</code> to <em>pretty</em> format the Swagger output
	 */
	boolean prettyPrint() default false;

}
