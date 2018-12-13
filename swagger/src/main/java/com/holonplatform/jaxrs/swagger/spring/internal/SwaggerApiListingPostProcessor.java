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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
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
import com.holonplatform.jaxrs.swagger.SwaggerConfiguration;
import com.holonplatform.jaxrs.swagger.annotations.ApiDefinition;
import com.holonplatform.jaxrs.swagger.exceptions.ApiContextConfigurationException;
import com.holonplatform.spring.internal.BeanRegistryUtils;

import io.swagger.annotations.Api;

/**
 * Base {@link BeanFactoryPostProcessor} to auto-detected {@link Api} Swagger endpoint and configure API listing
 * resources.
 * 
 * @since 5.0.0
 */
public abstract class SwaggerApiListingPostProcessor extends AbstractSwaggerConfigurator
		implements BeanFactoryPostProcessor, BeanClassLoaderAware {

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
		final Map<String, Set<ApiResource>> resources = new HashMap<>();
		for (String name : beanFactory.getBeanDefinitionNames()) {
			try {
				BeanDefinition definition = beanFactory.getBeanDefinition(name);
				if (!definition.isAbstract()) {
					final Class<?> beanClass = BeanRegistryUtils.getBeanClass(name, definition, beanFactory,
							classLoader);
					// check not hidden
					if (!getApiAnnotation(beanClass).map(a -> a.hidden()).orElse(false)) {
						if (beanClass != null && isApiResourceClass(beanClass)) {
							final ApiResource apiResource = new ApiResource(beanClass.getName(), beanClass,
									getApiDefinitionAnnotation(beanClass).orElse(null));
							resources.computeIfAbsent(apiResource.getPath(), path -> new HashSet<>()).add(apiResource);
						}
					}
				}
			} catch (@SuppressWarnings("unused") NoSuchBeanDefinitionException e) {
				// ignore
			}
		}

		// validate and reduce
		final List<ApiResource> mergedResources = new ArrayList<>(resources.size());
		int groupCount = 0;
		for (Entry<String, Set<ApiResource>> entry : resources.entrySet()) {
			validateAndMergeResources(
					SwaggerConfiguration.class.getPackage().getName() + ".api_definition_" + groupCount, entry.getKey(),
					entry.getValue()).ifPresent(r -> mergedResources.add(r));
			groupCount++;
		}

		// build API listing definitions
		definitions = mergedResources.stream().map(r -> {
			final DefaultApiListingDefinition definition = new DefaultApiListingDefinition(r.getGroupId());
			definition.setClassesToScan(r.getClassesToScan());
			definition.setPath(r.getPath());
			definition.setTitle(AnnotationUtils.getStringValue(r.getTitle()));
			definition.setVersion(AnnotationUtils.getStringValue(r.getVersion()));
			definition.setDescription(AnnotationUtils.getStringValue(r.getDescription()));
			definition.setTermsOfServiceUrl(AnnotationUtils.getStringValue(r.getTermsOfServiceUrl()));
			definition.setContact(AnnotationUtils.getStringValue(r.getContact()));
			definition.setLicense(AnnotationUtils.getStringValue(r.getLicense()));
			definition.setLicenseUrl(AnnotationUtils.getStringValue(r.getLicenseUrl()));
			definition.setHost(AnnotationUtils.getStringValue(r.getHost()));
			definition.setSchemes(r.getSchemes());
			definition.setPrettyPrint(r.isPrettyPrint());
			return definition;
		}).collect(Collectors.toList());
	}

	/**
	 * Validate given API resource set and merge it into a single definition.
	 * @param apiGroupId API group id
	 * @param path API listing path
	 * @param resources Resource to validate and merge
	 * @return Merged resource
	 * @throws ApiContextConfigurationException If the resources are not mergeable
	 */
	private static Optional<ApiResource> validateAndMergeResources(String apiGroupId, String path,
			Set<ApiResource> resources) {
		if (resources == null || resources.isEmpty()) {
			return Optional.empty();
		}

		String[] schemes = null;
		String title = null;
		String version = null;
		String description = null;
		String termsOfServiceUrl = null;
		String contact = null;
		String license = null;
		String licenseUrl = null;
		String host = null;
		boolean prettyPrint = false;

		for (ApiResource r : resources) {
			schemes = checkDefinitionValue(path, "schemes", schemes, () -> r.getSchemes());
			title = checkDefinitionValue(path, "title", title, () -> r.getTitle());
			version = checkDefinitionValue(path, "version", version, () -> r.getVersion());
			description = checkDefinitionValue(path, "description", description, () -> r.getDescription());
			termsOfServiceUrl = checkDefinitionValue(path, "termsOfServiceUrl", termsOfServiceUrl,
					() -> r.getTermsOfServiceUrl());
			contact = checkDefinitionValue(path, "contact", contact, () -> r.getContact());
			license = checkDefinitionValue(path, "license", license, () -> r.getLicense());
			licenseUrl = checkDefinitionValue(path, "licenseUrl", licenseUrl, () -> r.getLicenseUrl());
			host = checkDefinitionValue(path, "host", host, () -> r.getHost());
			if (r.isPrettyPrint()) {
				prettyPrint = true;
			}
		}
		// merge
		final Set<Class<?>> classes = new HashSet<>();
		for (ApiResource r : resources) {
			classes.addAll(r.getClassesToScan());
		}
		final ApiResource r = new ApiResource(apiGroupId, classes, path);
		r.setSchemes(schemes);
		r.setTitle(title);
		r.setVersion(version);
		r.setDescription(description);
		r.setTermsOfServiceUrl(termsOfServiceUrl);
		r.setContact(contact);
		r.setLicense(licenseUrl);
		r.setLicenseUrl(licenseUrl);
		r.setHost(host);
		r.setPrettyPrint(prettyPrint);
		return Optional.of(r);
	}

	private static String[] checkDefinitionValue(String path, String message, String[] value,
			Supplier<String[]> definition) {
		if (value == null) {
			return definition.get();
		} else {
			if (definition.get() != null && !Arrays.equals(value, definition.get())) {
				throw new ApiContextConfigurationException("Invalid Api definitions for the same path [" + path
						+ "], different " + message + " declarations: [" + value + "] - [" + definition.get() + "]");
			}
		}
		return value;
	}

	private static String checkDefinitionValue(String path, String message, String value, Supplier<String> definition) {
		if (value == null) {
			return definition.get();
		} else {
			if (definition.get() != null && !value.equals(definition.get())) {
				throw new ApiContextConfigurationException("Invalid Api definitions for the same path [" + path
						+ "], different " + message + " declarations: [" + value + "] - [" + definition.get() + "]");
			}
		}
		return value;
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

	/**
	 * Checks whether the given bean class is to be considered a Swagger API resource class.
	 * @param beanClass The class to check
	 * @return whether the given bean class is to be considered a Swagger API resource clas
	 */
	private static boolean isApiResourceClass(Class<?> beanClass) {
		return AnnotationUtils.hasAnnotation(beanClass, Path.class)
				&& AnnotationUtils.hasAnnotation(beanClass, Api.class);
	}

	private static class ApiResource implements ApiListingConfiguration {

		private static final long serialVersionUID = -7421981485721651139L;

		private final String apiGroupId;
		private final Set<Class<?>> resourceClasses;
		private final ApiDefinition apiDefinition;
		private final String path;

		private String[] schemes;
		private String title;
		private String version;
		private String description;
		private String termsOfServiceUrl;
		private String contact;
		private String license;
		private String licenseUrl;
		private String host;
		private boolean prettyPrint;

		public ApiResource(String apiGroupId, Class<?> resourceClass, ApiDefinition apiDefinition) {
			this(apiGroupId, Collections.singleton(resourceClass), null, apiDefinition);
			ObjectUtils.argumentNotNull(resourceClass, "Resource class must be not null");
		}

		public ApiResource(String apiGroupId, Set<Class<?>> resourceClasses, String path) {
			this(apiGroupId, resourceClasses, path, null);
		}

		public ApiResource(String apiGroupId, Set<Class<?>> resourceClasses, String path, ApiDefinition apiDefinition) {
			super();
			ObjectUtils.argumentNotNull(apiGroupId, "API group id must be not null");
			ObjectUtils.argumentNotNull(resourceClasses, "Resource classes must be not null");
			this.apiGroupId = apiGroupId;
			this.resourceClasses = resourceClasses;
			this.apiDefinition = apiDefinition;
			this.path = (path != null) ? path : getPath(apiDefinition);
		}

		@Override
		public String getGroupId() {
			return apiGroupId;
		}

		@Override
		public Set<Class<?>> getClassesToScan() {
			return resourceClasses;
		}

		@Override
		public String getPath() {
			return path;
		}

		@Override
		public Optional<String> getResourcePackage() {
			return Optional.empty();
		}

		@Override
		public String[] getSchemes() {
			return getApiDefinition().map(d -> d.schemes()).orElse(schemes);
		}

		@Override
		public String getTitle() {
			return getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.title())).orElse(title);
		}

		@Override
		public String getVersion() {
			return getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.version())).orElse(version);
		}

		@Override
		public String getDescription() {
			return getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.description())).orElse(description);
		}

		@Override
		public String getTermsOfServiceUrl() {
			return getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.termsOfServiceUrl()))
					.orElse(termsOfServiceUrl);
		}

		@Override
		public String getContact() {
			return getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.contact())).orElse(contact);
		}

		@Override
		public String getLicense() {
			return getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.license())).orElse(license);
		}

		@Override
		public String getLicenseUrl() {
			return getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.licenseUrl())).orElse(licenseUrl);
		}

		@Override
		public String getHost() {
			return getApiDefinition().map(d -> AnnotationUtils.getStringValue(d.host())).orElse(host);
		}

		@Override
		public boolean isPrettyPrint() {
			return getApiDefinition().map(d -> d.prettyPrint()).orElse(prettyPrint);
		}

		public void setSchemes(String[] schemes) {
			this.schemes = schemes;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setTermsOfServiceUrl(String termsOfServiceUrl) {
			this.termsOfServiceUrl = termsOfServiceUrl;
		}

		public void setContact(String contact) {
			this.contact = contact;
		}

		public void setLicense(String license) {
			this.license = license;
		}

		public void setLicenseUrl(String licenseUrl) {
			this.licenseUrl = licenseUrl;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public void setPrettyPrint(boolean prettyPrint) {
			this.prettyPrint = prettyPrint;
		}

		private Optional<ApiDefinition> getApiDefinition() {
			return Optional.ofNullable(apiDefinition);
		}

		@SuppressWarnings("deprecation")
		private static String getPath(ApiDefinition apiDefinition) {
			if (apiDefinition != null) {
				String docsPath = AnnotationUtils.getStringValue(apiDefinition.docsPath());
				if (docsPath != null && !docsPath.trim().equals("")) {
					return docsPath;
				}
				String path = AnnotationUtils.getStringValue(apiDefinition.value());
				if (path != null && !path.trim().equals("")) {
					return path;
				}
			}
			return ApiDefinition.DEFAULT_PATH;
		}

	}

}
