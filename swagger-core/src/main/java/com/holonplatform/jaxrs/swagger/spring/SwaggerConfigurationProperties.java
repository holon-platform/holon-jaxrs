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

import com.holonplatform.jaxrs.swagger.ApiContext;
import com.holonplatform.jaxrs.swagger.ApiEndpointType;

/**
 * Configuration properties for Swagger auto configuration.
 *
 * @since 5.0.0
 *
 * @see <a href=
 *      "http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html">http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html</a>
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
	 * The package names to use to filter the API resource classes.
	 * <p>
	 * To specify more than one package name, a comma (<code>,</code>) separator con be used
	 * </p>
	 */
	private String resourcePackage;

	/**
	 * The API listing endpoint path.
	 * <p>
	 * Default is {@link ApiContext#DEFAULT_API_ENDPOINT_PATH}.
	 * </p>
	 */
	private String path;

	/**
	 * The API endpoint type.
	 * <p>
	 * Must be one of the {@link ApiEndpointType} enumeration values.
	 * </p>
	 * @since 5.2.0
	 */
	private ApiEndpointType type;

	/**
	 * The supported API protocol schemes (e.g. <code>https</code>).
	 */
	private String[] schemes;

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
	 * <p>
	 * This configuration property is used with OpenAPI v3 only.
	 * </p>
	 */
	private String contact;

	/**
	 * The API contact email.
	 * <p>
	 * This configuration property is used with OpenAPI v3 only.
	 * </p>
	 */
	private String contactEmail;

	/**
	 * The API contact URL.
	 * <p>
	 * For OpenAPI v3, this represents the contact name.
	 * </p>
	 */
	private String contactUrl;

	/**
	 * The API license information.
	 */
	private String license;

	/**
	 * The API license URL.
	 */
	private String licenseUrl;

	/**
	 * The API host.
	 */
	private String host;

	/**
	 * Whether to <em>pretty</em> format the API listing output.
	 */
	private boolean prettyPrint = true;

	/**
	 * The API definition groups.
	 */
	private final List<ApiGroupConfiguration> apiGroups = new LinkedList<>();

	// ------- getters and setters

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
	public ApiEndpointType getType() {
		return type;
	}

	public void setType(ApiEndpointType type) {
		this.type = type;
	}

	@Override
	public String[] getSchemes() {
		return schemes;
	}

	public void setSchemes(String[] schemes) {
		this.schemes = schemes;
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

	@Override
	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Override
	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	@Override
	public String getContactUrl() {
		return contactUrl;
	}

	public void setContactUrl(String contactUrl) {
		this.contactUrl = contactUrl;
	}

	@Override
	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	@Override
	public String getLicenseUrl() {
		return licenseUrl;
	}

	public void setLicenseUrl(String licenseUrl) {
		this.licenseUrl = licenseUrl;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
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
		 * The package names to use to filter the API resource classes.
		 * <p>
		 * To specify more than one package name, a comma (<code>,</code>) separator con be used
		 * </p>
		 */
		private String resourcePackage;

		/**
		 * The API listing endpoint path.
		 * <p>
		 * Default is {@link ApiContext#DEFAULT_API_ENDPOINT_PATH}.
		 * </p>
		 */
		private String path;

		/**
		 * The API endpoint type.
		 * <p>
		 * Must be one of the {@link ApiEndpointType} enumeration values.
		 * </p>
		 * @since 5.2.0
		 */
		private ApiEndpointType type;

		/**
		 * The supported API protocol schemes (e.g. <code>https</code>).
		 */
		private String[] schemes;

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
		 * <p>
		 * This configuration property is used with OpenAPI v3 only.
		 * </p>
		 */
		private String contact;

		/**
		 * The API contact email.
		 * <p>
		 * This configuration property is used with OpenAPI v3 only.
		 * </p>
		 */
		private String contactEmail;

		/**
		 * The API contact URL.
		 * <p>
		 * For OpenAPI v3, this represents the contact name.
		 * </p>
		 */
		private String contactUrl;

		/**
		 * The API license information.
		 */
		private String license;

		/**
		 * The API license URL.
		 */
		private String licenseUrl;

		/**
		 * Whether to <em>pretty</em> format the API listing output.
		 */
		private boolean prettyPrint = true;

		// ------- getters and setters

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
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
		public ApiEndpointType getType() {
			return type;
		}

		public void setType(ApiEndpointType type) {
			this.type = type;
		}

		@Override
		public String[] getSchemes() {
			return schemes;
		}

		public void setSchemes(String[] schemes) {
			this.schemes = schemes;
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

		@Override
		public String getContact() {
			return contact;
		}

		public void setContact(String contact) {
			this.contact = contact;
		}

		@Override
		public String getContactEmail() {
			return contactEmail;
		}

		public void setContactEmail(String contactEmail) {
			this.contactEmail = contactEmail;
		}

		@Override
		public String getContactUrl() {
			return contactUrl;
		}

		public void setContactUrl(String contactUrl) {
			this.contactUrl = contactUrl;
		}

		@Override
		public String getLicense() {
			return license;
		}

		public void setLicense(String license) {
			this.license = license;
		}

		@Override
		public String getLicenseUrl() {
			return licenseUrl;
		}

		public void setLicenseUrl(String licenseUrl) {
			this.licenseUrl = licenseUrl;
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
