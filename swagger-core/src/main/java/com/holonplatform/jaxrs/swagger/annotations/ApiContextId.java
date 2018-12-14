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

/**
 * Annotation which can be used on API definition resource classes to declare the API context id to which the API
 * definition resource is bound.
 * <p>
 * When an API listing endpoint is configured and bound to an API context id, it will include only the API definitions
 * which corresponds to the API resource classes with a consistent context id {@link #value()}, if the
 * {@link ApiContextId} is present on the API resource class.
 * </p>
 *
 * @since 5.2.0
 */
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiContextId {

	/**
	 * Get the API context id.
	 * @return the API context id
	 */
	String value();

}
