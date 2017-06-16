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
package com.holonplatform.jaxrs.swagger.spring.internal;

import java.util.LinkedList;
import java.util.List;

import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties.ApiGroupConfiguration;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.config.SwaggerConfigLocator;

/**
 * Default {@link ApiListingDefinition} implementation.
 * 
 * @since 5.0.0
 */
public class DefaultApiListingDefinition implements ApiListingDefinition {

	private static final long serialVersionUID = 7343286354265897257L;

	private final String groupId;
	private String resourcePackage;
	private String path;
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

	public DefaultApiListingDefinition(String groupId) {
		this(groupId, null);
	}

	public DefaultApiListingDefinition(String groupId, SwaggerConfigurationProperties properties) {
		super();
		this.groupId = (groupId != null && !groupId.trim().equals("")) ? groupId : ApiGroupId.DEFAULT_GROUP_ID;

		if (properties != null) {

			if (properties.getPath() != null && !properties.getPath().trim().equals("")) {
				setPath(properties.getPath());
			}
			if (properties.getSchemes() != null) {
				setSchemes(properties.getSchemes());
			}
			setTitle(properties.getTitle());
			setDescription(properties.getDescription());
			setVersion(properties.getVersion());
			setTermsOfServiceUrl(properties.getTermsOfServiceUrl());
			setContact(properties.getContact());
			setLicense(properties.getLicense());
			setLicenseUrl(properties.getLicenseUrl());
			setHost(properties.getHost());
			setPrettyPrint(properties.isPrettyPrint());

			ApiGroupConfiguration cfg = SwaggerJaxrsUtils.getApiGroup(properties, this.groupId);
			if (cfg != null) {
				setResourcePackage(cfg.getResourcePackage());
				if (cfg.getPath() != null && !cfg.getPath().trim().equals("")) {
					setPath(properties.getPath());
				}
				if (cfg.getSchemes() != null && cfg.getSchemes().length > 0) {
					setSchemes(properties.getSchemes());
				}
				if (cfg.getTitle() != null) {
					setTitle(properties.getTitle());
				}
				if (cfg.getDescription() != null) {
					setDescription(properties.getDescription());
				}
				if (cfg.getVersion() != null) {
					setVersion(properties.getVersion());
				}
				if (cfg.getTermsOfServiceUrl() != null) {
					setTermsOfServiceUrl(properties.getTermsOfServiceUrl());
				}
				if (cfg.getContact() != null) {
					setContact(properties.getContact());
				}
				if (cfg.getLicense() != null) {
					setLicense(properties.getLicense());
				}
				if (cfg.getLicenseUrl() != null) {
					setLicenseUrl(properties.getLicenseUrl());
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.ApiListingDefinition#getGroupId()
	 */
	@Override
	public String getGroupId() {
		return groupId;
	}

	/**
	 * Set the package name to scan to detect API endpoints.
	 * @return the resourcePackage the package name to scan to detect API endpoints
	 */
	public String getResourcePackage() {
		return resourcePackage;
	}

	/**
	 * Get the package name to scan to detect API endpoints.
	 * @param resourcePackage the resourcePackage the package name to scan to detect API endpoints
	 */
	public void setResourcePackage(String resourcePackage) {
		this.resourcePackage = resourcePackage;
	}

	/**
	 * Get the API listing path
	 * @return the API listing path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Set the API listing path
	 * @param path the API listing path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Get the supported protocol schemes
	 * @return the supported protocol schemes
	 */
	public String[] getSchemes() {
		return schemes;
	}

	/**
	 * Set the supported protocol schemes (e.g. <code>https</code>)
	 * @param schemes the supported protocol schemes to set
	 */
	public void setSchemes(String[] schemes) {
		this.schemes = schemes;
	}

	/**
	 * Get the API title
	 * @return the API title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the API title
	 * @param title the API title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the API version
	 * @return the API version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Set the API version
	 * @param version the API version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Get the API description
	 * @return the API description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the API description
	 * @param description the API description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the <em>Terms of service</em> URL
	 * @return the <em>Terms of service</em> URL
	 */
	public String getTermsOfServiceUrl() {
		return termsOfServiceUrl;
	}

	/**
	 * Set the <em>Terms of service</em> URL
	 * @param termsOfServiceUrl the <em>Terms of service</em> URL to set
	 */
	public void setTermsOfServiceUrl(String termsOfServiceUrl) {
		this.termsOfServiceUrl = termsOfServiceUrl;
	}

	/**
	 * Get the contact information
	 * @return the contact information
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * Set the contact information
	 * @param contact the contact information to set
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}

	/**
	 * Get the license information
	 * @return the license information
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * Set the license information
	 * @param license the license information to set
	 */
	public void setLicense(String license) {
		this.license = license;
	}

	/**
	 * Get the license URL
	 * @return the license URL
	 */
	public String getLicenseUrl() {
		return licenseUrl;
	}

	/**
	 * Set the license URL
	 * @param licenseUrl the license URL to set
	 */
	public void setLicenseUrl(String licenseUrl) {
		this.licenseUrl = licenseUrl;
	}

	/**
	 * Get the API host
	 * @return the API host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Set the API host
	 * @param host the API host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Get whether to <em>pretty</em> format the API listing output
	 * @return whether to <em>pretty</em> format the API listing output
	 */
	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	/**
	 * Set whether to <em>pretty</em> format the API listing output
	 * @param prettyPrint <code>true</code> to <em>pretty</em> format the API listing output
	 */
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.spring.internal.ApiListingDefinition#configureEndpoints(java.lang.ClassLoader,
	 * java.lang.String)
	 */
	@Override
	public List<ApiListingEndpoint> configureEndpoints(ClassLoader classLoader, String basePath) {

		final List<ApiListingEndpoint> endpoints = new LinkedList<>();

		String apiListingPath = getPath();
		String defaultApiListingPath = null;
		if (apiListingPath == null || apiListingPath.trim().equals("")) {
			// use default path with appended group id if no path specified
			apiListingPath = SwaggerConfigurationProperties.DEFAULT_PATH + "/" + getGroupId();
			if (ApiGroupId.DEFAULT_GROUP_ID.equals(getGroupId())) {
				defaultApiListingPath = SwaggerConfigurationProperties.DEFAULT_PATH;
			}
		}

		BeanConfig swaggerCfg = new BeanConfig();
		if (getResourcePackage() != null) {
			swaggerCfg.setResourcePackage(getResourcePackage());
		}
		swaggerCfg.setTitle(getTitle());
		swaggerCfg.setVersion(getVersion());
		swaggerCfg.setDescription(getDescription());
		swaggerCfg.setTermsOfServiceUrl(getTermsOfServiceUrl());
		swaggerCfg.setContact(getContact());
		swaggerCfg.setLicense(getLicense());
		swaggerCfg.setLicenseUrl(getLicenseUrl());

		swaggerCfg.setBasePath(basePath);
		swaggerCfg.setHost(getHost());

		if (getSchemes() != null && getSchemes().length > 0) {
			swaggerCfg.setSchemes(getSchemes());
		}
		swaggerCfg.setPrettyPrint(isPrettyPrint());

		swaggerCfg.setConfigId(getGroupId());
		swaggerCfg.setScannerId(getGroupId());
		swaggerCfg.setContextId(getGroupId());

		// scan
		swaggerCfg.setScan(true); // TODO lazy mode

		SwaggerConfigLocator.getInstance().putSwagger(getGroupId(), swaggerCfg.getSwagger());

		// API listing resource
		endpoints.add(new DefaultApiListingEndpoint(getGroupId(), apiListingPath,
				SwaggerJaxrsUtils.buildApiListingEndpoint(classLoader, getGroupId(), apiListingPath)));

		if (defaultApiListingPath != null) {
			endpoints.add(new DefaultApiListingEndpoint(getGroupId(), defaultApiListingPath,
					SwaggerJaxrsUtils.buildApiListingEndpoint(classLoader, getGroupId(), defaultApiListingPath)));
		}

		return endpoints;
	}

}
