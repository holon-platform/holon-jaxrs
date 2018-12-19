/*
 * Copyright 2016-2018 Axioma srl.
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
package com.holonplatform.jaxrs.swagger.v2.internal.context;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Application;

import com.holonplatform.jaxrs.swagger.ApiDefaults;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.v2.SwaggerReader;
import com.holonplatform.jaxrs.swagger.v2.internal.DefaultSwaggerConfiguration;
import com.holonplatform.jaxrs.swagger.v2.internal.DefaultSwaggerReader;
import com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration;
import com.holonplatform.jaxrs.swagger.v2.internal.scanner.DefaultJaxrsApplicationAndAnnotationScanner;
import com.holonplatform.jaxrs.swagger.v2.internal.scanner.JaxrsScannerAdapter;

import io.swagger.jaxrs.config.DefaultReaderConfig;
import io.swagger.jaxrs.config.JaxrsScanner;
import io.swagger.jaxrs.config.SwaggerConfigLocator;
import io.swagger.models.Swagger;

/**
 * Default {@link JaxrsSwaggerApiContext} implementation.
 *
 * @since 5.2.0
 */
public class DefaultJaxrsSwaggerApiContext implements JaxrsSwaggerApiContext {

	private String id;
	private SwaggerConfiguration configuration;
	private JaxrsScanner scanner;
	private SwaggerReader reader;
	private Application application;

	private Set<String> resourcePackages;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.internal.context.SwaggerApiContext#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.internal.context.SwaggerApiContext#getConfiguration()
	 */
	@Override
	public SwaggerConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Set the API resources scanner.
	 * @param scanner the scanner to set
	 */
	public void setScanner(JaxrsScanner scanner) {
		this.scanner = scanner;
	}

	/**
	 * Set the API resources reader.
	 * @param reader the reader to set
	 */
	public void setReader(SwaggerReader reader) {
		this.reader = reader;
	}

	/**
	 * Set the context id.
	 * @param id the context id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Set the API configuration.
	 * @param configuration the API configuration to set
	 */
	public void setConfiguration(SwaggerConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Get the API scanner.
	 * @return the scanner
	 */
	public JaxrsScanner getScanner() {
		return scanner;
	}

	/**
	 * Get the API reader.
	 * @return the reader
	 */
	public SwaggerReader getReader() {
		return reader;
	}

	/**
	 * Get the JAX-RS application.
	 * @return the JAX-RS application
	 */
	public Application getApplication() {
		return application;
	}

	/**
	 * Set the JAX-RS application.
	 * @param application the application to set
	 */
	public void setApplication(Application application) {
		this.application = application;
	}

	/**
	 * Get the package names to use to filter the API resource classes.
	 * @return The resource packages
	 */
	public Set<String> getResourcePackages() {
		return resourcePackages;
	}

	/**
	 * Set the package names to use to filter the API resource classes.
	 * @param resourcePackages the resource packages to set
	 */
	public void setResourcePackages(Set<String> resourcePackages) {
		this.resourcePackages = resourcePackages;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.internal.context.SwaggerApiContext#init()
	 */
	@Override
	public void init() throws ApiConfigurationException {
		initAndRegister();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.internal.context.SwaggerApiContext#read()
	 */
	@Override
	public Swagger read() {
		// check cached
		Swagger api = SwaggerConfigLocator.getInstance().getSwagger(getContextId());
		if (api == null) {
			api = initAndRegister();
		}
		return api;
	}

	/**
	 * Init the context and register the API model and configuration using locators.
	 * @return The API model
	 */
	protected Swagger initAndRegister() {
		try {
			// context id
			String contextId = getContextId();
			// configuration
			SwaggerConfiguration config = getConfiguration();
			if (config == null) {
				config = new DefaultSwaggerConfiguration();
				config.setResourcePackages(getResourcePackages());
				if (getScanner() != null) {
					config.setPrettyPrint(getScanner().getPrettyPrint());
				}
				setConfiguration(config);
			} else {
				// check packages
				if (config.getResourcePackages() == null || config.getResourcePackages().isEmpty()) {
					config.setResourcePackages(getResourcePackages());
				}
			}
			// read and register
			Swagger swagger = scanAndRead(config, contextId);
			SwaggerConfigLocator.getInstance().putSwagger(contextId, swagger);
			SwaggerConfigLocator.getInstance().putConfig(contextId, config);
			return swagger;
		} catch (Exception e) {
			throw new ApiConfigurationException(e);
		}
	}

	/**
	 * Scan the API resource classes and read them to generate the API model.
	 * @param config The API configuration
	 * @param contextId The context id
	 * @return The API model
	 */
	protected Swagger scanAndRead(final SwaggerConfiguration config, String contextId) {
		JaxrsScanner scanner = getScanner();
		if (scanner == null) {
			scanner = new DefaultJaxrsApplicationAndAnnotationScanner(() -> config);
		}
		SwaggerReader reader = getReader();
		if (reader == null) {
			DefaultReaderConfig readerConfig = new DefaultReaderConfig();
			readerConfig.setScanAllResources(config.isReadAllResources());
			readerConfig.setIgnoredRoutes(config.getIgnoredRoutes());
			reader = new DefaultSwaggerReader(new Swagger(), readerConfig);
		}
		final JaxrsScanner adaptedScanner = JaxrsScannerAdapter.adapt(scanner, contextId);
		final SwaggerReader adaptedReader = new SwaggerReaderAdapter(reader);
		// scan
		Set<Class<?>> classes = adaptedScanner.classesFromContext(getApplication(), null);
		// filter classes
		if (config.getResourcePackages() != null && !config.getResourcePackages().isEmpty()) {
			classes = filter(classes, config.getResourcePackages().stream()
					.filter(p -> p != null && !p.trim().equals("")).collect(Collectors.toSet()));
		}
		// read
		final Swagger api = adaptedReader.read(classes);
		if (api.getExternalDocs() == null) {
			api.setExternalDocs(config.getExternalDocs());
		}
		return config.configure(api);
	}

	/**
	 * Filter given classes using the provided package names.
	 * @param classes The API resource classes
	 * @param packages The admitted package names
	 * @return The filtered classes
	 */
	protected Set<Class<?>> filter(Set<Class<?>> classes, Set<String> packages) {
		Set<Class<?>> output = new HashSet<>();
		for (Class<?> cls : classes) {
			for (String pkg : packages) {
				if (cls.getPackage().getName().startsWith(pkg)) {
					output.add(cls);
				}
			}
		}
		return output;
	}

	/**
	 * Get the context id.
	 * @return The context id, or {@link ApiDefaults#DEFAULT_CONTEXT_ID} if not configured
	 */
	protected String getContextId() {
		if (getId() == null) {
			return ApiDefaults.DEFAULT_CONTEXT_ID;
		}
		return getId();
	}

}
