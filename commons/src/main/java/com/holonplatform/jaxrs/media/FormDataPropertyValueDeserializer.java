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
package com.holonplatform.jaxrs.media;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.Property.PropertyReadException;
import com.holonplatform.jaxrs.internal.media.DefaultFormDataPropertyValueDeserializer;

/**
 * {@link Property} value deserializer from <code>application/x-www-form-urlencoded</code> media type.
 *
 * @since 5.0.0
 */
public interface FormDataPropertyValueDeserializer {

	/**
	 * Deserialize given property <code>value</code> from <code>application/x-www-form-urlencoded</code> media type
	 * String.
	 * @param property Property to deserialize the value for
	 * @param value Property value
	 * @return Serialized value
	 * @throws PropertyReadException If an error occurred
	 */
	Object deserialize(final Property<?> property, final String value) throws PropertyReadException;

	/**
	 * Get the default instance.
	 * @return the default {@link FormDataPropertyValueDeserializer}
	 */
	static FormDataPropertyValueDeserializer getDefault() {
		return DefaultFormDataPropertyValueDeserializer.INSTANCE;
	}

}
