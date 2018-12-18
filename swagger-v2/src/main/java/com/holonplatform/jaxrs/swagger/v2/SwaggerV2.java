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
package com.holonplatform.jaxrs.swagger.v2;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.jaxrs.swagger.ApiReader;
import com.holonplatform.jaxrs.swagger.v2.internal.DefaultApiReader;
import com.holonplatform.jaxrs.swagger.v2.internal.context.SwaggerContextListener;

import io.swagger.config.SwaggerConfig;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Swagger;

/**
 * Entrypoint interface for Swagger V2 readers and adapters.
 * 
 * @since 5.2.0
 */
public interface SwaggerV2 {

	/**
	 * A {@link ReaderListener} class to include in the API classes to enable a consistent API definition generation
	 * when the {@link PropertyBox} type is used.
	 */
	public static Class<? extends ReaderListener> CONTEXT_READER_LISTENER = SwaggerContextListener.class;

	/**
	 * Create a new {@link ApiReader} to create an {@link Swagger} definition from a set of API resource classes.
	 * @param configuration The configuration to use
	 * @return The {@link ApiReader} instance
	 */
	static ApiReader<Swagger> reader(SwaggerConfig configuration) {
		return new DefaultApiReader(configuration);
	}

}
