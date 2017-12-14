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
package com.holonplatform.jaxrs.server.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.http.HttpMethod;
import com.holonplatform.http.HttpRequest;
import com.holonplatform.http.internal.AbstractHttpRequest;
import com.holonplatform.jaxrs.server.JaxrsHttpRequest;

/**
 * {@link HttpRequest} using JAX-RS context informations.
 * 
 * @since 5.0.0
 */
public class DefaultJaxrsHttpRequest extends AbstractHttpRequest implements JaxrsHttpRequest {

	/**
	 * HTTP request method
	 */
	protected final String method;

	/**
	 * JAX-RS URI informations
	 */
	protected final UriInfo uriInfo;

	/**
	 * JAX-RS HTTP header informations
	 */
	protected final HttpHeaders headers;

	/**
	 * Query parameters
	 */
	protected final MultivaluedMap<String, String> queryParameters;

	/**
	 * Construct and {@link HttpRequest} using JAX-RS context informations
	 * @param request Request
	 * @param uriInfo URI informations
	 * @param headers Headers informations
	 */
	public DefaultJaxrsHttpRequest(Request request, UriInfo uriInfo, HttpHeaders headers) {
		super();
		ObjectUtils.argumentNotNull(uriInfo, "UriInfo must be not null");
		ObjectUtils.argumentNotNull(headers, "HttpHeaders must be not null");

		this.method = (request != null) ? request.getMethod() : null;
		this.uriInfo = uriInfo;
		this.headers = headers;
		this.queryParameters = uriInfo.getQueryParameters();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.server.oauth2.HttpRequest#getMethod()
	 */
	@Override
	public HttpMethod getMethod() {
		return HttpMethod.from(method);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.messaging.Message#getHeaders()
	 */
	@Override
	public Map<String, List<String>> getHeaders() {
		return headers.getRequestHeaders();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.messaging.Message#getHeader(java.lang.String)
	 */
	@Override
	public Optional<List<String>> getHeader(String name) {

		ObjectUtils.argumentNotNull(name, "Header name must be not null");

		if (headers != null) {
			return Optional.ofNullable(headers.getRequestHeader(name));
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.server.oauth2.HttpRequest#getRequestPath()
	 */
	@Override
	public String getRequestPath() {
		return uriInfo.getPath();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.http.HttpRequest#getRequestHost()
	 */
	@Override
	public String getRequestHost() {
		return uriInfo.getRequestUri().getHost();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.server.oauth2.HttpRequest#getRequestParameter(java.lang.String)
	 */
	@Override
	public Optional<String> getRequestParameter(String name) {
		ObjectUtils.argumentNotNull(name, "Parameter name must be not null");
		return RequestUtils.getQueryParameterValue(queryParameters, name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.server.oauth2.HttpRequest#getMultiValueRequestParameter(java.lang.String)
	 */
	@Override
	public Optional<List<String>> getMultiValueRequestParameter(String name) {
		ObjectUtils.argumentNotNull(name, "Parameter name must be not null");
		if (queryParameters != null && queryParameters.containsKey(name)) {
			return Optional.ofNullable(queryParameters.get(name));
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.server.oauth2.request.HttpRequest#getRequestParameters()
	 */
	@Override
	public MultivaluedMap<String, String> getRequestParameters() {
		return queryParameters;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.HttpRequest#getRequestCookie(java.lang.String)
	 */
	@Override
	public Optional<com.holonplatform.http.Cookie> getRequestCookie(String name) {
		ObjectUtils.argumentNotNull(name, "Cookie name must be not null");
		Map<String, Cookie> cookies = headers.getCookies();
		if (cookies != null && cookies.containsKey(name)) {
			Cookie cookie = cookies.get(name);
			if (cookie != null) {
				return Optional.of(com.holonplatform.http.Cookie.builder().name(cookie.getName())
						.value(cookie.getValue()).version(cookie.getVersion()).path(cookie.getPath())
						.domain(cookie.getDomain()).build());
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
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.server.JaxrsHttpRequest#getRequestURI()
	 */
	@Override
	public String getRequestURI() {
		URI uri = uriInfo.getRequestUri();
		if (uri != null) {
			return uri.toString();
		}
		return null;
	}

}
