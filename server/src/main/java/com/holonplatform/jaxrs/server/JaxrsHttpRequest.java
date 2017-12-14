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
package com.holonplatform.jaxrs.server;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.holonplatform.http.HttpRequest;
import com.holonplatform.jaxrs.server.internal.DefaultJaxrsHttpRequest;

/**
 * A {@link HttpRequest} backed by a JAX-RS context request.
 * 
 * @since 5.0.5
 *
 */
public interface JaxrsHttpRequest extends HttpRequest {

	/**
	 * Get the absolute request URI including any query parameters.
	 * @return the absolute request URI
	 */
	String getRequestURI();

	/**
	 * Create a {@link JaxrsHttpRequest} using JAX-RS request information to obtain the concrete request attributes and
	 * configuration.
	 * @param request JAX-RS {@link Request} for request processing information
	 * @param uriInfo JAX-RS {@link UriInfo} for request URI information (not null)
	 * @param headers JAX-RS {@link HttpHeaders} for request headers information (not null)
	 * @return A new {@link JaxrsHttpRequest}
	 */
	static JaxrsHttpRequest create(Request request, UriInfo uriInfo, HttpHeaders headers) {
		return new DefaultJaxrsHttpRequest(request, uriInfo, headers);
	}

	/**
	 * Create a {@link JaxrsHttpRequest} using JAX-RS request information to obtain the concrete request attributes and
	 * configuration.
	 * <p>
	 * This method don't requires the {@link Request} object, but the request method, obtained from
	 * {@link HttpRequest#getMethod()} will not be available.
	 * </p>
	 * @param uriInfo JAX-RS {@link UriInfo} for request URI information (not null)
	 * @param headers JAX-RS {@link HttpHeaders} for request headers information (not null)
	 * @return A new {@link JaxrsHttpRequest}
	 * @see #create(Request, UriInfo, HttpHeaders)
	 */
	static JaxrsHttpRequest create(UriInfo uriInfo, HttpHeaders headers) {
		return create(null, uriInfo, headers);
	}

}
