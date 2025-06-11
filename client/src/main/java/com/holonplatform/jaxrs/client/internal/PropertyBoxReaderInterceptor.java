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
package com.holonplatform.jaxrs.client.internal;

import java.io.IOException;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

import com.holonplatform.core.Context;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;

/**
 * JAX-RS {@link ReaderInterceptor} to handle client REST requests which involve {@link PropertyBox} unmarshalling.
 * 
 * <p>
 * Every interceptor instance must know the {@link PropertySet} to use to deserialize PropertyBox instances, so the
 * property set is given at construction time.
 * </p>
 * 
 * @since 5.0.0
 */
public class PropertyBoxReaderInterceptor implements ReaderInterceptor {

	/**
	 * PropertySet
	 */
	private final PropertySet<?> propertySet;

	/**
	 * Constructor
	 * @param propertySet PropertySet to use to deserialize PropertyContext instances
	 */
	public PropertyBoxReaderInterceptor(PropertySet<?> propertySet) {
		super();
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		this.propertySet = propertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.ext.ReaderInterceptor#aroundReadFrom(jakarta.ws.rs.ext.ReaderInterceptorContext)
	 */
	@Override
	public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
		return Context.get().executeThreadBound(PropertySet.CONTEXT_KEY, propertySet, () -> context.proceed());
	}

}
