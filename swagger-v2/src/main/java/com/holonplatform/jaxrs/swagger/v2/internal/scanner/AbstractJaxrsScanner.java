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
package com.holonplatform.jaxrs.swagger.v2.internal.scanner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Application;

import org.apache.commons.lang3.StringUtils;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration;

import io.swagger.jaxrs.config.AbstractScanner;
import io.swagger.jaxrs.config.JaxrsScanner;

public abstract class AbstractJaxrsScanner extends AbstractScanner implements JaxrsScanner {

	private final Supplier<SwaggerConfiguration> configurationSupplier;

	public AbstractJaxrsScanner(Supplier<SwaggerConfiguration> configurationSupplier) {
		super();
		ObjectUtils.argumentNotNull(configurationSupplier, "SwaggerConfiguration supplier must be not null");
		this.configurationSupplier = configurationSupplier;
	}

	/**
	 * Perform scan.
	 * @param application The JAX-RS application
	 * @return Detected API resource classes
	 */
	protected abstract Set<Class<?>> scan(Application application);

	/*
	 * (non-Javadoc)
	 * @see io.swagger.config.Scanner#classes()
	 */
	@Override
	public Set<Class<?>> classes() {
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see io.swagger.jaxrs.config.JaxrsScanner#classesFromContext(javax.ws.rs.core.Application,
	 * javax.servlet.ServletConfig)
	 */
	@Override
	public Set<Class<?>> classesFromContext(Application app, ServletConfig sc) {
		Set<Class<?>> scanned = scan(app);
		Set<Class<?>> classes = (scanned != null) ? new HashSet<>(scanned) : new HashSet<>();
		// check configuration resource packages
		Set<String> packageNames = getConfiguration().map(cfg -> cfg.getResourcePackages()).filter(cs -> cs != null)
				.orElse(Collections.emptySet()).stream().filter(pn -> pn != null && !pn.trim().equals(""))
				.collect(Collectors.toSet());
		// validate classes
		if (!packageNames.isEmpty()) {
			classes.removeIf(cls -> !isValidPackageName(cls, packageNames));
		}
		return classes;
	}

	protected boolean isValidPackageName(Class<?> cls, Set<String> packageNames) {
		for (String packageName : packageNames) {
			if (cls.getPackage().getName().startsWith(packageName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the API configuration, if available.
	 * @return Optional API configuration
	 */
	protected Optional<SwaggerConfiguration> getConfiguration() {
		return Optional.ofNullable(configurationSupplier.get());
	}

	/**
	 * Get the JAX-RS {@link Application} resource classes.
	 * @param application The JAX-RS application
	 * @return The resource classes
	 */
	protected Set<Class<?>> getApplicationClasses(Application application) {
		final Set<Class<?>> output = new HashSet<>();
		if (application != null) {
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
		}
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
		return IgnoredPackages.ignored.stream().anyMatch(i -> classOrPackageName.startsWith(i));
	}

}
