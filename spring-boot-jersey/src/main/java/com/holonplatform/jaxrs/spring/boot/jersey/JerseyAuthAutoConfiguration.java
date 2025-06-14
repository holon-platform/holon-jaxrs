/*
 * Copyright 2000-2017 Holon TDCN.
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
package com.holonplatform.jaxrs.spring.boot.jersey;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.auth.Realm;
import com.holonplatform.jaxrs.server.auth.AuthenticationFeature;

import jakarta.ws.rs.ext.ContextResolver;

/**
 * Jersey authentication and authorization auto configuration based on {@link Realm}.
 * <p>
 * If a Jersey {@link ResourceConfig} bean and a {@link Realm} bean are available form Spring
 * context, the following operations are performed:
 * <ul>
 * <li>A {@link Realm} type {@link ContextResolver} providing the {@link Realm} bean instance is
 * registered</li>
 * <li>The {@link AuthenticationFeature} is registered</li>
 * <li>The Jersey {@link RolesAllowedDynamicFeature} is registered</li>
 * </ul>
 * 
 * @since 5.0.0
 */
@AutoConfiguration
@ConditionalOnBean(ResourceConfig.class)
@AutoConfigureAfter(value = { JerseyAutoConfiguration.class, JerseyServerAutoConfiguration.class })
public class JerseyAuthAutoConfiguration {

	@Configuration
	@ConditionalOnBean(Realm.class)
	static class AuthConfiguration implements ResourceConfigCustomizer {

		private final Realm realm;

		public AuthConfiguration(final Realm realm) {
			this.realm = realm;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer#customize(org.glassfish.
		 * jersey.server. ResourceConfig)
		 */
		@Override
		public void customize(ResourceConfig config) {
			// Realm ContextResolver
			config.register(new ContextResolver<Realm>() {

				@Override
				public Realm getContext(Class<?> type) {
					return realm;
				}
			});
			// Authentication
			config.register(AuthenticationFeature.class);
			// Authorization
			config.register(RolesAllowedDynamicFeature.class);
		}

	}

}
