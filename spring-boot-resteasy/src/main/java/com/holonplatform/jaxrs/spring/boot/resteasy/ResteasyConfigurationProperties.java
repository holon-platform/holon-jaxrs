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
package com.holonplatform.jaxrs.spring.boot.resteasy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TODO
 */
@ConfigurationProperties(prefix = "holon.resteasy")
public class ResteasyConfigurationProperties {

	/**
	 * JAX-RS Application path
	 */
	private String applicationPath;

	/**
	 * Init parameters to pass to Resteasy servlet.
	 */
	private Map<String, String> init = new HashMap<>();

	/**
	 * Load on startup priority of the Resteasy servlet.
	 */
	private int loadOnStartup = 1;

	public String getApplicationPath() {
		return applicationPath;
	}

	public void setApplicationPath(String applicationPath) {
		this.applicationPath = applicationPath;
	}

	public Map<String, String> getInit() {
		return init;
	}

	public void setInit(Map<String, String> init) {
		this.init = init;
	}

	public int getLoadOnStartup() {
		return loadOnStartup;
	}

	public void setLoadOnStartup(int loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

}
