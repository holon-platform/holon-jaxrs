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
package com.holonplatform.jaxrs.swagger.v2.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import io.swagger.config.SwaggerConfig;
import io.swagger.jaxrs.config.BeanConfig;

/**
 * Default {@link SwaggerConfiguration} implementation.
 *
 */
public class DefaultSwaggerConfiguration extends BeanConfig implements SwaggerConfiguration {

	private String readerClass;

	private String scannerClass;

	private Collection<String> ignoredRoutes;

	private boolean readAllResources = true;

	/**
	 * Default constructor.
	 */
	public DefaultSwaggerConfiguration() {
		super();
	}

	/**
	 * Constructor.
	 * @param parent The paretn configuration from which to inherit the configuration attributes
	 */
	public DefaultSwaggerConfiguration(SwaggerConfig parent) {
		super();
		if (parent != null) {
			setFilterClass(parent.getFilterClass());
			if (parent instanceof BeanConfig) {
				merge((BeanConfig) parent);
			}
		}
	}

	private void merge(BeanConfig config) {
		setResourcePackage(config.getResourcePackage());
		setSchemes(config.getSchemes());
		setInfo(config.getInfo());
		setTitle(config.getTitle());
		setVersion(config.getVersion());
		setTermsOfServiceUrl(config.getTermsOfServiceUrl());
		setContact(config.getContact());
		setLicense(config.getLicense());
		setLicenseUrl(config.getLicenseUrl());
		setFilterClass(config.getFilterClass());
		setHost(config.getHost());
		setBasePath(config.getBasePath());
		setContextId(config.getContextId());
		setConfigId(config.getConfigId());
		setScannerId(config.getScannerId());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration#getResourcePackages()
	 */
	@Override
	public Set<String> getResourcePackages() {
		String resourcePackage = getResourcePackage();
		if (resourcePackage != null && !"".equals(resourcePackage)) {
			return Arrays.asList(resourcePackage.split(",")).stream().filter(p -> p != null && !p.trim().equals(""))
					.collect(Collectors.toSet());
		}
		return Collections.emptySet();
	}

	/**
	 * Set the package names to use to filter the API resource classes.
	 * @param resourcePackages the package names to set
	 */
	@Override
	public void setResourcePackages(Set<String> resourcePackages) {
		if (resourcePackages == null) {
			setResourcePackage(null);
		} else {
			setResourcePackage(resourcePackages.stream().collect(Collectors.joining(",")));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration#getReaderClass()
	 */
	@Override
	public String getReaderClass() {
		return readerClass;
	}

	/**
	 * Set the API reader class.
	 * @param readerClass the reader class to set
	 */
	public void setReaderClass(String readerClass) {
		this.readerClass = readerClass;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration#getScannerClass()
	 */
	@Override
	public String getScannerClass() {
		return scannerClass;
	}

	/**
	 * Set the API scanner class.
	 * @param scannerClass the scanner class to set
	 */
	public void setScannerClass(String scannerClass) {
		this.scannerClass = scannerClass;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration#getIgnoredRoutes()
	 */
	@Override
	public Collection<String> getIgnoredRoutes() {
		return ignoredRoutes;
	}

	/**
	 * Set the API definition routes to ignore.
	 * @param ignoredRoutes the ignored routes to set
	 */
	public void setIgnoredRoutes(Collection<String> ignoredRoutes) {
		this.ignoredRoutes = ignoredRoutes;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration#isReadAllResources()
	 */
	@Override
	public boolean isReadAllResources() {
		return readAllResources;
	}

	/**
	 * Set whether to read all the API resource classes, diregarding the Api annotation.
	 * @param readAllResources whether to read all the API resource classes
	 */
	public void setReadAllResources(boolean readAllResources) {
		this.readAllResources = readAllResources;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration#isPrettyPrint()
	 */
	@Override
	public boolean isPrettyPrint() {
		return super.getPrettyPrint();
	}

	/**
	 * Set whether to pretty print the API output.
	 * @param prettyPrint whether to pretty print the API output
	 */
	@Override
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

}
