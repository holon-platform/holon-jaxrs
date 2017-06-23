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
 * Resteasy Spring Boot configuration properties.
 * 
 * @since 5.0.0
 */
@ConfigurationProperties(prefix = "holon.resteasy")
public class ResteasyConfigurationProperties {

	/**
	 * Reasteasy integration type.
	 */
	private Type type = Type.SERVLET;

	/**
	 * JAX-RS Application path. Overrides the value of "@ApplicationPath" if specified.
	 */
	private String applicationPath;

	/**
	 * Servlet context init parameters.
	 */
	private Map<String, String> init = new HashMap<>();

	private final Filter filter = new Filter();

	private final Servlet servlet = new Servlet();

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

	public Filter getFilter() {
		return this.filter;
	}

	public Servlet getServlet() {
		return this.servlet;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public enum Type {

		SERVLET, FILTER

	}

	public static class Filter {

		/**
		 * Reasteasy filter chain order.
		 */
		private int order;

		public int getOrder() {
			return this.order;
		}

		public void setOrder(int order) {
			this.order = order;
		}

	}

	public static class Servlet {

		/**
		 * Load on startup priority of the Resteasy servlet.
		 */
		private int loadOnStartup = -1;

		public int getLoadOnStartup() {
			return this.loadOnStartup;
		}

		public void setLoadOnStartup(int loadOnStartup) {
			this.loadOnStartup = loadOnStartup;
		}

	}

}
