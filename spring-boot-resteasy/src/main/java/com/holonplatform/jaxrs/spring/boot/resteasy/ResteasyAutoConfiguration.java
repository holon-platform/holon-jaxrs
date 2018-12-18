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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.plugins.server.servlet.Filter30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.holonplatform.jaxrs.spring.boot.resteasy.internal.ResteasyBootstrapListener;

/**
 * Spring boot auto configuration to setup Resteasy using a {@link ResteasyConfig} JAX-RS Application bean and
 * automatically register any JAX-RS resource which is declared as a Spring bean, such as {@link Path} and
 * {@link Provider} annotated beans.
 * 
 * <p>
 * If a {@link ResteasyConfig} bean is not provided in Spring context, a default one will be created and registered
 * automatically. You can use {@link ResteasyConfig} class to explicitly register JAX-RS endpoint/provider classes,
 * singleton resources and configuration properties.
 * </p>
 * 
 * <p>
 * The {@link ResteasyConfigCustomizer} interface can be used to customize application resources registration. Any
 * Spring bean which implements such interface is automatically detected and the
 * {@link ResteasyConfigCustomizer#customize(ResteasyConfig)} method is invoked.
 * </p>
 * 
 * <p>
 * The Resteasy JAX-RS application path can be defined either using the {@link ApplicationPath} annotation on the
 * {@link ResteasyConfig} bean class or through the <code>holon.resteasy.application-path</code> configuration property.
 * See {@link ResteasyConfigurationProperties} for a list of available configuration properties.
 * </p>
 * 
 * @since 5.0.0
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
		springBeanProcessor.setOrder(Integer.MAX_VALUE - 10000);

		return springBeanProcessor;
	}

	@Bean
	public ServletContextListener resteasyBootstrap(SpringBeanProcessor springBeanProcessor) {
		return new ResteasyBootstrapListener(springBeanProcessor, this.config);
	}

	@Bean
	@ConditionalOnMissingBean(name = "resteasyServletRegistration")
	@ConditionalOnProperty(prefix = "holon.resteasy", name = "type", havingValue = "servlet", matchIfMissing = true)
	public ServletRegistrationBean<Servlet> resteasyServletRegistration() {
		final Servlet servlet = new HttpServlet30Dispatcher();
		final ServletRegistrationBean<Servlet> registration = new ServletRegistrationBean<>(servlet, this.path);
		registration.setName(getServletRegistrationName());
		registration.setLoadOnStartup(this.resteasy.getServlet().getLoadOnStartup());
		registration.setAsyncSupported(true);

		if (this.path != null && !"/*".equals(this.path)) {
			String prefix = path;
			if (prefix.endsWith("*")) {
				prefix = prefix.substring(0, prefix.length() - 1);
			}
			if (prefix.endsWith("/")) {
				prefix = prefix.substring(0, prefix.length() - 1);
			}
			registration.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, prefix);
		}
		return registration;
	}

	@Bean
	@ConditionalOnMissingBean(name = "jerseyFilterRegistration")
	@ConditionalOnProperty(prefix = "holon.resteasy", name = "type", havingValue = "filter")
	public FilterRegistrationBean<Filter> resteasyFilterRegistration() {
		final Filter filter = new Filter30Dispatcher();
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
		registration.setFilter(filter);
		registration.setUrlPatterns(Arrays.asList(this.path));
		registration.setOrder(this.resteasy.getFilter().getOrder());
		registration.setName("resteasyFilter");
		registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
		return registration;
	}

	@Bean
	public ServletContextInitializer contextInitParamsInitializer(final ResteasyConfigurationProperties resteasy) {
		return new ServletContextInitializer() {

			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				for (Entry<String, String> entry : resteasy.getInit().entrySet()) {
					servletContext.setInitParameter(entry.getKey(), entry.getValue());
				}
			}
		};
	}

	private String getServletRegistrationName() {
		return ClassUtils.getUserClass(this.config.getClass()).getName();
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
