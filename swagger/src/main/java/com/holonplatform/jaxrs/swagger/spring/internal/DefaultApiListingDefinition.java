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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.SwaggerConfiguration;
import com.holonplatform.jaxrs.swagger.internal.ApiGroupId;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationException;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties.ApiGroupConfiguration;

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
	private Set<Class<?>> classesToScan = Collections.emptySet();

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
	private String[] authSchemes;
	private String[] securityRoles;

	/**
	 * Constructor.
	 * @param groupId Group id (not null)
	 */
	public DefaultApiListingDefinition(String groupId) {
		super();
		this.groupId = groupId;
		ObjectUtils.argumentNotNull(groupId, "Group id must be not null");
	}

	/**
	 * Init the definition using given configuration properties.
	 * @param properties Optional configuration properties
	 * @param groupConfiguration Optional group configuration properties
	 */
	public void init(SwaggerConfigurationProperties properties, ApiGroupConfiguration groupConfiguration) {
		if (properties != null) {
			setResourcePackage(properties.getResourcePackage());
			setPath(properties.getPath());
			setSchemes(properties.getSchemes());
			setTitle(properties.getTitle());
			setDescription(properties.getDescription());
			setVersion(properties.getVersion());
			setTermsOfServiceUrl(properties.getTermsOfServiceUrl());
			setContact(properties.getContact());
			setLicense(properties.getLicense());
			setLicenseUrl(properties.getLicenseUrl());
			setHost(properties.getHost());
			setPrettyPrint(properties.isPrettyPrint());
			setAuthSchemes(properties.getAuthSchemes());
			setSecurityRoles(properties.getSecurityRoles());
		}
		if (groupConfiguration != null) {
			setResourcePackage(groupConfiguration.getResourcePackage());
			if (groupConfiguration.getPath() != null && !groupConfiguration.getPath().trim().equals("")) {
				setPath(groupConfiguration.getPath());
			}
			if (groupConfiguration.getSchemes() != null && groupConfiguration.getSchemes().length > 0) {
				setSchemes(groupConfiguration.getSchemes());
			}
			if (groupConfiguration.getTitle() != null) {
				setTitle(groupConfiguration.getTitle());
			}
			if (groupConfiguration.getDescription() != null) {
				setDescription(groupConfiguration.getDescription());
			}
			if (groupConfiguration.getVersion() != null) {
				setVersion(groupConfiguration.getVersion());
			}
			if (groupConfiguration.getTermsOfServiceUrl() != null) {
				setTermsOfServiceUrl(groupConfiguration.getTermsOfServiceUrl());
			}
			if (groupConfiguration.getContact() != null) {
				setContact(groupConfiguration.getContact());
			}
			if (groupConfiguration.getLicense() != null) {
				setLicense(groupConfiguration.getLicense());
			}
			if (groupConfiguration.getLicenseUrl() != null) {
				setLicenseUrl(groupConfiguration.getLicenseUrl());
			}
			if (groupConfiguration.getAuthSchemes() != null && groupConfiguration.getAuthSchemes().length > 0) {
				setAuthSchemes(groupConfiguration.getAuthSchemes());
			}
			if (groupConfiguration.getSecurityRoles() != null && groupConfiguration.getSecurityRoles().length > 0) {
				setSecurityRoles(groupConfiguration.getSecurityRoles());
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
	 * @return Optional package name to scan to detect API endpoints
	 */
	public Optional<String> getResourcePackage() {
		return Optional.ofNullable(resourcePackage);
	}

	/**
	 * Get the package name to scan to detect API endpoints.
	 * @param resourcePackage the resourcePackage the package name to scan to detect API endpoints
	 */
	public void setResourcePackage(String resourcePackage) {
		this.resourcePackage = resourcePackage;
	}

	/**
	 * Get the classes to scan.
	 * @return the classes to scan, empty if none
	 */
	public Set<Class<?>> getClassesToScan() {
		return classesToScan;
	}

	/**
	 * Set the classes to scan.
	 * <p>
	 * If explicit classes to scan are set, the resource package will be ignored.
	 * </p>
	 * @param classesToScan the classes to scan set
	 */
	public void setClassesToScan(Set<Class<?>> classesToScan) {
		this.classesToScan = (classesToScan == null) ? Collections.emptySet() : classesToScan;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.ApiListingDefinition#getPath()
	 */
	@Override
	public Optional<String> getPath() {
		return Optional.ofNullable(path);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.ApiListingDefinition#getEndpointPath()
	 */
	@Override
	public String getEndpointPath() {
		return getPath().orElseGet(() -> {
			if (ApiGroupId.DEFAULT_GROUP_ID.equals(getGroupId())) {
				return SwaggerConfigurationProperties.DEFAULT_PATH;
			}
			return SwaggerConfigurationProperties.DEFAULT_PATH + "/" + getGroupId();
		});
	}

	/**
	 * Set the API listing path
	 * @param path the API listing path to set
	 */
	public void setPath(String path) {
		this.path = (path != null && path.trim().equals("")) ? null : path;
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

	/**
	 * Get the authentication schemes to use for API listing endpoint protection.
	 * @return the authentication scheme names, if only one <code>*</code> scheme is provided, any supported
	 *         authentication scheme is allowed
	 */
	public String[] getAuthSchemes() {
		return authSchemes;
	}

	/**
	 * Set the authentication schemes to use for API listing endpoint protection.
	 * @param authSchemes the authentication scheme names to set, if only one <code>*</code> scheme is provided, any
	 *        supported authentication scheme is allowed
	 */
	public void setAuthSchemes(String[] authSchemes) {
		this.authSchemes = authSchemes;
	}

	/**
	 * Get the security roles to be used for endpoint access control
	 * @return the security roles
	 */
	public String[] getSecurityRoles() {
		return securityRoles;
	}

	/**
	 * Set the security roles to be used for endpoint access control
	 * @param securityRoles the security roles to set
	 */
	public void setSecurityRoles(String[] securityRoles) {
		this.securityRoles = securityRoles;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.spring.internal.ApiListingDefinition#configureEndpoints(java.lang.ClassLoader,
	 * java.lang.String)
	 */
	@Override
	public ApiListingEndpoint configureEndpoint(ClassLoader classLoader, String basePath) {

		if (!getResourcePackage().isPresent() && getClassesToScan().isEmpty()) {
			throw new SwaggerConfigurationException(
					"Invalid API listing definition: neither resource package nor classes to scan were configured");
		}

		final String apiListingPath = getEndpointPath();

		final SwaggerConfiguration swaggerCfg = getClassesToScan().isEmpty()
				? new SwaggerConfiguration(getResourcePackage().orElse(null))
				: new SwaggerConfiguration(getClassesToScan());
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
		swaggerCfg.setScan(true);

		// register the Swagger instance
		SwaggerConfigLocator.getInstance().putSwagger(getGroupId(), swaggerCfg.getSwagger());

		// return th endpoint implementation
		return new DefaultApiListingEndpoint(getGroupId(), apiListingPath, SwaggerJaxrsUtils.buildApiListingEndpoint(
				classLoader, getGroupId(), apiListingPath, getAuthSchemes(), getSecurityRoles()));
	}

}
