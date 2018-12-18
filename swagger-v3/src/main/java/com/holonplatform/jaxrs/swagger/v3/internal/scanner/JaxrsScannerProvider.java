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
package com.holonplatform.jaxrs.swagger.v3.internal.scanner;

import java.util.Optional;

import com.holonplatform.jaxrs.swagger.JaxrsScannerType;

import io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner;
import io.swagger.v3.jaxrs2.integration.JaxrsApplicationAndAnnotationScanner;
import io.swagger.v3.jaxrs2.integration.api.JaxrsOpenApiScanner;

/**
 * Provides a suitable scanner implementation according to a {@link JaxrsScannerType} enumeration value.
 *
 * @since 5.2.0
 */
public final class JaxrsScannerProvider {

	private JaxrsScannerProvider() {
	}

	/**
	 * Get the {@link JaxrsOpenApiScanner} class which corresponds to given type, if available.
	 * @param type Scanner type
	 * @return Optional scanner class. An empty Optional is always returned for {@link JaxrsScannerType#DEFAULT}.
	 */
	public static Optional<Class<? extends JaxrsOpenApiScanner>> getScannerClass(JaxrsScannerType type) {
		if (type != null) {
			switch (type) {
			case ANNOTATION:
				return Optional.of(JaxrsAnnotationScanner.class);
			case APPLICATION:
				return Optional.of(JaxrsApplicationResourcesScanner.class);
			case APPLICATION_AND_ANNOTATION:
				return Optional.of(JaxrsApplicationAndAnnotationScanner.class);
			case DEFAULT:
			default:
				break;
			}
		}
		return Optional.empty();
	}

}
