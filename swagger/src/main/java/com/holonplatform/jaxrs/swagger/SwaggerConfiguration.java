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
package com.holonplatform.jaxrs.swagger;

import java.util.Set;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * Swagger {@link BeanConfig} extension to ensure {@link SwaggerContextListener} registration.
 * 
 * @since 5.0.0
 */
public class SwaggerConfiguration extends BeanConfig {

	/*
	 * (non-Javadoc)
	 * @see io.swagger.jaxrs.config.BeanConfig#classes()
	 */
	@Override
	public Set<Class<?>> classes() {
		Set<Class<?>> classes = super.classes();
		classes.add(SwaggerContextListener.class);
		return classes;
	}

}
