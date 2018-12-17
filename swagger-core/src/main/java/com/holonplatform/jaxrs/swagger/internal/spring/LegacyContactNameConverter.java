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
package com.holonplatform.jaxrs.swagger.internal.spring;

import org.springframework.core.convert.converter.Converter;

import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties.Contact;

/**
 * Converter for the legacy <code>contact</code> configuration property.
 *
 * @since 5.2.0
 */
public class LegacyContactNameConverter implements Converter<String, Contact> {

	@Override
	public Contact convert(String source) {
		if (source != null) {
			final Contact c = new Contact();
			c.setName(source);
			return c;
		}
		return null;
	}

}
