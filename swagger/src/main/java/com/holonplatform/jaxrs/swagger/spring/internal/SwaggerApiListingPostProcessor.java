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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Path;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.internal.JaxrsLogger;
import com.holonplatform.jaxrs.swagger.annotations.ApiDefinition;
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
	private ClassLoader classLoader;

	/**
	 * Detected resources
	 */
	private List<ApiListingDefinition> definitions;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang.ClassLoader)
	 */
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Get the bean factory ClassLoader.
	 * @return the bean factory ClassLoader
	 */
	protected ClassLoader getBeanClassLoader() {
		return classLoader;
	}

	/**
	 * Get the detected Swagger API listing definitions.
	 * @return the wagger API listing definitions
	 */
	protected List<ApiListingDefinition> getDefinitions() {
		return (definitions != null) ? definitions : Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.
	 * beans.factory.config.ConfigurableListableBeanFactory)
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		LOGGER.debug(() -> "Lookup Swagger API resources in bean factory [" + beanFactory + "]");

		definitions = new ArrayList<>();

		// detect valid API listing resources
		final Set<ApiResource> resources = new HashSet<>();
		for (String name : beanFactory.getBeanDefinitionNames()) {
			try {
				BeanDefinition definition = beanFactory.getBeanDefinition(name);
				if (!definition.isAbstract()) {
					final Class<?> beanClass = BeanRegistryUtils.getBeanClass(name, definition, beanFactory,
							classLoader);
					// check not hidden
					if (!getApiAnnotation(beanClass).map(a -> a.hidden()).orElse(false)) {
						if (beanClass != null && isApiResourceClass(beanClass)) {
							resources.add(
									new ApiResource(beanClass, getApiDefinitionAnnotation(beanClass).orElse(null)));
						}
					}
				}
			} catch (@SuppressWarnings("unused") NoSuchBeanDefinitionException e) {
				// ignore
			}
		}

		// build API listing definitions
		definitions = resources.stream().map(r -> {
			final DefaultApiListingDefinition definition = new DefaultApiListingDefinition(
					r.getResourceClass().getName());
			definition.setClassesToScan(Collections.singleton(r.getResourceClass()));
			definition
					.setPath(r.getApiDefinition().map(d -> d.docsPath()).filter(p -> p != null && !p.trim().equals(""))
							.orElse(SwaggerConfigurationProperties.DEFAULT_PATH));
			definition.setTitle(r.getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.title())).orElse(null));
			definition.setVersion(
					r.getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.version())).orElse(null));
			definition.setDescription(
					r.getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.description())).orElse(null));
			definition.setTermsOfServiceUrl(
					r.getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.termsOfServiceUrl())).orElse(null));
			definition.setContact(
					r.getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.contact())).orElse(null));
			definition.setLicense(
					r.getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.license())).orElse(null));
			definition.setLicenseUrl(
					r.getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.licenseUrl())).orElse(null));
			definition.setSchemes(r.getApiDefinition().map(d -> d.schemes()).orElse(null));
			definition.setPrettyPrint(r.getApiDefinition().map(d -> d.prettyPrint()).orElse(false));
			definition.setHost(r.getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.host())).orElse(null));
			return definition;
		}).collect(Collectors.toList());

		// TODO check duplicate or semanticaly equal docs api paths
	}

	/**
	 * Get the {@link ApiDefinition} annotation associated to given class or to the class package definition.
	 * @param resourceClass The resource class
	 * @return Optional {@link ApiDefinition} annotation
	 */
	private static Optional<ApiDefinition> getApiDefinitionAnnotation(Class<?> resourceClass) {
		if (resourceClass.isAnnotationPresent(ApiDefinition.class)) {
			return Optional.of(resourceClass.getAnnotation(ApiDefinition.class));
		}
		return Optional.ofNullable(resourceClass.getPackage().getAnnotation(ApiDefinition.class));
	}

	/**
	 * Get the {@link Api} annotation associated to given class or to the class package definition.
	 * @param resourceClass The resource class
	 * @return Optional {@link Api} annotation
	 */
	private static Optional<Api> getApiAnnotation(Class<?> resourceClass) {
		if (resourceClass.isAnnotationPresent(Api.class)) {
			return Optional.of(resourceClass.getAnnotation(Api.class));
		}
		return Optional.ofNullable(resourceClass.getPackage().getAnnotation(Api.class));
	}

	private boolean hasApiDefinition(String path) {
		final String docsPath = getPathOrDefault(path);
		if (definitions != null) {
			for (ApiListingDefinition d : definitions) {
				if (docsPath.equalsIgnoreCase(d.getEndpointPath())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether the given bean class is to be considered a Swagger API resource class.
	 * @param beanClass The class to check
	 * @return whether the given bean class is to be considered a Swagger API resource clas
	 */
	private static boolean isApiResourceClass(Class<?> beanClass) {
		return AnnotationUtils.hasAnnotation(beanClass, Path.class)
				&& (AnnotationUtils.hasAnnotation(beanClass, Api.class)
						|| AnnotationUtils.hasAnnotation(beanClass, ApiDefinition.class));
	}

	/**
	 * Get the API listing endpoint path.
	 * @param path The path
	 * @return The given path or {@link SwaggerConfigurationProperties#DEFAULT_PATH} if path was null or blank
	 */
	private static String getPathOrDefault(String path) {
		return (path != null && !path.trim().equals("")) ? path : SwaggerConfigurationProperties.DEFAULT_PATH;
	}

	private static class ApiResource {

		private final Class<?> resourceClass;
		private final ApiDefinition apiDefinition;

		public ApiResource(Class<?> resourceClass, ApiDefinition apiDefinition) {
			super();
			ObjectUtils.argumentNotNull(resourceClass, "Resource class must be not null");
			this.resourceClass = resourceClass;
			this.apiDefinition = apiDefinition;
		}

		public Class<?> getResourceClass() {
			return resourceClass;
		}

		public Optional<ApiDefinition> getApiDefinition() {
			return Optional.ofNullable(apiDefinition);
		}

	}

}
