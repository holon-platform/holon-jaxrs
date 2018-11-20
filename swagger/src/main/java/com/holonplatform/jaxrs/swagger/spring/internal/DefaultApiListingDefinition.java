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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
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
		ObjectUtils.argumentNotNull(groupId, "Group id must be not null");
		this.groupId = groupId;
	}

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
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getGroupId()
	 */
	@Override
	public String getGroupId() {
		return groupId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getResourcePackage()
	 */
	@Override
	public Optional<String> getResourcePackage() {
		return Optional.ofNullable(resourcePackage);
	}

	public void setResourcePackage(String resourcePackage) {
		this.resourcePackage = resourcePackage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getClassesToScan()
	 */
	@Override
	public Set<Class<?>> getClassesToScan() {
		return classesToScan;
	}

	public void setClassesToScan(Set<Class<?>> classesToScan) {
		this.classesToScan = (classesToScan == null) ? Collections.emptySet() : classesToScan;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getPath()
	 */
	@Override
	public Optional<String> getPath() {
		return Optional.ofNullable(path);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getEndpointPath()
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

	public void setPath(String path) {
		this.path = (path != null && path.trim().equals("")) ? null : path;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getSchemes()
	 */
	@Override
	public String[] getSchemes() {
		return schemes;
	}

	public void setSchemes(String[] schemes) {
		this.schemes = schemes;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getVersion()
	 */
	@Override
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getTermsOfServiceUrl()
	 */
	@Override
	public String getTermsOfServiceUrl() {
		return termsOfServiceUrl;
	}

	public void setTermsOfServiceUrl(String termsOfServiceUrl) {
		this.termsOfServiceUrl = termsOfServiceUrl;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getContact()
	 */
	@Override
	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getLicense()
	 */
	@Override
	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getLicenseUrl()
	 */
	@Override
	public String getLicenseUrl() {
		return licenseUrl;
	}

	public void setLicenseUrl(String licenseUrl) {
		this.licenseUrl = licenseUrl;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getHost()
	 */
	@Override
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#isPrettyPrint()
	 */
	@Override
	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getAuthSchemes()
	 */
	@Override
	public String[] getAuthSchemes() {
		return authSchemes;
	}

	public void setAuthSchemes(String[] authSchemes) {
		this.authSchemes = authSchemes;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getSecurityRoles()
	 */
	@Override
	public String[] getSecurityRoles() {
		return securityRoles;
	}

	public void setSecurityRoles(String[] securityRoles) {
		this.securityRoles = securityRoles;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.spring.internal.Todo#isMergeable(com.holonplatform.jaxrs.swagger.spring.internal.
	 * ApiListingDefinition)
	 */
	@Override
	public Optional<ApiListingDefinition> isMergeable(ApiListingDefinition definition) {
		if (definition != null) {

			if (this == definition) {
				return Optional.empty();
			}

			if (getEndpointPath().equals(definition.getEndpointPath())) {
				if (getResourcePackage().isPresent()) {
					if (!definition.getResourcePackage().isPresent()
							|| !getResourcePackage().get().equals(definition.getResourcePackage().orElse(""))) {
						return Optional.empty();
					}
				}
				if (!getResourcePackage().isPresent() && definition.getResourcePackage().isPresent()) {
					return Optional.empty();
				}
				if (getTitle() != null && !Objects.equals(getTitle(), definition.getTitle())) {
					return Optional.empty();
				}
				if (getDescription() != null && !Objects.equals(getDescription(), definition.getDescription())) {
					return Optional.empty();
				}
				if (getVersion() != null && !Objects.equals(getVersion(), definition.getVersion())) {
					return Optional.empty();
				}
				if (getTermsOfServiceUrl() != null
						&& !Objects.equals(getTermsOfServiceUrl(), definition.getTermsOfServiceUrl())) {
					return Optional.empty();
				}
				if (getContact() != null && !Objects.equals(getContact(), definition.getContact())) {
					return Optional.empty();
				}
				if (getLicenseUrl() != null && !Objects.equals(getLicense(), definition.getLicense())) {
					return Optional.empty();
				}
				if (getLicenseUrl() != null && !Objects.equals(getLicenseUrl(), definition.getLicenseUrl())) {
					return Optional.empty();
				}
				if (getHost() != null && !Objects.equals(getHost(), definition.getHost())) {
					return Optional.empty();
				}
				if (getSchemes() != null && !arrayEquals(getSchemes(), definition.getSchemes())) {
					return Optional.empty();
				}
				if (getAuthSchemes() != null && !arrayEquals(getAuthSchemes(), definition.getAuthSchemes())) {
					return Optional.empty();
				}
				if (getSecurityRoles() != null && !arrayEquals(getSecurityRoles(), definition.getSecurityRoles())) {
					return Optional.empty();
				}

				// merge
				DefaultApiListingDefinition merged = new DefaultApiListingDefinition(getGroupId());
				if (getResourcePackage().isPresent()) {
					merged.setResourcePackage(getResourcePackage().get());
				} else {
					Set<Class<?>> classes = new HashSet<>();
					classes.addAll(getClassesToScan());
					classes.addAll(definition.getClassesToScan());
					merged.setClassesToScan(classes);
				}
				merged.setPath(getPath().orElse(null));
				merged.setTitle(getTitle());
				merged.setSchemes(getSchemes());
				merged.setVersion(getVersion());
				merged.setDescription(getDescription());
				merged.setTermsOfServiceUrl(getTermsOfServiceUrl());
				merged.setContact(getContact());
				merged.setLicense(getLicense());
				merged.setLicenseUrl(getLicenseUrl());
				merged.setHost(getHost());
				merged.setAuthSchemes(getAuthSchemes());
				merged.setSecurityRoles(getSecurityRoles());
				if (isPrettyPrint() || definition.isPrettyPrint()) {
					merged.setPrettyPrint(true);
				}
				return Optional.of(merged);
			}
		}
		return Optional.empty();
	}

	private static boolean arrayEquals(String[] a, String[] b) {
		if (a == null && b == null) {
			return true;
		}
		if (a == null && b != null) {
			return false;
		}
		if (a != null && b == null) {
			return false;
		}
		if (a != null && b != null) {
			if (a.length != b.length) {
				return false;
			}
			return Arrays.equals(a, b);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#configureEndpoint(java.lang.ClassLoader,
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
