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
package com.holonplatform.jaxrs.swagger.spring;

import java.util.LinkedList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.holonplatform.jaxrs.swagger.ApiDefaults;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;
import com.holonplatform.jaxrs.swagger.JaxrsScannerType;

/**
 * Configuration properties for Swagger auto configuration.
 *
 * @since 5.0.0
 */
@ConfigurationProperties(prefix = "holon.swagger")
public class SwaggerConfigurationProperties implements ApiConfigurationProperties {

	/**
	 * Whether to enable the API listing endpoints configuration.
	 * <p>
	 * Defaults to <code>true</code>.
	 * </p>
	 */
	private boolean enabled = true;

	/**
	 * Whether to include all API resources or to include only the resources with a suitable API definition annotation.
	 * <p>
	 * Default is <code>true</code>.
	 * </p>
	 * <p>
	 * If <code>false</code>: For Swagger V2, only the <code>io.swagger.annotations.Api</code> annotated
	 * resource classes will be included. For Swagger/OpenAPI V3, only the
	 * <code>io.swagger.v3.oas.annotations.Operation</code> annotated resource methods will be included.
	 * </p>
	 */
	private boolean includeAll = true;

	/**
	 * The API operation routes to ignore for API definition generation.
	 */
	private List<String> ignoredRoutes;

	/**
	 * The package names to use to filter the API resource classes.
	 * <p>
	 * To specify more than one package name, a comma (<code>,</code>) separator con be used
	 * </p>
	 */
	private String resourcePackage;

	/**
	 * The API listing endpoint path.
	 * <p>
	 * Default is {@link ApiDefaults#DEFAULT_API_ENDPOINT_PATH}.
	 * </p>
	 */
	private String path;

	/**
	 * The Swagger V2 API specific configuration.
	 * <p>
	 * If a {@link Version#path} is configured and the Swagger V2 auto-configuration is enabled, overrides the default
	 * {@link #path} configuration.
	 * </p>
	 */
	private Version v2;

	/**
	 * The Swagger/OpenAPI V3 API specific configuration.
	 * <p>
	 * If a {@link Version#path} is and the Swagger/OpenAPI V3 auto-configuration is enabled, overrides the default
	 * {@link #path} configuration.
	 * </p>
	 */
	private Version v3;

	/**
	 * The API endpoint type.
	 * <p>
	 * Must be one of the {@link ApiEndpointType} enumeration values.
	 * </p>
	 * @since 5.2.0
	 */
	private ApiEndpointType type;

	/**
	 * The API resource classes scanner type. By default the {@link JaxrsScannerType#DEFAULT} is used.
	 * <p>
	 * Must be one of the {@link JaxrsScannerType} enumeration values.
	 * </p>
	 * @since 5.2.0
	 */
	private JaxrsScannerType scannerType;

	/**
	 * The API context id to use.
	 * <p>
	 * If not specified, the {@link ApiDefaults#DEFAULT_CONTEXT_ID} default context id will be used.
	 * </p>
	 * @since 5.2.0
	 */
	private String contextId;

	/**
	 * The API title.
	 */
	private String title;

	/**
	 * The API version.
	 */
	private String version;

	/**
	 * The API description.
	 */
	private String description;

	/**
	 * The API Terms of Service URL.
	 */
	private String termsOfServiceUrl;

	/**
	 * The API contact information.
	 * 
	 * @since 5.2.0
	 */
	private Contact contact;

	/**
	 * The API license information.
	 * 
	 * @since 5.2.0
	 */
	private License license;

	/**
	 * The API license URL.
	 * @deprecated Use {@link #license}
	 */
	@Deprecated
	private String licenseUrl;

	/**
	 * External documentation information.
	 * 
	 * @since 5.2.0
	 */
	private ExternalDocs externalDocs;

	/**
	 * The supported API protocol schemes (e.g. <code>https</code>).
	 * @deprecated Use {@link #server}
	 */
	@Deprecated
	private String[] schemes;

