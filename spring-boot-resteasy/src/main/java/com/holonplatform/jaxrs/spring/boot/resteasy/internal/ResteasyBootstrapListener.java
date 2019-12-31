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
package com.holonplatform.jaxrs.spring.boot.resteasy.internal;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.holonplatform.jaxrs.spring.boot.resteasy.ResteasyConfig;

/**
 * Reasteasy auto configuration bootstrap {@link ServletContextListener}.
 * 
 * @since 5.0.0
 */
public class ResteasyBootstrapListener implements ServletContextListener {

	private final SpringBeanProcessor processor;
	private final ResteasyConfig application;

	private ResteasyDeployment deployment;

	public ResteasyBootstrapListener(SpringBeanProcessor processor, ResteasyConfig application) {
		super();
		this.processor = processor;
		this.application = application;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		final ServletContext ctx = sce.getServletContext();

		ListenerBootstrap config = new ListenerBootstrap(ctx);

		ResourceMethodRegistry resourceMethodRegistry = (ResourceMethodRegistry) processor.getRegistry();

		deployment = config.createDeployment();

		deployment.setProviderFactory(processor.getProviderFactory());
		deployment.setRegistry(resourceMethodRegistry);

		if (deployment.isAsyncJobServiceEnabled()) {
			AsynchronousDispatcher dispatcher = new AsynchronousDispatcher(deployment.getProviderFactory(),
					resourceMethodRegistry);
			deployment.setDispatcher(dispatcher);
		} else {
			SynchronousDispatcher dispatcher = new SynchronousDispatcher(deployment.getProviderFactory(),
					resourceMethodRegistry);
			deployment.setDispatcher(dispatcher);
		}

		if (application != null) {
			deployment.setApplication(application);
		}

		deployment.start();

		ctx.setAttribute(ResteasyProviderFactory.class.getName(), deployment.getProviderFactory());
		ctx.setAttribute(Dispatcher.class.getName(), deployment.getDispatcher());
		ctx.setAttribute(Registry.class.getName(), deployment.getRegistry());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (deployment != null) {
			deployment.stop();
		}
	}

}
