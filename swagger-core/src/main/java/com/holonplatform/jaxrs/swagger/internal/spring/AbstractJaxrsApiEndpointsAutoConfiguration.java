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
package com.holonplatform.jaxrs.swagger.internal.spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Application;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.ApiDefaults;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.annotations.ApiConfiguration;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.internal.SwaggerLogger;
import com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointBuilder;
import com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointConfiguration;
import com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointDefinition;
import com.holonplatform.jaxrs.swagger.spring.ApiConfigurationProperties;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties.ApiGroupConfiguration;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties.Contact;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties.License;

/**
 * Base Spring Boot auto-configuration class for JAX-RS API listing endpoints.
 * 
 * @param <A> JAX-RS Application type
 * @param <C> API configuration type
 * 
 * @since 5.2.0
 */
public abstract class AbstractJaxrsApiEndpointsAutoConfiguration<A extends Application, C>
		implements BeanClassLoaderAware, BeanFactoryAware {

	protected static final Logger LOGGER = SwaggerLogger.create();

	private final SwaggerConfigurationProperties configurationProperties;

	private final ObjectProvider<C> apiConfigurations;

	private final ApiEndpointBuilder<C> apiEndpointBuilder;

	protected ClassLoader classLoader;

	private BeanFactory beanFactory;

	/**
	 * Constructor.
	 * @param configurationProperties API configuration properties
	 * @param apiConfigurations API configurations provider
	 * @param apiEndpointBuilder API endpoint builder
	 */
	public AbstractJaxrsApiEndpointsAutoConfiguration(SwaggerConfigurationProperties configurationProperties,
			ObjectProvider<C> apiConfigurations, ApiEndpointBuilder<C> apiEndpointBuilder) {
		super();
		ObjectUtils.argumentNotNull(apiEndpointBuilder, "ApiEndpointBuilder must be not null");
		this.configurationProperties = configurationProperties;
		this.apiConfigurations = apiConfigurations;
		this.apiEndpointBuilder = apiEndpointBuilder;
	}

	@Bean
	@ConfigurationPropertiesBinding
	public static LegacyContactNameConverter legacyContactToContactNamePropertyConverter() {
		return new LegacyContactNameConverter();
	}

	@Bean
	@ConfigurationPropertiesBinding
	public static LegacyLicenseNameConverter legacyLicenseToLicenseNamePropertyConverter() {
		return new LegacyLicenseNameConverter();
	}

	/**
	 * Get the JAX-RS Application path.
	 * @param application The JAX-RS application
	 * @return Optional JAX-RS Application path
	 */
	protected abstract Optional<String> getApplicationPath(A application);

	/**
	 * Build an API configuration using given configuration properties.
	 * @param configurationProperties The API configuration properties
	 * @param parent Optional parent configuration properties
	 * @param applicationPath Application path
	 * @return The API configuration
	 */
	protected abstract C buildConfiguration(ApiConfigurationProperties configurationProperties,
			ApiConfigurationProperties parent, String applicationPath);

	/**
	 * Register given endpoint class in the JAX-RS application.
	 * @param application The JAX-RS application
	 * @param endpoint The endpoint definition to register
	 */
	protected abstract void registerEndpoint(A application, ApiEndpointDefinition endpoint);

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
	 * org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
	 * Configure the API listing endpoints, if the configuration is enabled
	 * @param application The JAX-RS application (not null)
	 * @return The API endpoint definitions
	 */
	protected List<ApiEndpointDefinition> configureEndpoints(A application) {
		// check disabled
		if (configurationProperties.isEnabled()) {
			return registerEndpoints(application);
		} else {
			LOGGER.info("Swagger API endpoints configuration is disabled.");
			return Collections.emptyList();
		}
	}

	/**
	 * Register the API listing endpoints.
	 * @param application The JAX-RS application (not null)
	 * @return The API endpoint definitions
	 */
	private List<ApiEndpointDefinition> registerEndpoints(A application) {
		final List<ApiEndpointDefinition> definitions = new LinkedList<>();
		final List<C> configurations = this.apiConfigurations.stream().collect(Collectors.toList());
		if (configurations.isEmpty()) {
			// default configurations
			final ApiConfigurationProperties parent;
			if (configurationProperties.getApiGroups() != null && !configurationProperties.getApiGroups().isEmpty()) {
				parent = configurationProperties;
			} else {
				parent = null;
			}
			getDefaultConfigurations(application).entrySet().forEach(e -> {
				definitions.add(configureAndRegisterEndpoint(application, e.getValue(), parent, e.getKey()));
			});
		} else {
			// use configurations
			for (C configuration : configurations) {
				definitions.add(configureAndRegisterEndpoint(application, configuration,
						getApiEndpointContextId(configuration)));
			}
		}
		return definitions;
	}

	/**
	 * Get the default API configurations from configuration properties.
	 * @param application The JAX-RS application (not null)
	 * @return The contextId - configuration properties, empty if none
	 */
	private Map<String, ApiConfigurationProperties> getDefaultConfigurations(A application) {
		final Map<String, ApiConfigurationProperties> configurations = new HashMap<>();
		if (configurationProperties.getApiGroups() != null && !configurationProperties.getApiGroups().isEmpty()) {
			// groups
			for (ApiGroupConfiguration group : configurationProperties.getApiGroups()) {
				String contextId = group.getGroupId();
				if (contextId == null || contextId.trim().equals("")) {
					contextId = ApiDefaults.DEFAULT_CONTEXT_ID;
				}
				if (configurations.containsKey(contextId)) {
					throw new ApiConfigurationException("Duplicate Swagger API configuration group id: " + contextId);
				}
				configurations.put(contextId, group);
			}
		} else {
			// check legacy ApiDefinition
			Map<String, ApiConfigurationProperties> legacy = getApiDefinitionConfigurations(application);
			if (!legacy.isEmpty()) {
				return legacy;
			}
			// default
			String contextId = configurationProperties.getContextId();
			configurations.put(
					(contextId != null && !contextId.trim().equals("")) ? contextId : ApiDefaults.DEFAULT_CONTEXT_ID,
					configurationProperties);
		}
		return configurations;
	}

	@Deprecated
	private Map<String, ApiConfigurationProperties> getApiDefinitionConfigurations(A application) {
		Map<String, ApiConfigurationProperties> cfgs = new HashMap<>();
		Set<Class<?>> classes = (application != null && application.getClasses() != null) ? application.getClasses()
				: Collections.emptySet();
		for (Class<?> cls : classes) {
			if (cls.isAnnotationPresent(com.holonplatform.jaxrs.swagger.annotations.ApiDefinition.class)) {
				com.holonplatform.jaxrs.swagger.annotations.ApiDefinition ad = cls
						.getAnnotation(com.holonplatform.jaxrs.swagger.annotations.ApiDefinition.class);
				String path = AnnotationUtils.getStringValue(ad.value());
				if (!"".equals(ad.docsPath())) {
					path = AnnotationUtils.getStringValue(ad.docsPath());
				}
				if (path != null && !cfgs.containsKey(path)) {
					SwaggerConfigurationProperties cfg = new SwaggerConfigurationProperties();
					cfg.setContextId(path);
					cfg.setPath(path);
					cfg.setPrettyPrint(ad.prettyPrint());
					cfg.setTitle(AnnotationUtils.getStringValue(ad.title()));
					cfg.setVersion(AnnotationUtils.getStringValue(ad.version()));
					cfg.setDescription(AnnotationUtils.getStringValue(ad.description()));
					cfg.setTermsOfServiceUrl(AnnotationUtils.getStringValue(ad.termsOfServiceUrl()));
					cfg.setHost(AnnotationUtils.getStringValue(ad.host()));
					cfg.setSchemes(ad.schemes());
					String cname = AnnotationUtils.getStringValue(ad.contact());
					if (cname != null) {
						Contact c = new Contact();
						c.setName(cname);
						cfg.setContact(c);
					}
					String lname = AnnotationUtils.getStringValue(ad.license());
					String lurl = AnnotationUtils.getStringValue(ad.licenseUrl());
					if (lname != null || lurl != null) {
						License l = new License();
						l.setName(lname);
						l.setUrl(lurl);
						cfg.setLicense(l);
					}
					cfgs.put(path, cfg);
				}
			}
		}
		return cfgs;
	}

	/**
	 * Register an API listing endpoint using given configuration.
	 * @param application The JAX-RS application
	 * @param configuration The API configuration
	 * @param contextId The API context id
	 * @return The API endpoint definition
	 */
	private ApiEndpointDefinition configureAndRegisterEndpoint(A application, C configuration, String contextId) {
		// create endpoint
		final ApiEndpointDefinition endpoint = apiEndpointBuilder.build(ApiEndpointConfiguration.<C>builder()
				// context id
				.contextId(contextId)
				// API configuration
				.configuration(configuration)
				// JAX-RS application
				.application(application)
				// path
				.path(getApiEndpointPath(configuration, contextId))
				// type
				.type(getApiEndpointType(configuration))
				// scanner
				.scannerType(getScannerType(configuration))
				// classLoader
				.classLoader(classLoader)
				// auto configuration packages
				.rootResourcePackages(getAutoScanPackages())
				// build
				.build());
		// register endpoint
		registerEndpoint(application, endpoint);
		return endpoint;
	}

	/**
	 * Register an API listing endpoint using given configuration properties.
	 * @param application The JAX-RS application
	 * @param configurationProperties The API configuration properties
	 * @param parent Optional parent configuration properties
	 * @param contextId The API context id
	 * @return The API endpoint definition
	 */
	private ApiEndpointDefinition configureAndRegisterEndpoint(A application,
			ApiConfigurationProperties configurationProperties, ApiConfigurationProperties parent, String contextId) {
		final C configuration = buildConfiguration(configurationProperties, parent,
				getApplicationPath(application).orElse(null));
		// create endpoint
		final ApiEndpointDefinition endpoint = apiEndpointBuilder.build(ApiEndpointConfiguration.<C>builder()
				// context id
				.contextId(contextId)
				// API configuration
				.configuration(configuration)
				// JAX-RS application
				.application(application)
				// path
				.path(getApiEndpointPath(configurationProperties, contextId))
				// type
				.type(getApiEndpointType(configurationProperties))
				// scanner
				.scannerType(getScannerType(configurationProperties))
				// classLoader
				.classLoader(classLoader)
				// auto configuration packages
				.rootResourcePackages(getAutoScanPackages())
				// build
				.build());
		// register endpoint
		registerEndpoint(application, endpoint);
		return endpoint;
	}

	/**
	 * Get the API context id.
	 * @param configuration The API configuration
	 * @return the API context id
	 */
	protected String getApiEndpointContextId(C configuration) {
		return AnnotationUtils.getAnnotation(configuration.getClass(), ApiConfiguration.class)
				.map(a -> AnnotationUtils.getStringValue(a.contextId())).orElse(ApiDefaults.DEFAULT_CONTEXT_ID);
	}

	/**
	 * Get the API endpoint path.
	 * @param configuration The API configuration
	 * @param contextId The API context id
	 * @return the API endpoint path
	 */
	private String getApiEndpointPath(C configuration, String contextId) {
		return AnnotationUtils.getAnnotation(configuration.getClass(), ApiConfiguration.class)
				.map(a -> AnnotationUtils.getStringValue(a.path()))
				.orElseGet(() -> getDefaultApiEndpointPath(contextId, false));
	}

	/**
	 * Get the API endpoint type.
	 * @param configuration The API configuration
	 * @return the API endpoint type
	 */
	private ApiEndpointType getApiEndpointType(C configuration) {
		return AnnotationUtils.getAnnotation(configuration.getClass(), ApiConfiguration.class)
				.map(a -> a.endpointType()).orElse(ApiEndpointType.getDefault());
	}

	/**
	 * Get the scanner type.
	 * @param configuration The API configuration
	 * @return the API resources scanner type
	 */
	private JaxrsScannerType getScannerType(C configuration) {
		JaxrsScannerType type = AnnotationUtils.getAnnotation(configuration.getClass(), ApiConfiguration.class)
				.map(a -> a.scannerType()).orElseGet(() -> getDefaultScannerType());
		if (JaxrsScannerType.DEFAULT == type) {
			return getDefaultScannerType();
		}
		return type;
	}

	/**
	 * Get the API endpoint path.
	 * @param configurationProperties API configuration properties
	 * @param contextId The API context id
	 * @param appendContextId Whether to append the context id, if available
	 * @return the API endpoint path
	 */
	private String getApiEndpointPath(ApiConfigurationProperties configurationProperties, String contextId) {
		String versionPath = getVersionApiEndpointPath(configurationProperties);
		if (versionPath != null && !versionPath.trim().equals("")) {
			return versionPath;
		}
		if (configurationProperties.getPath() != null && !configurationProperties.getPath().trim().equals("")) {
			return configurationProperties.getPath();
		}
		return getDefaultApiEndpointPath(contextId, configurationProperties.isGroupConfiguration());
	}

	/**
	 * Get the API endpoint path for a specific API definition version.
	 * @param configurationProperties API configuration properties
	 * @return the API endpoint path, <code>null</code> if not available
	 */
	protected abstract String getVersionApiEndpointPath(ApiConfigurationProperties configurationProperties);

	/**
	 * Get the API endpoint type.
	 * @param configurationProperties API configuration properties
	 * @return the API endpoint type
	 */
	private static ApiEndpointType getApiEndpointType(ApiConfigurationProperties configurationProperties) {
		if (configurationProperties.getType() != null) {
			return configurationProperties.getType();
		}
		return ApiEndpointType.getDefault();
	}

	/**
	 * Get the API scanner type.
	 * @param configurationProperties API configuration properties
	 * @return the API resources scanner type
	 */
	private JaxrsScannerType getScannerType(ApiConfigurationProperties configurationProperties) {
		if (configurationProperties.getScannerType() != null) {
			return configurationProperties.getScannerType();
		}
		return getDefaultScannerType();
	}

	/**
	 * Get the default scanner type.
	 * @return the default scanner type
	 */
	protected abstract JaxrsScannerType getDefaultScannerType();

	/**
	 * Get the packages to scan in context.
	 * @return the packages to scan
	 */
	private Set<String> getAutoScanPackages() {
		// check Component scan
		if (beanFactory instanceof ListableBeanFactory) {
			String[] names = ((ListableBeanFactory) beanFactory).getBeanNamesForAnnotation(ComponentScan.class);
			if (names != null && names.length > 0) {
				final Set<String> packages = new HashSet<>();
				for (String name : names) {
					ComponentScan scan = ((ListableBeanFactory) beanFactory).findAnnotationOnBean(name,
							ComponentScan.class);
					if (scan != null) {
						for (String p : scan.value()) {
							packages.add(p);
						}
						for (String p : scan.basePackages()) {
							packages.add(p);
						}
						for (Class<?> cls : scan.basePackageClasses()) {
							if (cls.getPackage() != null) {
								packages.add(cls.getPackage().getName());
							}
						}
					}
				}
				return packages;
			}
		}
		// auto configuration packages
		if (AutoConfigurationPackages.has(beanFactory)) {
			List<String> acpackages = AutoConfigurationPackages.get(beanFactory);
			if (acpackages != null) {
				return new HashSet<>(acpackages);
			}
		}
		return Collections.emptySet();
	}

	/**
	 * Get the default API listing endpoint path.
	 * @param contextId Optional API context id
	 * @param appendContextId Whether to append the context id, if available
	 * @return the default API listing endpoint path
	 */
	protected String getDefaultApiEndpointPath(String contextId, boolean appendContextId) {
		if (appendContextId && contextId != null && !contextId.trim().equals("")
				&& !ApiDefaults.DEFAULT_CONTEXT_ID.equals(contextId)) {
			return ApiDefaults.DEFAULT_API_ENDPOINT_PATH + "/" + contextId;
		}
		return ApiDefaults.DEFAULT_API_ENDPOINT_PATH;
	}

	/**
	 * Get a configuration property value, only if it is not <code>null</code> and not blank.
	 * @param value The value
	 * @return Optional configuration property value
	 */
	protected static Optional<String> getConfigurationProperty(String value) {
		if (value != null && !value.trim().equals("")) {
			return Optional.of(value.trim());
		}
		return Optional.empty();
	}

}