	/**
	 * The API host.
	 * @deprecated Use {@link #server}
	 */
	@Deprecated
	private String host;

	/**
	 * The API server configuration
	 */
	private Server server;

	/**
	 * Whether to <em>pretty</em> format the API listing output.
	 */
	private boolean prettyPrint = true;

	/**
	 * The API definition groups.
	 */
	private final List<ApiGroupConfiguration> apiGroups = new LinkedList<>();

	// ------- getters and setters

	@Override
	public boolean isGroupConfiguration() {
		return false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isIncludeAll() {
		return includeAll;
	}

	public void setIncludeAll(boolean includeAll) {
		this.includeAll = includeAll;
	}

	@Override
	public List<String> getIgnoredRoutes() {
		return ignoredRoutes;
	}

	public void setIgnoredRoutes(List<String> ignoredRoutes) {
		this.ignoredRoutes = ignoredRoutes;
	}

	@Override
	public String getResourcePackage() {
		return resourcePackage;
	}

	public void setResourcePackage(String resourcePackage) {
		this.resourcePackage = resourcePackage;
	}

	@Override
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public Version getV2() {
		return v2;
	}

	public void setV2(Version v2) {
		this.v2 = v2;
	}

	@Override
	public Version getV3() {
		return v3;
	}

	public void setV3(Version v3) {
		this.v3 = v3;
	}

	@Override
	public ApiEndpointType getType() {
		return type;
	}

	public void setType(ApiEndpointType type) {
		this.type = type;
	}

	@Override
	public JaxrsScannerType getScannerType() {
		return scannerType;
	}

	public void setScannerType(JaxrsScannerType scannerType) {
		this.scannerType = scannerType;
	}

	@Override
	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getTermsOfServiceUrl() {
		return termsOfServiceUrl;
	}

	public void setTermsOfServiceUrl(String termsOfServiceUrl) {
		this.termsOfServiceUrl = termsOfServiceUrl;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	public void setLicenseUrl(String licenseUrl) {
		this.licenseUrl = licenseUrl;
	}

	public ExternalDocs getExternalDocs() {
		return externalDocs;
	}

	public void setExternalDocs(ExternalDocs externalDocs) {
		this.externalDocs = externalDocs;
	}

	@Override
	public String getContactName() {
		if (getContact() != null) {
			return getContact().getName();
		}
		return null;
	}

	@Override
	public String getContactEmail() {
		if (getContact() != null) {
			return getContact().getEmail();
		}
		return null;
	}

	@Override
	public String getContactUrl() {
		if (getContact() != null) {
			return getContact().getUrl();
		}
		return null;
	}

	@Override
	public String getLicenseName() {
		if (getLicense() != null) {
			return getLicense().getName();
		}
		return null;
	}

	@Override
	public String getLicenseUrl() {
		if (licenseUrl != null) {
			return licenseUrl;
		}
		if (getLicense() != null) {
			return getLicense().getUrl();
		}
		return null;
	}

	@Override
	public String getExternalDocsUrl() {
		if (getExternalDocs() != null) {
			return getExternalDocs().getUrl();
		}
		return null;
	}

	@Override
	public String getExternalDocsDescription() {
		if (getExternalDocs() != null) {
			return getExternalDocs().getDescription();
		}
		return null;
	}

	public String[] getSchemes() {
		return schemes;
	}

	public void setSchemes(String[] schemes) {
		this.schemes = schemes;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	@Override
	public String getServerUrl() {
		if (getServer() != null) {
			return getServer().getUrl();
		}
		if (getHost() != null) {
			if (getSchemes() != null && getSchemes().length > 0) {
				return getSchemes()[0] + "://" + getHost();
			}
			return getHost();
		}
		return null;
	}

	@Override
	public String getServerDescription() {
		if (getServer() != null) {
			return getServer().getDescription();
		}
		return null;
	}

	@Override
	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public List<ApiGroupConfiguration> getApiGroups() {
		return apiGroups;
	}

	// ------- sub classes

	/**
	 * API version specific configuration.
	 * 
	 * @since 5.2.0
	 */
	public static class Version {

		/**
		 * The API endpoint path.
		 */
		private String path;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

	}

	/**
	 * API contact configuration.
	 * 
	 * @since 5.2.0
	 */
	public static class Contact {

		/**
		 * Contact name
		 */
		private String name;

		/**
		 * Contact email.
		 */
		private String email;

		/**
		 * Contact URL.
		 */
		private String url;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

	/**
	 * API license configuration.
	 * 
	 * @since 5.2.0
	 */
	public static class License {

		/**
		 * License name
		 */
		private String name;

		/**
		 * License URL.
		 */
		private String url;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

	/**
	 * API server configuration.
	 * 
	 * @since 5.2.0
	 */
	public static class Server {

		/**
		 * Server URL
		 */
		private String url;

		/**
		 * Server description
		 */
		private String description;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

	/**
	 * API external docs configuration.
	 * 
	 * @since 5.2.0
	 */
	public static class ExternalDocs {

		/**
		 * External documentation description.
		 */
		private String description;

		/**
		 * External documentation URL.
		 */
		private String url;

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

	/**
	 * API group configuration.
	 */
	public static class ApiGroupConfiguration implements ApiConfigurationProperties {

		/**
		 * The API group id.
		 * <p>
		 * This will be used as API context id.
		 * </p>
		 */
		private String groupId;

		/**
		 * Whether to include all API resources or to include only the resources with a suitable API definition
		 * annotation.
		 * <p>
		 * Default is <code>true</code>.
		 * </p>
		 * <p>
		 * If <code>false</code>: For Swagger V2, only the <code>io.swagger.annotations.ApiOperation</code> annotated
		 * resource methods will be incuded. For Swagger/OpenAPI V3, only the
		 * <code>io.swagger.v3.oas.annotations.Operation</code> annotated resource methods will be incuded.
		 * </p>
		 */
		private boolean includeAll = true;

		/**
		 * The API operation routes to ignore for API definition generation.
		 */
		private List<String> ignoredRoutes;

		/**
		 * The package names to use to filter the API resource classes.
		 * <p>
		 * To specify more than one package name, a comma (<code>,</code>) separator con be used
		 * </p>
		 */
		private String resourcePackage;

		/**
		 * The API listing endpoint path.
		 * <p>
		 * Default is {@link ApiDefaults#DEFAULT_API_ENDPOINT_PATH}.
		 * </p>
		 */
		private String path;

		/**
		 * The Swagger V2 API specific configuration.
		 * <p>
		 * If a {@link Version#path} is configured and the Swagger V2 auto-configuration is enabled, overrides the
		 * default {@link #path} configuration.
		 * </p>
		 */
		private Version v2;

		/**
		 * The Swagger/OpenAPI V3 API specific configuration.
		 * <p>
		 * If a {@link Version#path} is and the Swagger/OpenAPI V3 auto-configuration is enabled, overrides the default
		 * {@link #path} configuration.
		 * </p>
		 */
		private Version v3;

		/**
		 * The API endpoint type.
		 * <p>
		 * Must be one of the {@link ApiEndpointType} enumeration values.
		 * </p>
		 * @since 5.2.0
		 */
		private ApiEndpointType type;

		/**
		 * The API resource classes scanner type. By default the {@link JaxrsScannerType#DEFAULT} is used.
		 * <p>
		 * Must be one of the {@link JaxrsScannerType} enumeration values.
		 * </p>
		 * @since 5.2.0
		 */
		private JaxrsScannerType scannerType;

		/**
		 * The API title.
		 */
		private String title;

		/**
		 * The API version.
		 */
		private String version;

		/**
		 * The API description.
		 */
		private String description;

		/**
		 * The API Terms of Service URL.
		 */
		private String termsOfServiceUrl;

		/**
		 * The API contact information.
		 * 
		 * @since 5.2.0
		 */
		private Contact contact;

		/**
		 * The API license information.
		 * 
		 * @since 5.2.0
		 */
		private License license;

		/**
		 * External documentation information.
		 * 
		 * @since 5.2.0
		 */
		private ExternalDocs externalDocs;

		/**
		 * The API server configuration
		 */
		private Server server;

		/**
		 * Whether to <em>pretty</em> format the API listing output.
		 */
		private boolean prettyPrint = true;

		// ------- getters and setters

		@Override
		public boolean isGroupConfiguration() {
			return true;
		}

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		@Override
		public boolean isIncludeAll() {
			return includeAll;
		}

		public void setIncludeAll(boolean includeAll) {
			this.includeAll = includeAll;
		}

		@Override
		public List<String> getIgnoredRoutes() {
			return ignoredRoutes;
		}

		public void setIgnoredRoutes(List<String> ignoredRoutes) {
			this.ignoredRoutes = ignoredRoutes;
		}

		@Override
		public String getResourcePackage() {
			return resourcePackage;
		}

		public void setResourcePackage(String resourcePackage) {
			this.resourcePackage = resourcePackage;
		}

		@Override
		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		@Override
		public Version getV2() {
			return v2;
		}

		public void setV2(Version v2) {
			this.v2 = v2;
		}

		@Override
		public Version getV3() {
			return v3;
		}

		public void setV3(Version v3) {
			this.v3 = v3;
		}

		@Override
		public ApiEndpointType getType() {
			return type;
		}

		public void setType(ApiEndpointType type) {
			this.type = type;
		}

		@Override
		public JaxrsScannerType getScannerType() {
			return scannerType;
		}

		public void setScannerType(JaxrsScannerType scannerType) {
			this.scannerType = scannerType;
		}

		@Override
		public String getContextId() {
			return getGroupId();
		}

		@Override
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		@Override
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		@Override
		public String getTermsOfServiceUrl() {
			return termsOfServiceUrl;
		}

		public void setTermsOfServiceUrl(String termsOfServiceUrl) {
			this.termsOfServiceUrl = termsOfServiceUrl;
		}

		public Contact getContact() {
			return contact;
		}

		public void setContact(Contact contact) {
			this.contact = contact;
		}

		public License getLicense() {
			return license;
		}

		public void setLicense(License license) {
			this.license = license;
		}

		public ExternalDocs getExternalDocs() {
			return externalDocs;
		}

		public void setExternalDocs(ExternalDocs externalDocs) {
			this.externalDocs = externalDocs;
		}

		@Override
		public String getContactName() {
			if (getContact() != null) {
				return getContact().getName();
			}
			return null;
		}

		@Override
		public String getContactEmail() {
			if (getContact() != null) {
				return getContact().getEmail();
			}
			return null;
		}

		@Override
		public String getContactUrl() {
			if (getContact() != null) {
				return getContact().getUrl();
			}
			return null;
		}

		@Override
		public String getLicenseName() {
			if (getLicense() != null) {
				return getLicense().getName();
			}
			return null;
		}

		@Override
		public String getLicenseUrl() {
			if (getLicense() != null) {
				return getLicense().getUrl();
			}
			return null;
		}

		@Override
		public String getExternalDocsUrl() {
			if (getExternalDocs() != null) {
				return getExternalDocs().getUrl();
			}
			return null;
		}

		@Override
		public String getExternalDocsDescription() {
			if (getExternalDocs() != null) {
				return getExternalDocs().getDescription();
			}
			return null;
		}

		public Server getServer() {
			return server;
		}

		public void setServer(Server server) {
			this.server = server;
		}

		@Override
		public String getServerUrl() {
			if (getServer() != null) {
				return getServer().getUrl();
			}
			return null;
		}

		@Override
		public String getServerDescription() {
			if (getServer() != null) {
				return getServer().getDescription();
			}
			return null;
		}

		@Override
		public boolean isPrettyPrint() {
			return prettyPrint;
		}

		public void setPrettyPrint(boolean prettyPrint) {
			this.prettyPrint = prettyPrint;
		}

	}

}
