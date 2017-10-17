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
package com.holonplatform.jaxrs.swagger.spring.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Path;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.jaxrs.internal.JaxrsLogger;
import com.holonplatform.jaxrs.swagger.annotations.ApiDefinition;
import com.holonplatform.jaxrs.swagger.internal.ApiGroupId;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.spring.internal.BeanRegistryUtils;

import io.swagger.annotations.Api;

/**
 * Base {@link BeanFactoryPostProcessor} to auto-detected {@link Api} Swagger endpoint and configure API listing
 * resources.
 * 
 * @since 5.0.0
 */
public abstract class SwaggerApiListingPostProcessor implements BeanFactoryPostProcessor, BeanClassLoaderAware {

	/**
	 * Logger
	 */
	protected static final Logger LOGGER = JaxrsLogger.create();

	/**
	 * Bean class laoder
	 */
	protected ClassLoader classLoader;

	/**
	 * Detected resources
	 */
	protected List<ApiListingDefinition> definitions;

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
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		LOGGER.debug(() -> "Lookup @Api Swagger resources in bean factory [" + beanFactory + "]");

		definitions = new ArrayList<>();

		Map<String, List<Class<?>>> packageClasses = new HashMap<>();

		for (String name : beanFactory.getBeanDefinitionNames()) {
			try {
				BeanDefinition definition = beanFactory.getBeanDefinition(name);
				if (!definition.isAbstract()) {
					Class<?> beanClass = BeanRegistryUtils.getBeanClass(name, definition, beanFactory, classLoader);
					if (beanClass != null && isApiResourceClass(definition, beanClass)) {

						final String packageName = beanClass.getPackage().getName();
						List<Class<?>> classes = packageClasses.get(packageName);
						if (classes == null) {
							classes = new LinkedList<>();
							packageClasses.put(packageName, classes);
						}
						classes.add(beanClass);

					}
				}
			} catch (@SuppressWarnings("unused") NoSuchBeanDefinitionException e) {
				// ignore
			}
		}

		if (!packageClasses.isEmpty()) {
			for (Entry<String, List<Class<?>>> entry : packageClasses.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().isEmpty()) {
					ApiDefinition apiDef = null;
					if (entry.getValue().get(0).getPackage().isAnnotationPresent(ApiDefinition.class)) {
						apiDef = entry.getValue().get(0).getPackage().getAnnotation(ApiDefinition.class);
					}
					if (apiDef == null) {
						for (Class<?> cls : entry.getValue()) {
							if (cls.isAnnotationPresent(ApiDefinition.class)) {
								apiDef = cls.getAnnotation(ApiDefinition.class);
								break;
							}
						}
					}

					if (packageClasses.size() == 1) {
						DefaultApiListingDefinition def = new DefaultApiListingDefinition(ApiGroupId.DEFAULT_GROUP_ID);
						def.setResourcePackage(entry.getKey());
						configure(def, apiDef);
						definitions.add(def);
					} else {

						String path = (apiDef != null) ? AnnotationUtils.getStringValue(apiDef.docsPath()) : null;

						if (hasApiDefinition(path)) {
							LOGGER.warn("Ignoring Swagger API listing endpoint auto-configuration for package name ["
									+ entry.getKey() + "]: an API listing endpoint with path [" + getPathOrDefault(path)
									+ "] is already registered");
						} else {
							DefaultApiListingDefinition def = new DefaultApiListingDefinition(
									(path == null) ? ApiGroupId.DEFAULT_GROUP_ID : entry.getKey());
							def.setResourcePackage(entry.getKey());
							configure(def, apiDef);
							definitions.add(def);
						}
					}
				}
			}
		}
	}

	private static void configure(DefaultApiListingDefinition definition, ApiDefinition apiDef) {
		if (apiDef != null) {
			String path = AnnotationUtils.getStringValue(apiDef.docsPath());
			if (path != null) {
				definition.setPath(path);
			}
			String value = AnnotationUtils.getStringValue(apiDef.title());
			if (value != null) {
				definition.setTitle(value);
			}
			value = AnnotationUtils.getStringValue(apiDef.version());
			if (value != null) {
				definition.setVersion(value);
			}
			value = AnnotationUtils.getStringValue(apiDef.description());
			if (value != null) {
				definition.setDescription(value);
			}
			value = AnnotationUtils.getStringValue(apiDef.termsOfServiceUrl());
			if (value != null) {
				definition.setTermsOfServiceUrl(value);
			}
			value = AnnotationUtils.getStringValue(apiDef.contact());
			if (value != null) {
				definition.setContact(value);
			}
			value = AnnotationUtils.getStringValue(apiDef.license());
			if (value != null) {
				definition.setLicense(value);
			}
			value = AnnotationUtils.getStringValue(apiDef.licenseUrl());
			if (value != null) {
				definition.setLicenseUrl(value);
			}
			String[] schemes = apiDef.schemes();
			if (schemes != null) {
				definition.setSchemes(schemes);
			}
			definition.setPrettyPrint(apiDef.prettyPrint());
		}
	}

	private boolean hasApiDefinition(String path) {
		final String docsPath = getPathOrDefault(path);
		if (definitions != null) {
			for (ApiListingDefinition d : definitions) {
				if (docsPath.equalsIgnoreCase(getPathOrDefault(d.getPath()))) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isApiResourceClass(@SuppressWarnings("unused") BeanDefinition definition,
			Class<?> beanClass) {
		return AnnotationUtils.getClassWithAnnotation(beanClass, Api.class) != null
				&& AnnotationUtils.getClassWithAnnotation(beanClass, Path.class) != null;
	}

	private static String getPathOrDefault(String path) {
		return (path != null && !path.trim().equals("")) ? path : SwaggerConfigurationProperties.DEFAULT_PATH;
	}

}
