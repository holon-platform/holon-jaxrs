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
package com.holonplatform.jaxrs.spring.boot.resteasy;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.auth.Realm;
import com.holonplatform.jaxrs.server.auth.AuthenticationFeature;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.ws.rs.ext.ContextResolver;

/**
 * Resteasy authentication and authorization auto configuration based on {@link Realm}.
 * <p>
 * If a Resteasy {@link ResteasyConfig} bean and a {@link Realm} bean are available form Spring
 * context, the following operations are performed:
 * <ul>
 * <li>A {@link Realm} type {@link ContextResolver} providing the {@link Realm} bean instance is
 * registered</li>
 * <li>The {@link AuthenticationFeature} is registered</li>
 * </ul>
 * 
 * @since 5.0.0
 */
@AutoConfiguration
@ConditionalOnBean(ResteasyConfig.class)
@AutoConfigureAfter(ResteasyAutoConfiguration.class)
public class ResteasyAuthAutoConfiguration {

	@Configuration
	@ConditionalOnBean(Realm.class)
	static class AuthConfiguration {

		public AuthConfiguration(ResteasyConfig config) {
			// Authentication
			config.register(AuthenticationFeature.class);
		}

		@Bean
		public ServletContextInitializer initializer() {
			return new ServletContextInitializer() {

				@Override
				public void onStartup(ServletContext servletContext) throws ServletException {
					servletContext.setInitParameter("resteasy.role.based.security", "true");
				}
			};
		}

	}

}
