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
package com.holonplatform.jaxrs.server.security.internal;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;

import com.holonplatform.auth.AuthContext;
import com.holonplatform.auth.Realm;
import com.holonplatform.jaxrs.server.internal.auth.AbstractAuthContextFilter;
import com.holonplatform.spring.security.SpringSecurity;

/**
 * Filter to replace the request {@link SecurityContext} with an {@link AuthContext} compatible implementation, using
 * Spring Security to obtain current authentication and authorities.
 * <p>
 * The {@link Realm} is obtained either from a registered {@link ContextResolver} of {@link Realm} type, if available,
 * or as a {@link com.holonplatform.core.Context} resource using {@link Realm#getCurrent()}.
 * </p>
 * <p>
 * When this filter is registered and enabled, the default {@link AuthContext} filter is skipped.
 * </p>
 *
 * @since 5.1.0
 */
@Priority(Priorities.AUTHENTICATION - 15)
public class SpringSecurityAuthContextFilter extends AbstractAuthContextFilter {

	public SpringSecurityAuthContextFilter() {
		super(false);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.server.internal.auth.AbstractAuthContextFilter#getAuthContext(com.holonplatform.auth.
	 * Realm)
	 */
	@Override
	protected AuthContext getAuthContext(Realm realm) {
		return SpringSecurity.authContext(getRealmOrDefault(realm));
	}

	/**
	 * Get the given {@link Realm} or a default Realm if <code>null</code>.
	 * @param realm Realm
	 * @return The Realm or a default Realm if <code>null</code>
	 */
	private static Realm getRealmOrDefault(Realm realm) {
		if (realm != null) {
			return realm;
		}
		// default
		return Realm.builder().withDefaultAuthorizer().build();
	}

}
