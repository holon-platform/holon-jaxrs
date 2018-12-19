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

import java.util.Set;

import javax.ws.rs.core.Application;

import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.exceptions.ApiConfigurationException;
import com.holonplatform.jaxrs.swagger.v2.SwaggerReader;
import com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration;
import com.holonplatform.jaxrs.swagger.v2.internal.scanner.DefaultJaxrsAnnotationScanner;
import com.holonplatform.jaxrs.swagger.v2.internal.scanner.DefaultJaxrsApplicationAndAnnotationScanner;
import com.holonplatform.jaxrs.swagger.v2.internal.scanner.DefaultJaxrsApplicationScanner;

import io.swagger.jaxrs.config.JaxrsScanner;

/**
 * Default {@link JaxrsSwaggerApiContextBuilder} implementation.
 *
 * @since 5.2.0
 */
public class DefaultJaxrsSwaggerApiContextBuilder implements JaxrsSwaggerApiContextBuilder {

	private final DefaultJaxrsSwaggerApiContext context;

	public DefaultJaxrsSwaggerApiContextBuilder() {
		super();
		this.context = new DefaultJaxrsSwaggerApiContext();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContextBuilder#contextId(java.lang.String)
	 */
	@Override
	public JaxrsSwaggerApiContextBuilder contextId(String contextId) {
		context.setId(contextId);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContextBuilder#configuration(com.holonplatform
	 * .jaxrs.swagger.v2.internal.SwaggerConfiguration)
	 */
	@Override
	public JaxrsSwaggerApiContextBuilder configuration(SwaggerConfiguration configuration) {
		context.setConfiguration(configuration);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContextBuilder#resourcePackages(java.util.Set)
	 */
	@Override
	public JaxrsSwaggerApiContextBuilder resourcePackages(Set<String> resourcePackages) {
		context.setResourcePackages(resourcePackages);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContextBuilder#scannerType(com.holonplatform.
	 * jaxrs.swagger.JaxrsScannerType)
	 */
	@Override
	public JaxrsSwaggerApiContextBuilder scannerType(JaxrsScannerType scannerType) {
		if (scannerType != null) {
			switch (scannerType) {
			case ANNOTATION:
				context.setScanner(new DefaultJaxrsAnnotationScanner(() -> context.getConfiguration()));
				break;
			case APPLICATION:
				context.setScanner(new DefaultJaxrsApplicationScanner(() -> context.getConfiguration()));
				break;
			case APPLICATION_AND_ANNOTATION:
				context.setScanner(new DefaultJaxrsApplicationAndAnnotationScanner(() -> context.getConfiguration()));
				break;
			case DEFAULT:
				context.setScanner(null);
				break;
			default:
				break;
			}
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContextBuilder#scanner(io.swagger.jaxrs.config
	 * .JaxrsScanner)
	 */
	@Override
	public JaxrsSwaggerApiContextBuilder scanner(JaxrsScanner scanner) {
		context.setScanner(scanner);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContextBuilder#reader(com.holonplatform.jaxrs.
	 * swagger.v2.SwaggerReader)
	 */
	@Override
	public JaxrsSwaggerApiContextBuilder reader(SwaggerReader reader) {
		context.setReader(reader);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContextBuilder#application(javax.ws.rs.core.
	 * Application)
	 */
	@Override
	public JaxrsSwaggerApiContextBuilder application(Application application) {
		context.setApplication(application);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContextBuilder#build(boolean)
	 */
	@Override
	public JaxrsSwaggerApiContext build(boolean initialize) throws ApiConfigurationException {
		if (initialize) {
			context.init();
		}
		return context;
	}

}
