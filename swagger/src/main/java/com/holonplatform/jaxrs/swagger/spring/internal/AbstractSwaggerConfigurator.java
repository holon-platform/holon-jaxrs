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
package com.holonplatform.jaxrs.swagger.spring.internal;

/**
 * Base Swagger API listing configuration class.
 *
 * @since 5.2.0
 */
public abstract class AbstractSwaggerConfigurator {

	/**
	 * Compose a path.
	 * @param basePath Base path
	 * @param path Additinal path
	 * @return Composed path
	 */
	protected static String composePath(String basePath, String path) {
		StringBuilder ap = new StringBuilder();
		if (basePath != null) {
			if (!basePath.startsWith("/")) {
				ap.append('/');
			}
			ap.append(basePath);
		}
		if (path != null) {
			final String prefix = ap.toString();
			if (prefix != null && !prefix.endsWith("/")) {
				ap.append('/');
			}
			if (path.startsWith("/")) {
				ap.append(path.substring(1));
			} else {
				ap.append(path);
			}
		}
		return ap.toString();
	}

}
