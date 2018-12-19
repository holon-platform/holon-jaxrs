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
package com.holonplatform.jaxrs.swagger.v2.internal.context;

import java.util.HashSet;
import java.util.Set;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.v2.SwaggerReader;
import com.holonplatform.jaxrs.swagger.v2.SwaggerV2;

import io.swagger.models.Swagger;

/**
 * {@link SwaggerReader} adapter to ensure the {@link SwaggerV2#CONTEXT_READER_LISTENER} class inclusion in the classes
 * to read.
 *
 * @since 5.2.0
 */
public class SwaggerReaderAdapter implements SwaggerReader {

	private final SwaggerReader reader;

	/**
	 * Constructor.
	 * @param reader The concrete reader (not null)
	 */
	public SwaggerReaderAdapter(SwaggerReader reader) {
		super();
		ObjectUtils.argumentNotNull(reader, "Reader must be not null");
		this.reader = reader;
	}

	/**
	 * Get the concrete reader.
	 * @return the concrete reader
	 */
	protected SwaggerReader getReader() {
		return reader;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.SwaggerReader#read(java.util.Set)
	 */
	@Override
	public Swagger read(Set<Class<?>> classes) {
		Set<Class<?>> cs = (classes != null) ? new HashSet<>(classes) : new HashSet<>();
		if (!cs.contains(SwaggerV2.CONTEXT_READER_LISTENER)) {
			cs.add(SwaggerV2.CONTEXT_READER_LISTENER);
		}
		return getReader().read(cs);
	}

}
