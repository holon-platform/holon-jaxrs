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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.http.Cookie;
import com.holonplatform.http.HttpMethod;
import com.holonplatform.http.HttpRequest;
import com.holonplatform.http.internal.AbstractHttpRequest;
import com.holonplatform.jaxrs.server.utils.RequestUtils;

/**
 * {@link HttpRequest} using JAX-RS {@link ContainerRequestContext}.
 * 
 * @since 5.0.0
 */
public class JaxrsContainerHttpRequest extends AbstractHttpRequest {

	/**
	 * Concrete JAX-RS container request
	 */
	protected final ContainerRequestContext request;

	/**
	 * Constructor
	 * @param request JAX-RS {@link ContainerRequestContext}
	 */
	public JaxrsContainerHttpRequest(ContainerRequestContext request) {
		super();
		ObjectUtils.argumentNotNull(request, "ContainerRequestContext must be not null");
		this.request = request;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.http.HttpRequest#getMethod()
	 */
	@Override
	public HttpMethod getMethod() {
		return HttpMethod.from(request.getMethod());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.messaging.Message#getHeaders()
	 */
	@Override
	public Map<String, List<String>> getHeaders() {
		return request.getHeaders();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.messaging.Message#getHeader(java.lang.String)
	 */
	@Override
	public Optional<List<String>> getHeader(String name) {
		ObjectUtils.argumentNotNull(request, "Header name must be not null");
		return Optional.ofNullable(request.getHeaders().get(name));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.http.HttpRequest#getRequestPath()
	 */
	@Override
	public String getRequestPath() {
		return request.getUriInfo().getPath();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.HttpRequest#getRequestHost()
	 */
	@Override
	public String getRequestHost() {
		return request.getUriInfo().getRequestUri().getHost();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.http.HttpRequest#getRequestParameter(java.lang.String)
	 */
	@Override
	public Optional<String> getRequestParameter(String name) {
		ObjectUtils.argumentNotNull(name, "Parameter name must be not null");
		return RequestUtils.getQueryParameterValue(request.getUriInfo().getQueryParameters(), name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.http.HttpRequest#getMultiValueRequestParameter(java.lang.String)
	 */
	@Override
	public Optional<List<String>> getMultiValueRequestParameter(String name) {
		ObjectUtils.argumentNotNull(name, "Parameter name must be not null");
		Map<String, List<String>> queryParameters = request.getUriInfo().getQueryParameters();
		if (queryParameters != null && queryParameters.containsKey(name)) {
			return Optional.ofNullable(queryParameters.get(name));
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.http.HttpRequest#getRequestParameters()
	 */
	@Override
	public Map<String, List<String>> getRequestParameters() {
		return request.getUriInfo().getQueryParameters();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.http.HttpRequest#getRequestCookie(java.lang.String)
	 */
	@Override
	public Optional<Cookie> getRequestCookie(String name) {
		ObjectUtils.argumentNotNull(name, "Cookie name must be not null");
		Map<String, javax.ws.rs.core.Cookie> cookies = request.getCookies();
		if (cookies != null && cookies.containsKey(name)) {
			javax.ws.rs.core.Cookie cookie = cookies.get(name);
			if (cookie != null) {
				return Optional.of(Cookie.builder().name(cookie.getName()).value(cookie.getValue())
						.version(cookie.getVersion()).path(cookie.getPath()).domain(cookie.getDomain()).build());
			}
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.http.HttpRequest#getBody()
	 */
	@Override
	public InputStream getBody() throws IOException, UnsupportedOperationException {
		return request.getEntityStream();
	}

}
