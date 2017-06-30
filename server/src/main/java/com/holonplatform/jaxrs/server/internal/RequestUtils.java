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
package com.holonplatform.jaxrs.server.internal;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Utility class to handle requests.
 * 
 * @since 5.0.0
 */
public class RequestUtils implements Serializable {

	private static final long serialVersionUID = -8932866380055499648L;

	private RequestUtils() {
	}

	/**
	 * Get a URI query parameter value by name
	 * @param queryParameters URI query parameters
	 * @param name Parameter name
	 * @return Parameter value, or <code>null</code> if not found
	 */
	public static Optional<String> getQueryParameterValue(MultivaluedMap<String, String> queryParameters, String name) {
		if (name != null) {
			if (queryParameters != null && queryParameters.containsKey(name)) {
				List<String> values = queryParameters.get(name);
				if (values != null && !values.isEmpty()) {
					if (values.size() == 1) {
						return Optional.ofNullable(values.get(0));
					} else {
						StringBuilder sb = new StringBuilder();
						for (String value : values) {
							if (sb.length() > 0) {
								sb.append(',');
							}
							sb.append(value);
						}
						return Optional.of(sb.toString());
					}
				}
			}
		}
		return Optional.empty();
	}

}
