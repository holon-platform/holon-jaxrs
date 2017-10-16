/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.jaxrs.spring.boot.jersey;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Jersey auto configuration.
 *
 * @since 5.0.0
 */
@ConfigurationProperties(prefix = "holon.jersey")
public class JerseyConfigurationProperties {

	private boolean forwardOn404;

	/**
	 * Get the value of the <code>jersey.config.servlet.filter.forwardOn404</code> property.
	 * @return the property value
	 */
	public boolean isForwardOn404() {
		return forwardOn404;
	}

	/**
	 * Set the value for the <code>jersey.config.servlet.filter.forwardOn404</code> property.
	 * @param forwardOn404 the property value to set
	 */
	public void setForwardOn404(boolean forwardOn404) {
		this.forwardOn404 = forwardOn404;
	}

}
