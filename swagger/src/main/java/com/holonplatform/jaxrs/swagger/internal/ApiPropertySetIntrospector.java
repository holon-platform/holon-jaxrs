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
package com.holonplatform.jaxrs.swagger.internal;

import java.io.Serializable;

import com.holonplatform.core.Context;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.jaxrs.swagger.annotations.PropertySetRef;

/**
 * Introspector to obtain a {@link PropertySet} from {@link PropertySetRef} annotation.
 * 
 * @since 5.0.0
 */
public interface ApiPropertySetIntrospector extends Serializable {

	/**
	 * Default {@link Context} resource key
	 */
	static final String CONTEXT_KEY = ApiPropertySetIntrospector.class.getName();

	/**
	 * Obtain the {@link PropertySet} declared through given <code>annotation</code>.
	 * @param annotation {@link PropertySetRef} annotation from which to obtain the {@link PropertySet}
	 * @return PropertySet which corresponds to given annotation attributes
	 * @throws ApiPropertySetIntrospectionException In an error occurred
	 */
	PropertySet<?> getPropertySet(PropertySetRef annotation) throws ApiPropertySetIntrospectionException;

	// Accessors

	/**
	 * Gets the current {@link ApiPropertySetIntrospector} instance.
	 * @return The {@link Context}-bound ApiPropertySetIntrospector instance, if available using {@link #CONTEXT_KEY} as
	 *         context key, or the default instance for the default ClassLoader obtained through {@link #getDefault()}.
	 */
	static ApiPropertySetIntrospector get() {
		return Context.get().resource(CONTEXT_KEY, ApiPropertySetIntrospector.class).orElse(getDefault());
	}

	/**
	 * Return the default {@link ApiPropertySetIntrospector}.
	 * @return Default ApiPropertySetIntrospector
	 */
	static ApiPropertySetIntrospector getDefault() {
		return DefaultApiPropertySetIntrospector.INSTANCE;
	}

	/**
	 * Exception thrown for PropertySet introspection errors.
	 */
	@SuppressWarnings("serial")
	public class ApiPropertySetIntrospectionException extends RuntimeException {

		/**
		 * Constructor with error message
		 * @param message Error message
		 */
		public ApiPropertySetIntrospectionException(String message) {
			super(message);
		}

		/**
		 * Constructor with nested exception
		 * @param cause Nested exception
		 */
		public ApiPropertySetIntrospectionException(Throwable cause) {
			super(cause);
		}

		/**
		 * Constructor with error message and nested exception
		 * @param message Error message
		 * @param cause Nested exception
		 */
		public ApiPropertySetIntrospectionException(String message, Throwable cause) {
			super(message, cause);
		}

	}

}
