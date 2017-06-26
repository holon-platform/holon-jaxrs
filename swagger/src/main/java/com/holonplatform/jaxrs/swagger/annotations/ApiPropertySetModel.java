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
package com.holonplatform.jaxrs.swagger.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.holonplatform.core.property.PropertySet;

/**
 * Annotation which can be used in conjuction with {@link PropertySetRef} to declare a Swagger Model name to create
 * using the {@link PropertySet} definition and to append to Swagger model definitions.
 *
 * @since 5.0.0
 */
@Target({ ElementType.PARAMETER, ElementType.TYPE, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiPropertySetModel {

	/**
	 * Provide the model name.
	 * @return The Swagger Model name
	 */
	String value();

	/**
	 * Optional model description.
	 * @return Model description
	 */
	String description() default "";

	/**
	 * Specifies a reference to the corresponding type definition, overrides any other metadata specified.
	 * @return Model reference
	 */
	String reference() default "";

}
