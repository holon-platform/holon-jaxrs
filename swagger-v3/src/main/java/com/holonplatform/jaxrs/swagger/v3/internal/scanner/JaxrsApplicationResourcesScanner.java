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
package com.holonplatform.jaxrs.swagger.v3.internal.scanner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.Application;

import org.apache.commons.lang3.StringUtils;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.v3.internal.endpoints.AbstractOpenApiEndpoint;

import io.swagger.v3.jaxrs2.integration.api.JaxrsOpenApiScanner;
import io.swagger.v3.oas.integration.IgnoredPackages;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;

/**
 * A {@link JaxrsOpenApiScanner} to take into account only the JAX-RS application resources classes.
 * <p>
 * If a {@link OpenAPIConfiguration} is provided, the configuration resource classes and/or resource packages will be
 * used to filter the JAX-RS application classes to provide.
 * </p>
 * @since 5.2.0
 */
public class JaxrsApplicationResourcesScanner implements JaxrsOpenApiScanner {

	private static final Logger LOGGER = SwaggerLogger.create();

	private static final Set<String> ignored = new HashSet<>();

	static {
		ignored.addAll(IgnoredPackages.ignored);
		ignored.add(AbstractOpenApiEndpoint.class.getPackage().getName());
	}

	private OpenAPIConfiguration configuration;
	private Application application;

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiScanner#setConfiguration(io.swagger.v3.oas.integration.api.
	 * OpenAPIConfiguration)
	 */
	@Override
	public void setConfiguration(OpenAPIConfiguration configuration) {
		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.jaxrs2.integration.api.JaxrsOpenApiScanner#setApplication(jakarta.ws.rs.core.Application)
	 */
	@Override
	public void setApplication(Application application) {
		this.application = application;
	}

	/**
	 * Get the OpenAPI configuration, if available.
	 * @return Optional OpenAPI configuration
	 */
	protected Optional<OpenAPIConfiguration> getConfiguration() {
		return Optional.ofNullable(configuration);
	}

	/**
	 * Get the JAX-RS {@link Application}, if available.
	 * @return Optional JAX-RS {@link Application}
	 */
	protected Optional<Application> getApplication() {
		return Optional.ofNullable(application);
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.oas.integration.api.OpenApiScanner#classes()
	 */
	@Override
	public Set<Class<?>> classes() {
		final Set<Class<?>> applicationClasses = getApplicationClasses();
		// check configuration resource classes
		final Set<Class<?>> configurationClasses = getConfiguration().map(cfg -> cfg.getResourceClasses())
				.filter(cs -> cs != null).map(cs -> {
					Set<Class<?>> classes = new HashSet<>();
					for (String className : cs) {
						if (!isIgnored(className)) {
							try {
								classes.add(Class.forName(className));
							} catch (Exception e) {
								LOGGER.warn("Failed to load OpenAPI configuration resource class [" + className + "]",
										e);
							}
						}
					}
					return classes;
				}).orElse(Collections.emptySet());
		// check configuration resource packages
		Set<String> packageNames = getConfiguration().map(cfg -> cfg.getResourcePackages()).filter(cs -> cs != null)
				.orElse(Collections.emptySet()).stream().filter(pn -> pn != null && !pn.trim().equals(""))
				.collect(Collectors.toSet());
		// validate application classes
		if (!configurationClasses.isEmpty()) {
			applicationClasses.removeIf(cls -> !configurationClasses.contains(cls));
		} else if (!packageNames.isEmpty()) {
			applicationClasses.removeIf(cls -> !isValidPackageName(cls, packageNames));
		}
		return applicationClasses;
	}

	private static boolean isValidPackageName(Class<?> cls, Set<String> packageNames) {
		for (String packageName : packageNames) {
			if (cls.getPackage().getName().startsWith(packageName)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.v3.jaxrs2.integration.api.JaxrsOpenApiScanner#resources()
	 */
	@Override
	public Map<String, Object> resources() {
		return Collections.emptyMap();
	}

	/**
	 * Get the JAX-RS {@link Application} resource classes.
	 * @return The resource classes
	 */
	protected Set<Class<?>> getApplicationClasses() {
		final Set<Class<?>> output = new HashSet<>();
		getApplication().ifPresent(application -> {
			Set<Class<?>> clzs = application.getClasses();
			if (clzs != null) {
				for (Class<?> clz : clzs) {
					if (!isIgnored(clz.getName())) {
						output.add(clz);
					}
				}
			}
			Set<Object> singletons = application.getSingletons();
			if (singletons != null) {
				for (Object o : singletons) {
					if (!isIgnored(o.getClass().getName())) {
						output.add(o.getClass());
					}
				}
			}
		});
		return output;
	}

	/**
	 * Check if given class or package name must be ignored.
	 * @param classOrPackageName Class or package name
	 * @return <code>true</code> if given class or package name must be ignored, <code>false</code> otherwise
	 */
	protected boolean isIgnored(String classOrPackageName) {
		if (StringUtils.isBlank(classOrPackageName)) {
			return true;
		}
		return ignored.stream().anyMatch(i -> classOrPackageName.startsWith(i));
	}

}
