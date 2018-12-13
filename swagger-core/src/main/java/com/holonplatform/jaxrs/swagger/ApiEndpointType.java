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
package com.holonplatform.jaxrs.swagger;

/**
 * API endpoint types enumeration.
 *
 * @since 5.2.0
 */
public enum ApiEndpointType {

	/**
	 * API endpoint type which uses a <code>type</code> named query parameter to declare the API output format, which
	 * can be either <code>json</code> or <code>yaml</code>.
	 */
	QUERY_PARAMETER,

	/**
	 * API endpoint type which uses a <code>type</code> named path parameter to declare the API output format, which can
	 * be either <code>json</code> or <code>yaml</code>.
	 * <p>
	 * The path parameter is prefixed by a dot (<code>.</code>).
	 * </p>
	 */
	PATH_PARAMETER,

	/**
	 * API endpoint type which uses the <code>Accept</code> header value to declare the API output format, which can be
	 * either <code>application/json</code> or <code>application/yaml</code>.
	 */
	ACCEPT_HEADER;

}
