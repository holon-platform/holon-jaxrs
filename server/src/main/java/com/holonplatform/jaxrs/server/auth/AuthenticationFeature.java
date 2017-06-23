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
package com.holonplatform.jaxrs.server.auth;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.Realm;
import com.holonplatform.auth.AuthenticationToken.AuthenticationTokenResolver;
import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.auth.exceptions.AuthenticationException;
import com.holonplatform.auth.exceptions.UnsupportedMessageException;
import com.holonplatform.auth.internal.DefaultAuthContext;
import com.holonplatform.http.HttpHeaders;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.http.internal.HttpUtils;
import com.holonplatform.jaxrs.server.internal.JaxrsContainerHttpRequest;
import com.holonplatform.jaxrs.server.utils.ResourceUtils;
import com.holonplatform.jaxrs.server.utils.ResponseUtils;

/**
 * A JAX-RS {@link Feature} which can be registered in server application to enable authentication support using
 * {@link Authenticate} annotation.
 * <p>
 * Using this feature, the {@link SecurityContext} of every JAX-RS request will be replaced with an {@link AuthContext}
 * compatible implementation. This way, the application {@link SecurityContext} is always expected to be an
 * {@link AuthContext} instance and will be used to perform authentication when required.
 * </p>
 * <p>
 * JAX-RS resource classes and/or methods annotated with {@link Authenticate} will be protected from unauthorized
 * access, performing client authentication using the {@link AuthContext} security context {@link Realm}.
 * </p>
 * <p>
 * Allowed authentication schemes can be specified using {@link Authenticate#schemes()} annotation attribute. If any
 * scheme is specified, a scheme-matching {@link AuthenticationTokenResolver} must be registered in {@link Realm} to
 * perform authentication with given scheme.
 * </p>
 * <p>
 * The {@link Realm} to use is obtained either from a registered {@link ContextResolver} of {@link Realm} type, if
 * available, or as a {@link com.holonplatform.core.Context} resource using {@link Realm#getCurrent()}.
 * </p>
 * <p>
 * When authentication informations provided by a client according to allowed authentication schemes (if any) for an
 * {@link Authenticate} resource are missing or invalid, a <code>401 - Unauthorized</code> status response is returned,
 * including a {@link HttpHeaders#WWW_AUTHENTICATE} header for each allowed authentication scheme, if any.
 * </p>
 *
 * @since 5.0.0
 */
public class AuthenticationFeature implements Feature {

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.core.Feature#configure(javax.ws.rs.core.FeatureContext)
	 */
	@Override
	public boolean configure(FeatureContext context) {
		if (!context.getConfiguration().isRegistered(AuthContextFilter.class)) {
			context.register(AuthContextFilter.class, Priorities.AUTHENTICATION - 10);
		}
		if (!context.getConfiguration().isRegistered(AuthenticationDynamicFeature.class)) {
			context.register(AuthenticationDynamicFeature.class);
		}
		return true;
	}

	/**
	 * A {@link SecurityContext} implementation which uses an {@link AuthContext} to perform authentication, provide
	 * authenticated informations and check permissions.
	 * <p>
	 * If the context is authenticated, {@link #getUserPrincipal()} method always returns an {@link Authentication}
	 * principal type.
	 * </p>
	 */
	private static class AuthSecurityContext extends DefaultAuthContext implements SecurityContext {

		/**
		 * Whether the authentication context is handled using a secure channel (such as HTTPS)
		 */
		private final boolean secureChannel;

		/**
		 * Construct a new AuthSecurityContext
		 * @param realm Authentication realm (not null)
		 * @param secureChannel Whether the authentication context is handled using a secure channel (such as HTTPS)
		 */
		public AuthSecurityContext(Realm realm, boolean secureChannel) {
			super(realm);
			this.secureChannel = secureChannel;
		}

		/**
		 * The {@link Principal} returned is the current {@link Authentication}, if any
		 * @return {@link Authentication} principal, or <code>null</code> if the context is not authenticated
		 */
		@Override
		public Principal getUserPrincipal() {
			return getAuthentication().orElse(null);
		}

		/*
		 * (non-Javadoc)
		 * @see javax.ws.rs.core.SecurityContext#isUserInRole(java.lang.String)
		 */
		@Override
		public boolean isUserInRole(String role) {
			return role != null && isPermitted(role);
		}

