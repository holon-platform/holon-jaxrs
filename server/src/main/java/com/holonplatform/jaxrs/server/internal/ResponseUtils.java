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

import java.io.Serializable;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.holonplatform.auth.AuthenticationError;
import com.holonplatform.http.ErrorResponse;
import com.holonplatform.http.HttpHeaders;

/**
 * Utility class to handle responses.
 * 
 * @since 5.0.0
 */
public final class ResponseUtils implements Serializable {

	private static final long serialVersionUID = 3844502932778621114L;

	private ResponseUtils() {
	}

	/**
	 * Build an authentication error response using given {@link AuthenticationError}.
	 * @param error AuthenticationError
	 * @param realmName Optional realm name
	 * @return Authentication error response
	 */
	public static Response buildAuthenticationErrorResponse(AuthenticationError error, String realmName) {
		return buildAuthenticationErrorResponse(
				(error != null && error.getScheme() != null) ? new String[] { error.getScheme() } : null,
				(error != null) ? error.getErrorCode() : null, (error != null) ? error.getErrorDescription() : null,
				(error != null) ? error.getHttpStatus() : null, realmName);
	}

	/**
	 * Build an authentication error response
	 * @param schemes Optional allowed authentication schemes
	 * @param errorCode Optional error code
	 * @param errorDescription Optional error description
	 * @param statusCode HTTP status code
	 * @param realmName Optional realm name
	 * @return Authentication error response
	 */
	public static Response buildAuthenticationErrorResponse(String[] schemes, String errorCode, String errorDescription,
			int statusCode, String realmName) {

		// status
		Status status = Status.UNAUTHORIZED;
		if (statusCode > 0) {
			Status errorStatus = Status.fromStatusCode(statusCode);
			if (errorStatus != null) {
				status = errorStatus;
			}
		}

		// response
		ResponseBuilder responseBuilder = Response.status(status);

		if (schemes != null && schemes.length > 0) {
			for (String scheme : schemes) {
				responseBuilder.header(HttpHeaders.WWW_AUTHENTICATE,
						buildAuthenticationErrorHeader(scheme, errorCode, errorDescription, realmName));
			}
		}

		// response
		return responseBuilder.header(HttpHeaders.CACHE_CONTROL, "no-store").header(HttpHeaders.PRAGMA, "no-cache")
				.build();
	}

	/**
	 * Build an authentication error response {@link HttpHeaders#WWW_AUTHENTICATE} header.
	 * @param scheme Optional scheme name
	 * @param errorCode Optional error code
	 * @param errorDescription Optional error description
	 * @param realmName Optional realm name
	 * @return Header value
	 */
	private static String buildAuthenticationErrorHeader(String scheme, String errorCode, String errorDescription,
			String realmName) {
		StringBuilder sb = new StringBuilder();

		if (scheme != null) {
			sb.append(scheme).append(" ");
		}

		if (realmName != null) {
			sb.append("realm=\"");
			sb.append(realmName);
			sb.append("\",");
		}

		if (errorCode != null) {
			sb.append(ErrorResponse.ERROR_CODE_SERIALIZATION_NAME);
			sb.append("=\"");
			sb.append(errorCode);
			sb.append("\",");
		}

		if (errorDescription != null) {
			sb.append(ErrorResponse.ERROR_DESCRIPTION_SERIALIZATION_NAME);
			sb.append("=\"");
			sb.append(errorDescription);
			sb.append("\",");
		}

		String header = sb.toString();
		if (header.endsWith(",")) {
			header = header.substring(0, header.length() - 1);
		}

		return header;
	}

}
