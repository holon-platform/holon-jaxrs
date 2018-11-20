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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.holonplatform.core.internal.utils.ObjectUtils;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * Swagger {@link BeanConfig} extension to ensure {@link SwaggerContextListener} registration.
 * 
 * @since 5.0.0
 */
public class SwaggerConfiguration extends BeanConfig {

	private Set<Class<?>> classesToScan;

	/**
	 * Constructor using all the available classes for scanning.
	 */
	public SwaggerConfiguration() {
		this(Collections.emptySet());
	}

	/**
	 * Constructor specifiyng the resource package to be used to detect the classes to scan.
	 * @param resourcePackage the resource package to be used to detect the classes to scan (not null)
	 */
	public SwaggerConfiguration(String resourcePackage) {
		this(Collections.emptySet());
		ObjectUtils.argumentNotNull(resourcePackage, "Resource package must be not null");
		setResourcePackage(resourcePackage);
	}

	/**
	 * Constructor.
	 * @param classToScan Explicit class to scan (not null)
	 */
	public SwaggerConfiguration(Class<?> classToScan) {
		this(Collections.singleton(classToScan));
		ObjectUtils.argumentNotNull(classToScan, "Class to scan must be not null");
	}

	/**
	 * Constructor.
	 * @param classesToScan Explicit classes to scan
	 */
	public SwaggerConfiguration(Set<Class<?>> classesToScan) {
		super();
		ObjectUtils.argumentNotNull(classesToScan, "Classes to scan must be not null");
		this.classesToScan = classesToScan;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.jaxrs.config.BeanConfig#classes()
	 */
	@Override
	public Set<Class<?>> classes() {
		final Set<Class<?>> classes = !classesToScan.isEmpty() ? new HashSet<>(classesToScan) : super.classes();
		classes.add(SwaggerContextListener.class);
		return classes;
	}

}
