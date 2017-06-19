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

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.Servlet;
import javax.servlet.ServletContextListener;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.holonplatform.jaxrs.spring.boot.resteasy.internal.ResteasyBootstrapListener;

/**
 * TODO
 */
@Configuration
@ConditionalOnClass(name = { "org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap",
		"javax.servlet.ServletRegistration" })
@ConditionalOnWebApplication
@AutoConfigureBefore(DispatcherServletAutoConfiguration.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class)
@EnableConfigurationProperties(ResteasyConfigurationProperties.class)
public class ResteasyAutoConfiguration {

	private final ResteasyConfigurationProperties resteasy;

	private final ResteasyConfig config;

	private final List<ResteasyConfigCustomizer> customizers;

	private String path;

	public ResteasyAutoConfiguration(ResteasyConfigurationProperties resteasy, ResteasyConfig config,
			ObjectProvider<List<ResteasyConfigCustomizer>> customizers) {
		this.resteasy = resteasy;
		this.config = config;
		this.customizers = customizers.getIfAvailable();
	}

	@PostConstruct
	public void path() {
		resolveApplicationPath();
		customize();
	}

	private void resolveApplicationPath() {
		if (StringUtils.hasLength(this.resteasy.getApplicationPath())) {
			this.path = parseApplicationPath(this.resteasy.getApplicationPath());
		} else {
			this.path = findApplicationPath(
					AnnotationUtils.findAnnotation(this.config.getClass(), ApplicationPath.class));
		}
	}

	private void customize() {
		if (this.customizers != null) {
			AnnotationAwareOrderComparator.sort(this.customizers);
			for (ResteasyConfigCustomizer customizer : this.customizers) {
				customizer.customize(this.config);
			}
		}
	}

	@Bean
	public static SpringBeanProcessor resteasySpringBeanProcessor() {
		final ResteasyProviderFactory resteasyProviderFactory = new ResteasyProviderFactory();
		final ResourceMethodRegistry resourceMethodRegistry = new ResourceMethodRegistry(resteasyProviderFactory);

		SpringBeanProcessor springBeanProcessor = new SpringBeanProcessor();
		springBeanProcessor.setProviderFactory(resteasyProviderFactory);
		springBeanProcessor.setRegistry(resourceMethodRegistry);

		return springBeanProcessor;
	}

	@Bean
	public ServletContextListener resteasyBootstrap(SpringBeanProcessor springBeanProcessor) {
		return new ResteasyBootstrapListener(springBeanProcessor);

	}

	@Bean
	@ConditionalOnMissingBean(name = "resteasyServletRegistration")
	public ServletRegistrationBean resteasyServletRegistration() {

		final Servlet servlet = new HttpServlet30Dispatcher();

		ServletRegistrationBean registration = new ServletRegistrationBean(servlet, this.path);
		addInitParameters(registration);
		registration.setName(getServletRegistrationName());
		registration.setLoadOnStartup(this.resteasy.getLoadOnStartup());
		registration.setAsyncSupported(true);
		registration.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, this.path);

		Set<Class<?>> classes = this.config.getClasses();
		if (classes != null) {
			final Set<Class<?>> resources = new HashSet<>();
			final Set<Class<?>> providers = new HashSet<>();
			for (Class<?> cls : classes) {
				if (cls.isAnnotationPresent(Provider.class)) {
					providers.add(cls);
				} else {
					resources.add(cls);
				}
			}
			if (!providers.isEmpty()) {
				registration.addInitParameter(ResteasyContextParameters.RESTEASY_SCANNED_PROVIDERS,
						providers.stream().map(c -> c.getName()).collect(Collectors.joining(",")));
			}
			if (!resources.isEmpty()) {
				registration.addInitParameter(ResteasyContextParameters.RESTEASY_SCANNED_RESOURCES,
						resources.stream().map(c -> c.getName()).collect(Collectors.joining(",")));
			}
		}

		return registration;
	}

	private String getServletRegistrationName() {
		return ClassUtils.getUserClass(this.config.getClass()).getName();
	}

	private void addInitParameters(RegistrationBean registration) {
		for (Entry<String, String> entry : this.resteasy.getInit().entrySet()) {
			registration.addInitParameter(entry.getKey(), entry.getValue());
		}
	}

	private static String findApplicationPath(ApplicationPath annotation) {
		if (annotation == null) {
			return "/*";
		}
		return parseApplicationPath(annotation.value());
	}

	private static String parseApplicationPath(String applicationPath) {
		String path = applicationPath;
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return path.equals("/") ? "/*" : path + "/*";
	}

}
