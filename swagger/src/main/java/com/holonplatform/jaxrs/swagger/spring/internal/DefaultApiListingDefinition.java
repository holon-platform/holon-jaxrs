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

import javax.ws.rs.Path;

import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.jaxrs.swagger.SwaggerConfiguration;
import com.holonplatform.jaxrs.swagger.annotations.ApiDefinition;
import com.holonplatform.jaxrs.swagger.exceptions.SwaggerConfigurationException;
import com.holonplatform.jaxrs.swagger.internal.ApiGroupId;
import com.holonplatform.jaxrs.swagger.internal.SwaggerApiListingResource;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties.ApiGroupConfiguration;

import io.swagger.jaxrs.config.SwaggerConfigLocator;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

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
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#getEndpointPath()
	 */
	@Override
	public String getPath() {
		if (path == null || path.trim().equals("")) {
			if (ApiGroupId.DEFAULT_GROUP_ID.equals(getGroupId())) {
				return ApiDefinition.DEFAULT_PATH;
			}
			return ApiDefinition.DEFAULT_PATH + "/" + getGroupId();
		}
		return path;
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
	 * @see com.holonplatform.jaxrs.swagger.spring.internal.Todo#configureEndpoint(java.lang.ClassLoader,
	 * java.lang.String)
	 */
	@Override
	public ApiListingEndpoint configureEndpoint(ClassLoader classLoader, String basePath) {

		if (!getResourcePackage().isPresent() && getClassesToScan().isEmpty()) {
			throw new SwaggerConfigurationException(
					"Invalid API listing definition: neither resource package nor classes to scan were configured");
		}

		final String apiListingPath = getPath();

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
		return new DefaultApiListingEndpoint(getGroupId(), apiListingPath,
				buildApiListingEndpoint(classLoader, getGroupId(), apiListingPath));
	}

	/**
	 * Build a Swagger API listing JAX-RS endpoint class, binding it to given <code>path</code> using standard JAX-RS
	 * {@link Path} annotation.
	 * @param classLoader ClassLoader to use to create the class proxy
	 * @param apiGroupId API group id (not null)
	 * @param path Endpoint path (not null)
	 * @param authSchemes Authenticatiob schemes
	 * @param rolesAllowed Optional security roles for endpoint authorization
	 * @return The Swagger API listing JAX-RS endpoint class proxy
	 */
	private static Class<?> buildApiListingEndpoint(ClassLoader classLoader, String apiGroupId, String path) {
		ObjectUtils.argumentNotNull(apiGroupId, "API group id must be not null");
		ObjectUtils.argumentNotNull(path, "Path must be not null");
		final ClassLoader cl = (classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader();
		DynamicType.Builder<SwaggerApiListingResource> builder = new ByteBuddy()
				.subclass(SwaggerApiListingResource.class)
				.annotateType(AnnotationDescription.Builder.ofType(Path.class).define("value", path).build())
				.annotateType(
						AnnotationDescription.Builder.ofType(ApiGroupId.class).define("value", apiGroupId).build());
		return builder.make().load(cl, ClassLoadingStrategy.Default.INJECTION).getLoaded();
	}

}
