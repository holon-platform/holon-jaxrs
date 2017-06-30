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

/**
 * Configuration properties for Swagger auto configuration.
 *
 * @since 5.0.0
 *
 * @see <a href=
 *      "http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html">http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html</a>
 */
@ConfigurationProperties(prefix = "holon.swagger")
public class SwaggerConfigurationProperties {

	public static final String DEFAULT_PATH = "/api-docs";

	/**
	 * The package name to scan to detect API endpoints. Ingored when at least {@link ApiGroupConfiguration} is
	 * present.
	 */
	private String resourcePackage;

	/**
	 * API listing common base path. This path will be used as base API listing group path if no specific group path
	 * specified.
	 */
	private String path;

	/**
	 * API supported protocol schemes (e.g. <code>https</code>). Can be overridden by the specific
	 * {@link ApiGroupConfiguration} property.
	 */
	private String[] schemes;

	/**
	 * Common API title. Can be overridden by the specific {@link ApiGroupConfiguration} property.
	 */
	private String title;

	/**
	 * Common API version. Can be overridden by the specific {@link ApiGroupConfiguration} property.
	 */
	private String version;

	/**
	 * Common API description. Can be overridden by the specific {@link ApiGroupConfiguration} property.
	 */
	private String description;

	/**
	 * Common API Terms of Service URL. Can be overridden by the specific {@link ApiGroupConfiguration} property.
	 */
	private String termsOfServiceUrl;

	/**
	 * Common API contact information. Can be overridden by the specific {@link ApiGroupConfiguration} property.
	 */
	private String contact;

	/**
	 * Common API license information. Can be overridden by the specific {@link ApiGroupConfiguration} property.
	 */
	private String license;

	/**
	 * Common API license URL. Can be overridden by the specific {@link ApiGroupConfiguration} property.
	 */
	private String licenseUrl;

	/**
	 * API host.
	 */
	private String host;

	/**
	 * Whether to <em>pretty</em> format API listing output.
	 */
	private boolean prettyPrint;
	
	/**
	 * Set of security roles to use for API listing resource access control
	 */
	private String[] securityRoles;

	private final List<ApiGroupConfiguration> apiGroups = new LinkedList<>();

	public String getResourcePackage() {
		return resourcePackage;
	}

	public void setResourcePackage(String resourcePackage) {
		this.resourcePackage = resourcePackage;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String[] getSchemes() {
		return schemes;
	}

	public void setSchemes(String[] schemes) {
		this.schemes = schemes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTermsOfServiceUrl() {
		return termsOfServiceUrl;
	}

	public void setTermsOfServiceUrl(String termsOfServiceUrl) {
		this.termsOfServiceUrl = termsOfServiceUrl;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

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

	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public String[] getSecurityRoles() {
		return securityRoles;
	}

	public void setSecurityRoles(String[] securityRoles) {
		this.securityRoles = securityRoles;
	}

	public List<ApiGroupConfiguration> getApiGroups() {
		return apiGroups;
	}

	/**
	 * API group configuration.
	 */
	public static class ApiGroupConfiguration {

		/**
		 * Group id
		 */
		private String groupId;

		/**
		 * The package name to scan to detect API group endpoints
		 */
		private String resourcePackage;

		/**
		 * API group listing path
		 */
		private String path;

		/**
		 * API group supported protocol schemes (e.g. <code>https</code>)
		 */
		private String[] schemes;

		/**
		 * API group title
		 */
		private String title;

		/**
		 * API group version
		 */
		private String version;

		/**
		 * API group description
		 */
		private String description;

		/**
		 * API group <em>Terms of Service</em> URL
		 */
		private String termsOfServiceUrl;

		/**
		 * API group contact information
		 */
		private String contact;

		/**
		 * API group license information
		 */
		private String license;

		/**
		 * API group license URL
		 */
		private String licenseUrl;
		
		/**
		 * Set of security roles to use for API listing resource access control
		 */
		private String[] securityRoles;

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		public String getResourcePackage() {
			return resourcePackage;
		}

		public void setResourcePackage(String resourcePackage) {
			this.resourcePackage = resourcePackage;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String[] getSchemes() {
			return schemes;
		}

		public void setSchemes(String[] schemes) {
			this.schemes = schemes;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getTermsOfServiceUrl() {
			return termsOfServiceUrl;
		}

		public void setTermsOfServiceUrl(String termsOfServiceUrl) {
			this.termsOfServiceUrl = termsOfServiceUrl;
		}

		public String getContact() {
			return contact;
		}

		public void setContact(String contact) {
			this.contact = contact;
		}

		public String getLicense() {
			return license;
		}

		public void setLicense(String license) {
			this.license = license;
		}

		public String getLicenseUrl() {
			return licenseUrl;
		}

		public void setLicenseUrl(String licenseUrl) {
			this.licenseUrl = licenseUrl;
		}

		public String[] getSecurityRoles() {
			return securityRoles;
		}

		public void setSecurityRoles(String[] securityRoles) {
			this.securityRoles = securityRoles;
		}

	}

}
