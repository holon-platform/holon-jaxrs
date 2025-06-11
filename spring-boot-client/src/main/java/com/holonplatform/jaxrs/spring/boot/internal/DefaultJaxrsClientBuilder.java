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
package com.holonplatform.jaxrs.spring.boot.internal;

import java.util.LinkedList;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import com.holonplatform.jaxrs.spring.boot.JaxrsClientBuilder;
import com.holonplatform.jaxrs.spring.boot.JaxrsClientBuilderFactory;
import com.holonplatform.jaxrs.spring.boot.JaxrsClientCustomizer;

/**
 * Default {@link JaxrsClientBuilder} implementation.
 * 
 * @since 5.0.0
 */
public class DefaultJaxrsClientBuilder implements JaxrsClientBuilder {

	private JaxrsClientBuilderFactory factory;

	private List<JaxrsClientCustomizer> customizers = new LinkedList<>();

	private ClientBuilder builder;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.spring.boot.JaxrsClientBuilder#build()
	 */
	@Override
	public Client build() {
		return getClientBuilder().build();
	}

	/**
	 * Get the {@link JaxrsClientBuilderFactory} to use to create the {@link ClientBuilder} instance.
	 * @return the JaxrsClientBuilderFactory
	 */
	protected JaxrsClientBuilderFactory getFactory() {
		return factory;
	}

	/**
	 * Get the {@link JaxrsClientCustomizer}s to use to customize the {@link ClientBuilder} instance.
	 * @return the {@link JaxrsClientCustomizer}s
	 */
	protected List<JaxrsClientCustomizer> getCustomizers() {
		return customizers;
	}

	/**
	 * Get the {@link ClientBuilder} instance to use to create clients.
	 * @return the {@link ClientBuilder} instance
	 */
	private synchronized ClientBuilder getClientBuilder() {
		if (builder == null) {
			builder = customizeBuilder(createClientBuilder());
		}
		return builder;
	}

	/**
	 * Create the {@link ClientBuilder} instance to use to create clients.
	 * @return the new {@link ClientBuilder} instance
	 */
	private ClientBuilder createClientBuilder() {
		// check factory
		if (getFactory() != null) {
			return getFactory().createBuilder();
		}
		// default
		return ClientBuilder.newBuilder();
	}

	/**
	 * Customize the {@link ClientBuilder} using the {@link JaxrsClientCustomizer}s, if any.
	 * @param builder The {@link ClientBuilder} to customize
	 * @return the customized {@link ClientBuilder}
	 */
	private ClientBuilder customizeBuilder(final ClientBuilder builder) {
		customizers.forEach(c -> c.customize(builder));
		return builder;
	}

	/**
	 * Set the {@link JaxrsClientBuilderFactory} to use to create the {@link ClientBuilder} instance.
	 * @param factory the JaxrsClientBuilderFactory to set
	 */
	public void setFactory(JaxrsClientBuilderFactory factory) {
		this.factory = factory;
		// discard current builder, if any
		this.builder = null;
	}

	/**
	 * Add a {@link JaxrsClientCustomizer} to customize the {@link ClientBuilder} instance.
	 * @param customizer The {@link JaxrsClientCustomizer} to add
	 */
	public void addCustomizer(JaxrsClientCustomizer customizer) {
		if (customizer != null) {
			customizers.add(customizer);
			// discard current builder, if any
			this.builder = null;
		}
	}

}
