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
package com.holonplatform.jaxrs.spring.boot.resteasy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

/**
 * TODO
 */
public class ResteasyConfig extends Application {

	private final transient Set<Class<?>> classes = new HashSet<>();
	private final transient Map<String, Object> properties = new HashMap<>();
	private final transient List<String> packages = new LinkedList<>();

	/**
	 * Register a class of a JAX-RS component (such as {@link Path} resource or a {@link Provider}).
	 * @param componentClass JAX-RS component class to be registered
	 * @return the updated configuration
	 */
	public ResteasyConfig register(Class<?> componentClass) {
		if (componentClass != null) {
			classes.add(componentClass);
		}
		return this;
	}

	/**
	 * Set the new configuration property, if already set, the existing value of the property will be updated. Setting a
	 * {@code null} value into a property effectively removes the property from the property bag.
	 * @param name Property name
	 * @param value Property value
	 * @return the updated configuration
	 */
	public ResteasyConfig property(String name, Object value) {
		if (name != null) {
			if (value != null) {
				properties.put(name, value);
			} else {
				properties.remove(name);
			}
		}
		return this;
	}

	/**
	 * Adds a set of package names which will be used to scan for components.
	 * @param packages One or more package name
	 * @return Updated configuration instance
	 */
	public final ResteasyConfig packages(final String... packages) {
		if (packages != null) {
			for (String pkg : packages) {
				this.packages.add(pkg);
			}
		}
		return this;
	}

	/**
	 * Get the packages to scan for JAX-RS components.
	 * @return the packages to scan
	 */
	public List<String> getPackages() {
		return packages;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.Application#getClasses()
	 */
	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.Application#getProperties()
	 */
	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

}
