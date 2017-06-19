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
package com.holonplatform.jaxrs.spring.boot.jersey.internal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.jaxrs.server.internal.JaxrsServerLogger;
import com.holonplatform.spring.internal.BeanRegistryUtils;

/**
 * A {@link BeanFactoryPostProcessor} to detect valid {@link Path} and {@link Provider} annotated bean classes and
 * register them in Jersey server configuration as JAX-RS resources.
 *
 * @since 5.0.0
 */
public class JerseyResourcesPostProcessor
		implements BeanFactoryPostProcessor, ResourceConfigCustomizer, BeanClassLoaderAware {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = JaxrsServerLogger.create();

	/**
	 * Bean class laoder
	 */
	private ClassLoader classLoader;

	/**
	 * Detected resources
	 */
	private List<WeakReference<Class<?>>> resources;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang.ClassLoader)
	 */
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.
	 * beans.factory.config.ConfigurableListableBeanFactory)
	 */
	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		LOGGER.debug(() -> "Lookup @Path and @Provider JAX-RS resource in bean factory [" + beanFactory + "]");

		resources = new ArrayList<>();

		for (String name : beanFactory.getBeanDefinitionNames()) {
			try {
				BeanDefinition definition = beanFactory.getBeanDefinition(name);
				if (!definition.isAbstract()) {
					Class<?> beanClass = BeanRegistryUtils.getBeanClass(name, definition, beanFactory, classLoader);
					if (beanClass != null) {
						if (isJaxrsResourceClass(definition, beanClass)) {
							resources.add(new WeakReference<>(beanClass));
							LOGGER.debug(() -> "Found JAX-RS resource class: [" + beanClass.getName() + "]");
						}
					}
				}
			} catch (@SuppressWarnings("unused") NoSuchBeanDefinitionException e) {
				// ignore
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer#customize(org.glassfish.jersey.server.
	 * ResourceConfig)
	 */
	@Override
	public void customize(ResourceConfig config) {
		if (resources != null) {
			for (WeakReference<Class<?>> resource : resources) {
				final Class<?> resourceClass = resource.get();
				if (resourceClass != null) {
					config.register(resourceClass);
					LOGGER.debug(() -> "Registered JAX-RS resource class: [" + resourceClass.getName() + "]");
				}
			}
		}
	}

	/**
	 * Check whether given bean definition is a valid {@link Provider} or {@link Path} resource.
	 * @param definition Bean definition
	 * @param beanClass Bean class
	 * @return <code>true</code> if it is a valid JAX-RS resource class
	 */
	private static boolean isJaxrsResourceClass(BeanDefinition definition, Class<?> beanClass) {
		// check Provider
		if (beanClass.isAnnotationPresent(Provider.class)) {
			if (!definition.isSingleton()) {
				throw new BeanDefinitionValidationException("Invalid JAX-RS @Provider bean definition for bean class ["
						+ beanClass + "]: JAX-RS providers must be singleton beans");
			}
			return true;
		}
		// check Path resource
		Class<?> pathClass = AnnotationUtils.getClassWithAnnotation(beanClass, Path.class);
		if (pathClass != null) {
			return true;
		}
		return false;
	}

}
