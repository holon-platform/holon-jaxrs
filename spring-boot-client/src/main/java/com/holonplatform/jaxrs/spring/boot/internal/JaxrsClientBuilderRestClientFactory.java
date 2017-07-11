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
package com.holonplatform.jaxrs.spring.boot.internal;

import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Context;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.http.exceptions.RestClientCreationException;
import com.holonplatform.http.rest.RestClient;
import com.holonplatform.http.rest.RestClientFactory;
import com.holonplatform.jaxrs.client.JaxrsRestClient;
import com.holonplatform.jaxrs.client.internal.JaxrsClientRestClient;
import com.holonplatform.jaxrs.spring.boot.JaxrsClientBuilder;
import com.holonplatform.spring.internal.SpringLogger;

/**
 * A {@link RestClientFactory} to use a {@link JaxrsClientBuilder} to create JAX-RS client instances.
 *
 * @since 5.0.0
 */
@Priority(RestClientFactory.DEFAULT_PRIORITY - 10)
public class JaxrsClientBuilderRestClientFactory implements RestClientFactory {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = SpringLogger.create();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.rest.RestClientFactory#getRestClientImplementationClass()
	 */
	@Override
	public Class<?> getRestClientImplementationClass() {
		return JaxrsRestClient.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.rest.RestClientFactory#create(java.lang.ClassLoader)
	 */
	@Override
	public RestClient create(ClassLoader classLoader) throws RestClientCreationException {
		// Try to obtain a RestTemplate
		Optional<JaxrsClientBuilder> builder = Context.get().resource("restTemplateBuilder", JaxrsClientBuilder.class,
				classLoader);
		if (builder.isPresent()) {
			return new JaxrsClientRestClient(builder.get().build());
		}
		LOGGER.debug(() -> "No JaxrsClientBuilder type Context resource available - RestClient creation skipped");
		return null;
	}

}
