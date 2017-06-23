/*
 * Copyright 2000-2016 Holon TDCN.
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
package com.holonplatform.jaxrs.server;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Utility class to install SLF4J JUL logging bridge.
 * 
 * <p>
 * JUL logging bridge is required to enable logging using sl4fj for projects which use java.util.logging, for example
 * Jersey.
 * </p>
 * 
 * <p>
 * To enable JUL logging bridge, {@link #setupLogging()} method must be called statically.
 * </p>
 * 
 * @since 5.0.0
 */
public final class LogConfig {

	/*
	 * Empty private constructor: this class is intended only to provide constants ad utility methods.
	 */
	private LogConfig() {
	}

	/**
	 * Install SLF4J JUL logging bridge and reset previous JUL log handlers
	 */
	public static void setupLogging() {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

}
