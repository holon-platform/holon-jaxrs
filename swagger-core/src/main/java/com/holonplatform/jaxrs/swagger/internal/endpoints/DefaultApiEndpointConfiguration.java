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
package com.holonplatform.jaxrs.swagger.internal.endpoints;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import jakarta.ws.rs.core.Application;

import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;

/**
 * Default {@link ApiEndpointConfiguration} implementation.
 * 
 * @param <C> API configuration type
 *
 * @since 5.2.0
 */
public class DefaultApiEndpointConfiguration<C> implements ApiEndpointConfiguration<C> {

	private static final long serialVersionUID = 930451863047563827L;

	private transient ClassLoader classLoader;
	private transient Application application;
	private Set<String> rootResourcePackages;
	private ApiEndpointType type;
	private JaxrsScannerType scannerType;
	private String contextId;
	private String path;
	private String configurationLocation;
	private transient C configuration;

	public DefaultApiEndpointConfiguration() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration#getClassLoader()
	 */
	@Override
	public Optional<ClassLoader> getClassLoader() {
		return Optional.ofNullable(classLoader);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration#getApplication()
	 */
	@Override
	public Optional<Application> getApplication() {
		return Optional.ofNullable(application);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointConfiguration#getRootResourcePackages()
	 */
	@Override
	public Set<String> getRootResourcePackages() {
		return (rootResourcePackages != null) ? rootResourcePackages : Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration#getType()
	 */
	@Override
	public Optional<ApiEndpointType> getType() {
		return Optional.ofNullable(type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointConfiguration#getScannerType()
	 */
	@Override
	public Optional<JaxrsScannerType> getScannerType() {
		return Optional.ofNullable(scannerType);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration#getContextId()
	 */
	@Override
	public Optional<String> getContextId() {
		return Optional.ofNullable(contextId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration#getPath()
	 */
	@Override
	public Optional<String> getPath() {
		return Optional.ofNullable(path);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration#getConfigurationLocation()
	 */
	@Override
	public Optional<String> getConfigurationLocation() {
		return Optional.ofNullable(configurationLocation);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration#getConfiguration()
	 */
	@Override
	public Optional<C> getConfiguration() {
		return Optional.ofNullable(configuration);
	}

	/**
	 * Set the ClassLoader to use.
	 * @param classLoader the ClassLoader to set
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Set the JAX-RS Application.
	 * @param application the application to set
	 */
	public void setApplication(Application application) {
		this.application = application;
	}

	/**
	 * Set the root packages for API resources scan.
	 * @param rootResourcePackages the root packages to set
	 */
	public void setRootResourcePackages(Set<String> rootResourcePackages) {
		this.rootResourcePackages = rootResourcePackages;
	}

	/**
	 * Set the API endpoint type.
	 * @param type the API endpoint type to set
	 */
	public void setType(ApiEndpointType type) {
		this.type = type;
	}

	/**
	 * Set the API scanner type.
	 * @param scannerType the scanner type to set
	 */
	public void setScannerType(JaxrsScannerType scannerType) {
		this.scannerType = scannerType;
	}

	/**
	 * Set the API context id.
	 * @param contextId the context id to set
	 */
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	/**
	 * Set the API endpoint path.
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Set the API configuration location.
	 * @param configurationLocation the configuration location to set
	 */
	public void setConfigurationLocation(String configurationLocation) {
		this.configurationLocation = configurationLocation;
	}

	/**
	 * Set the API configuration.
	 * @param configuration the API configuration to set
	 */
	public void setConfiguration(C configuration) {
		this.configuration = configuration;
	}

	public static class DefaultBuilder<C> implements Builder<C> {

		private final DefaultApiEndpointConfiguration<C> instance;

		public DefaultBuilder() {
			super();
			this.instance = new DefaultApiEndpointConfiguration<>();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration.Builder#contextId(java.lang.String)
		 */
		@Override
		public Builder<C> contextId(String contextId) {
			this.instance.setContextId(contextId);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration.Builder#path(java.lang.String)
		 */
		@Override
		public Builder<C> path(String path) {
			this.instance.setPath(path);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration.Builder#classLoader(java.lang.ClassLoader)
		 */
		@Override
		public Builder<C> classLoader(ClassLoader classLoader) {
			this.instance.setClassLoader(classLoader);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration.Builder#type(com.holonplatform.jaxrs.swagger.
		 * ApiEndpointType)
		 */
		@Override
		public Builder<C> type(ApiEndpointType type) {
			this.instance.setType(type);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointConfiguration.Builder#scannerType(com.
		 * holonplatform.jaxrs.swagger.JaxrsScannerType)
		 */
		@Override
		public Builder<C> scannerType(JaxrsScannerType scannerType) {
			this.instance.setScannerType(scannerType);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration.Builder#application(jakarta.ws.rs.core.Application)
		 */
		@Override
		public Builder<C> application(Application application) {
			this.instance.setApplication(application);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.jaxrs.swagger.internal.endpoints.ApiEndpointConfiguration.Builder#rootResourcePackages(
		 * java. lang.String)
		 */
		@Override
		public Builder<C> rootResourcePackages(Set<String> rootResourcePackages) {
			this.instance.setRootResourcePackages(rootResourcePackages);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration.Builder#configurationLocation(java.lang.String)
		 */
		@Override
		public Builder<C> configurationLocation(String configurationLocation) {
			this.instance.setConfigurationLocation(configurationLocation);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration.Builder#configuration(java.lang.Object)
		 */
		@Override
		public Builder<C> configuration(C configuration) {
			this.instance.setConfiguration(configuration);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.jaxrs.swagger.ApiEndpointConfiguration.Builder#build()
		 */
		@Override
		public ApiEndpointConfiguration<C> build() {
			return instance;
		}

	}

}
