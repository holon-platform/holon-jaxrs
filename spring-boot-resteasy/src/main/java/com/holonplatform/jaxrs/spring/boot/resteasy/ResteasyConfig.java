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
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

/**
 * Resteasy JAX-RS {@link Application} configuration.
 * <p>
 * This class is used by the {@link ResteasyAutoConfiguration} class to auto configure Resteasy.
 * </p>
 * 
 * @since 5.0.0
 */
public class ResteasyConfig extends Application {

	private final transient Set<Class<?>> classes = new HashSet<>();
	private final transient Set<Object> singletons = new HashSet<>();
	private final transient Map<String, Object> properties = new HashMap<>();

	/**
	 * Register a class of a JAX-RS component (such as {@link Path} resource or a {@link Provider}).
	 * <p>
	 * {@link Path} resources registered this way will be treated by default as per-request resources.
	 * </p>
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
	 * Register a class of a JAX-RS component (such as {@link Path} resource or a {@link Provider}).
	 * <p>
	 * {@link Path} resources registered this way will be treated by default as singleton resources.
	 * </p>
	 * @param component JAX-RS component to be registered
	 * @return the updated configuration
	 */
	public ResteasyConfig register(Object component) {
		if (component != null) {
			singletons.add(component);
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
	 * Checks whether given component class is registered in this configuration.
	 * @param componentClass Component class to check
	 * @return <code>true</code> if class is registered in this configuration, <code>false</code> otherwise
	 */
	public boolean isRegistered(Class<?> componentClass) {
		if (componentClass != null) {
			return getClasses().contains(componentClass);
		}
		return false;
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
	 * @see javax.ws.rs.core.Application#getSingletons()
	 */
	@Override
	public Set<Object> getSingletons() {
		return singletons;
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
