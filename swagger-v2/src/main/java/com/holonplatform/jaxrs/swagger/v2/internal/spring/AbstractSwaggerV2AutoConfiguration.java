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
package com.holonplatform.jaxrs.swagger.v2.internal.spring;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointDefinition;
import com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration;
import com.holonplatform.jaxrs.swagger.spring.ApiConfigurationProperties;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.v2.internal.DefaultSwaggerConfiguration;
import com.holonplatform.jaxrs.swagger.v2.internal.endpoints.SwaggerEndpointBuilder;

import io.swagger.config.SwaggerConfig;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Contact;
import io.swagger.models.ExternalDocs;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.SecurityRequirement;

/**
 * Base Swagger API listing endpoints auto-configuration.
 * 
 * @param <A> JAX-RS Application type
 * 
 * @since 5.2.0
 */
public abstract class AbstractSwaggerV2AutoConfiguration<A extends Application>
		extends AbstractJaxrsApiEndpointsAutoConfiguration<A, SwaggerConfig> {

	private static final Map<ClassLoader, List<ApiEndpointDefinition>> API_ENDPOINT_DEFINITIONS = new WeakHashMap<>();

	/**
	 * Constructor.
	 * @param configurationProperties API configuration properties
	 * @param apiConfigurations API configurations provider
	 */
	public AbstractSwaggerV2AutoConfiguration(SwaggerConfigurationProperties configurationProperties,
			ObjectProvider<SwaggerConfig> apiConfigurations) {
		super(configurationProperties, apiConfigurations, SwaggerEndpointBuilder.INSTANCE);
	}

	/**
	 * Get the default JAX-RS application path, if available.
	 * @return Optional default JAX-RS application path
	 */
	protected abstract Optional<String> getDefaultApplicationPath();

	/**
	 * Register given endpoint class in JAX-RS application.
	 * @param application The JAX-RS application
	 * @param endpoint The endpoint class to register
	 */
	protected abstract void registerEndpoint(A application, Class<?> endpoint);

	@Bean
	@Order(Integer.MAX_VALUE - 100)
	public static ApplicationListener<ContextRefreshedEvent> jaxrsApiEndpointsDefinitionsV2InitializerApplicationListenerOnContextRefresh() {
		return event -> {
			API_ENDPOINT_DEFINITIONS
					.getOrDefault(event.getApplicationContext().getClassLoader(), Collections.emptyList())
					.forEach(d -> {
						if (d.init()) {
							LOGGER.info("Swagger V2 endpoint definition [" + d.getContextId() + "] initialized.");
						}
					});
		};
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#
	 * getApiEndpointContextId(java.lang.Object)
	 */
	@Override
	protected String getApiEndpointContextId(SwaggerConfig configuration) {
		if (configuration instanceof BeanConfig && ((BeanConfig) configuration).getContextId() != null) {
			return ((BeanConfig) configuration).getContextId();
		}
		return super.getApiEndpointContextId(configuration);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#
	 * getVersionApiEndpointPath(com.holonplatform.jaxrs.swagger.spring.ApiConfigurationProperties)
	 */
	@Override
	protected String getVersionApiEndpointPath(ApiConfigurationProperties configurationProperties) {
		if (configurationProperties.getV2() != null) {
			return configurationProperties.getV2().getPath();
		}
		return null;
	}

	/**
	 * Configure the API listing endpoints.
	 * @param application The JAX-RS application (not null)
	 */
	protected void configure(A application) {
		final List<ApiEndpointDefinition> definitions = API_ENDPOINT_DEFINITIONS.computeIfAbsent(classLoader,
				cl -> new LinkedList<>());
		configureEndpoints(application).forEach(d -> definitions.add(d));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#registerEndpoint(javax
	 * .ws.rs.core.Application, com.holonplatform.jaxrs.swagger.ApiEndpointDefinition)
	 */
	@Override
	protected void registerEndpoint(A application, ApiEndpointDefinition endpoint) {
		registerEndpoint(application, endpoint.getEndpointClass());
		// log
		final String path = getApplicationPath(application).map(ap -> {
			StringBuilder sb = new StringBuilder();
			if (!ap.startsWith("/")) {
				sb.append("/");
			}
			sb.append(ap);
			if (!endpoint.getPath().startsWith("/") && !ap.endsWith("/")) {
				sb.append("/");
			}
			if (endpoint.getPath().startsWith("/")) {
				if (endpoint.getPath().length() > 1) {
					sb.append(endpoint.getPath().substring(1));
				}
			} else {
				sb.append(endpoint.getPath());
			}
			return sb.toString();
		}).orElseGet(() -> endpoint.getPath());
		LOGGER.info("Registered Swagger V2 endpoint type [" + endpoint.getType() + "] to path [" + path
				+ "] bound to API context id: [" + endpoint.getContextId() + "] with scanner type ["
				+ endpoint.getScannerType() + "]");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.internal.spring.AbstractJaxrsApiEndpointsAutoConfiguration#getApplicationPath(
	 * javax.ws.rs.core.Application)
	 */
	@Override
	protected Optional<String> getApplicationPath(A application) {
		Optional<String> defaultPath = getDefaultApplicationPath().filter(p -> p != null && !p.trim().equals(""));
		if (defaultPath.isPresent()) {
			return defaultPath;
		}
		if (application != null) {
			final ApplicationPath applicationPath = AnnotationUtils.findAnnotation(application.getClass(),
					ApplicationPath.class);
			if (applicationPath != null && !"".equals(applicationPath.value())) {
				return Optional.of(applicationPath.value());
			}
		}
		return Optional.empty();
	}

	@Override
	protected SwaggerConfig buildConfiguration(ApiConfigurationProperties configurationProperties,
			ApiConfigurationProperties parent, String applicationPath) {
		final DefaultSwaggerConfiguration cfg = new DefaultSwaggerConfiguration();
		// configuration
		Set<String> packages = configurationProperties.getResourcePackages();
		if (!packages.isEmpty()) {
			cfg.setResourcePackages(packages);
		}
		cfg.setPrettyPrint(configurationProperties.isPrettyPrint());
		cfg.setReadAllResources(configurationProperties.isIncludeAll());
		// base path
		if (applicationPath != null) {
			cfg.setBasePath(applicationPath);
		}
		// info
		final Info info = new Info();
		cfg.setInfo(info);
		getConfigurationProperty(configurationProperties.getTitle()).ifPresent(v -> {
			info.setTitle(v);
		});
		getConfigurationProperty(configurationProperties.getVersion()).ifPresent(v -> {
			info.setVersion(v);
		});
		getConfigurationProperty(configurationProperties.getDescription()).ifPresent(v -> {
			info.setDescription(v);
		});
		getConfigurationProperty(configurationProperties.getTermsOfServiceUrl()).ifPresent(v -> {
			info.setTermsOfService(v);
		});
		getConfigurationProperty(configurationProperties.getLicenseName()).ifPresent(v -> {
			if (info.getLicense() == null) {
				info.setLicense(new License());
			}
			info.getLicense().setName(v);
		});
		getConfigurationProperty(configurationProperties.getLicenseUrl()).ifPresent(v -> {
			if (info.getLicense() == null) {
				info.setLicense(new License());
			}
			info.getLicense().setUrl(v);
		});
		getConfigurationProperty(configurationProperties.getContactName()).ifPresent(v -> {
			if (info.getContact() == null) {
				info.setContact(new Contact());
			}
			info.getContact().setName(v);
		});
		getConfigurationProperty(configurationProperties.getContactEmail()).ifPresent(v -> {
			if (info.getContact() == null) {
				info.setContact(new Contact());
			}
			info.getContact().setEmail(v);
		});
		getConfigurationProperty(configurationProperties.getContactUrl()).ifPresent(v -> {
			if (info.getContact() == null) {
				info.setContact(new Contact());
			}
			info.getContact().setUrl(v);
		});
		// server
		getConfigurationProperty(configurationProperties.getServerUrl()).ifPresent(v -> {
			cfg.setHost(v);
		});
		// external docs
		getConfigurationProperty(configurationProperties.getExternalDocsUrl()).ifPresent(v -> {
			if (cfg.getExternalDocs() == null) {
				cfg.setExternalDocs(new ExternalDocs());
			}
			cfg.getExternalDocs().setUrl(v);
		});
		getConfigurationProperty(configurationProperties.getExternalDocsDescription()).ifPresent(v -> {
			if (cfg.getExternalDocs() == null) {
				cfg.setExternalDocs(new ExternalDocs());
			}
			cfg.getExternalDocs().setDescription(v);
		});
		setSecurityRequirements(cfg, configurationProperties.getSecurityRequirements());
		// check parent
		if (parent != null) {
			if (info.getTitle() == null) {
				getConfigurationProperty(parent.getTitle()).ifPresent(v -> {
					info.setTitle(v);
				});
			}
			if (info.getVersion() == null) {
				getConfigurationProperty(parent.getVersion()).ifPresent(v -> {
					info.setVersion(v);
				});
			}
			if (info.getDescription() == null) {
				getConfigurationProperty(parent.getDescription()).ifPresent(v -> {
					info.setDescription(v);
				});
			}
			if (info.getTermsOfService() == null) {
				getConfigurationProperty(parent.getTermsOfServiceUrl()).ifPresent(v -> {
					info.setTermsOfService(v);
				});
			}
			if (info.getLicense() == null) {
				getConfigurationProperty(parent.getLicenseName()).ifPresent(v -> {
					if (info.getLicense() == null) {
						info.setLicense(new License());
					}
					info.getLicense().setName(v);
				});
				getConfigurationProperty(parent.getLicenseUrl()).ifPresent(v -> {
					if (info.getLicense() == null) {
						info.setLicense(new License());
					}
					info.getLicense().setUrl(v);
				});
			}
			if (info.getContact() == null) {
				getConfigurationProperty(parent.getContactName()).ifPresent(v -> {
					if (info.getContact() == null) {
						info.setContact(new Contact());
					}
					info.getContact().setName(v);
				});
				getConfigurationProperty(parent.getContactEmail()).ifPresent(v -> {
					if (info.getContact() == null) {
						info.setContact(new Contact());
					}
					info.getContact().setEmail(v);
				});
				getConfigurationProperty(parent.getContactUrl()).ifPresent(v -> {
					if (info.getContact() == null) {
						info.setContact(new Contact());
					}
					info.getContact().setUrl(v);
				});
			}
			if (cfg.getHost() == null) {
				getConfigurationProperty(parent.getServerUrl()).ifPresent(v -> {
					cfg.setHost(v);
				});
			}
			if (cfg.getExternalDocs() == null) {
				getConfigurationProperty(parent.getExternalDocsUrl()).ifPresent(v -> {
					if (cfg.getExternalDocs() == null) {
						cfg.setExternalDocs(new ExternalDocs());
					}
					cfg.getExternalDocs().setUrl(v);
				});
				getConfigurationProperty(parent.getExternalDocsDescription()).ifPresent(v -> {
					if (cfg.getExternalDocs() == null) {
						cfg.setExternalDocs(new ExternalDocs());
					}
					cfg.getExternalDocs().setDescription(v);
				});
			}
			if (cfg.getSecurity() == null || cfg.getSecurity().isEmpty()) {
				setSecurityRequirements(cfg, parent.getSecurityRequirements());
			}
		}

		// done
		return cfg;
	}

	private static void setSecurityRequirements(DefaultSwaggerConfiguration cfg,
			List<Map<String, List<String>>> security) {
		if (security != null) {
			for (Map<String, List<String>> requirement : security) {
				if (requirement != null) {
					SecurityRequirement sr = new SecurityRequirement();
					for (Entry<String, List<String>> entry : requirement.entrySet()) {
						if (entry.getKey() != null && entry.getValue() != null) {
							sr.setRequirements(entry.getKey(), entry.getValue());
						}
					}
					if (!sr.getRequirements().isEmpty()) {
						if (cfg.getSecurity() == null) {
							cfg.setSecurity(new LinkedList<>());
						}
						cfg.getSecurity().add(sr);
					}
				}
			}
		}
	}

	@Deprecated
	public static Optional<String> getContextIdByPath(ClassLoader classLoader, String path) {
		if (path != null) {
			ClassLoader cl = (classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader();
			for (ApiEndpointDefinition d : API_ENDPOINT_DEFINITIONS.getOrDefault(cl, Collections.emptyList())) {
				if (path.equals(d.getPath())) {
					return Optional.ofNullable(d.getContextId());
				}
			}
		}
		return Optional.empty();
	}

}