		/*
		 * (non-Javadoc)
		 * @see javax.ws.rs.core.SecurityContext#isSecure()
		 */
		@Override
		public boolean isSecure() {
			return secureChannel;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.ws.rs.core.SecurityContext#getAuthenticationScheme()
		 */
		@Override
		public String getAuthenticationScheme() {
			return getAuthentication().map(a -> a.getScheme().orElse(null)).orElse(null);
		}

	}

	// Authentication dynamic feature

	private static class AuthenticationDynamicFeature implements DynamicFeature {

		/*
		 * (non-Javadoc)
		 * @see javax.ws.rs.container.DynamicFeature#configure(javax.ws.rs.container.ResourceInfo,
		 * javax.ws.rs.core.FeatureContext)
		 */
		@Override
		public void configure(ResourceInfo resourceInfo, FeatureContext context) {
			// check method
			if (resourceInfo.getResourceMethod().isAnnotationPresent(Authenticate.class)) {
				context.register(
						new AuthenticationFilter(
								resourceInfo.getResourceMethod().getAnnotation(Authenticate.class).schemes()),
						Priorities.AUTHENTICATION);
				return;
			}
			// check class
			if (resourceInfo.getResourceClass().isAnnotationPresent(Authenticate.class)) {
				context.register(
						new AuthenticationFilter(
								resourceInfo.getResourceClass().getAnnotation(Authenticate.class).schemes()),
						Priorities.AUTHENTICATION);
				return;
			}
		}

	}

	// Filters

	/**
	 * Filter to replace the request {@link SecurityContext} with an {@link AuthContext} compatible implementation,
	 * using a {@link Realm} obtained either from a registered {@link ContextResolver} of {@link Realm} type, if
	 * available, or as a {@link com.holonplatform.core.Context} resource using {@link Realm#getCurrent()}.
	 */
	@Priority(Priorities.AUTHENTICATION - 10)
	private static class AuthContextFilter implements ContainerRequestFilter {

		@Context
		private Providers providers;

		/*
		 * (non-Javadoc)
		 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
		 */
		@Override
		public void filter(ContainerRequestContext requestContext) throws IOException {

			// Get realm
			Realm realm = ResourceUtils.lookupResource(getClass(), Realm.class, providers)
					.orElseThrow(() -> new IOException(
							"AuthContext setup failed: no Realm available from a ContextResolver or as a Context resource"));

			// replace SecurityContext
			requestContext.setSecurityContext(
					new AuthSecurityContext(realm, HttpUtils.isSecure(requestContext.getUriInfo().getRequestUri())));
		}

	}

	/**
	 * Filter to check if current {@link AuthContext} security context is authenticated, and if not, perform
	 * authentication using current request message and optional allowed authentication schemes.
	 */
	@Priority(Priorities.AUTHENTICATION)
	private static class AuthenticationFilter implements ContainerRequestFilter {

		@Context
		private Providers providers;

		/**
		 * Authentication schemes
		 */
		private final String[] schemes;

		/**
		 * Constructor
		 * @param schemes Authentication schemes to use. If <code>null</code> or empty, any scheme is allowed
		 */
		public AuthenticationFilter(String[] schemes) {
			super();
			this.schemes = schemes;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
		 */
		@Override
		public void filter(ContainerRequestContext requestContext) throws IOException {

			// check SecurityContext type
			if (!AuthContext.class.isAssignableFrom(requestContext.getSecurityContext().getClass())) {
				throw new IOException("Invalid SecurityContext type: expecting an AuthContext but found ["
						+ requestContext.getSecurityContext().getClass().getName() + "]");
			}

			final AuthContext authContext = (AuthContext) requestContext.getSecurityContext();
			// check authenticated
			if (!authContext.getAuthentication().isPresent()) {
				// authenticate
				try {
					authContext.authenticate(new JaxrsContainerHttpRequest(requestContext), schemes);
				} catch (@SuppressWarnings("unused") UnsupportedMessageException e) {
					requestContext.abortWith(ResponseUtils.buildAuthenticationErrorResponse(schemes, null, null,
							HttpStatus.UNAUTHORIZED.getCode(), null));
				} catch (AuthenticationException e) {
					requestContext.abortWith(ResponseUtils.buildAuthenticationErrorResponse(e, null));
				}
			}

		}

	}

}
