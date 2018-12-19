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
package com.holonplatform.jaxrs.swagger.v2.test;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;

import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.v2.internal.DefaultSwaggerConfiguration;
import com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContext;
import com.holonplatform.jaxrs.swagger.v2.internal.context.JaxrsSwaggerApiContextBuilder;
import com.holonplatform.jaxrs.swagger.v2.internal.scanner.DefaultJaxrsApplicationScanner;
import com.holonplatform.jaxrs.swagger.v2.test.model.AbstractTestResource;
import com.holonplatform.jaxrs.swagger.v2.test.resources.context2.Resource2;
import com.holonplatform.jaxrs.swagger.v2.test.resources.context3.Resource3;
import com.holonplatform.jaxrs.swagger.v2.test.utils.SwaggerValidation;

import io.swagger.models.Info;
import io.swagger.models.Swagger;

public class TestOpenApiContextScanner {

	@Path("resource1")
	private static class TestResource1 extends AbstractTestResource {

	}

	@Test
	public void testApplicationScanner() {

		final String id = TestOpenApiContextScanner.class.getName() + "_1";

		final DefaultSwaggerConfiguration configuration = new DefaultSwaggerConfiguration();
		configuration.setInfo(new Info().title("Title of " + id).version("1"));

		final ResourceConfig application = new ResourceConfig();
		application.register(TestResource1.class);
		application.register(Resource2.class);
		application.register(Resource3.class);

		final DefaultJaxrsApplicationScanner scanner = new DefaultJaxrsApplicationScanner(() -> configuration);

		final JaxrsSwaggerApiContext apiContext = JaxrsSwaggerApiContextBuilder.create().application(application)
				.scanner(scanner).configuration(configuration).contextId(id).build(true);

		Swagger api = apiContext.read();
		SwaggerValidation.validateApi(api, "Title of " + id, 25);

	}

	@Test
	public void testApplicationScannerConfigPackages() {

		final String id = TestOpenApiContextScanner.class.getName() + "_3";

		final DefaultSwaggerConfiguration configuration = new DefaultSwaggerConfiguration();
		configuration.setInfo(new Info().title("Title of " + id).version("1"));

		Set<String> rps = new HashSet<>();
		rps.add(Resource2.class.getPackage().getName());
		configuration.setResourcePackages(rps);

		final ResourceConfig application = new ResourceConfig();
		application.register(TestResource1.class);
		application.register(Resource2.class);
		application.register(Resource3.class);

		final DefaultJaxrsApplicationScanner scanner = new DefaultJaxrsApplicationScanner(() -> configuration);

		final JaxrsSwaggerApiContext apiContext = JaxrsSwaggerApiContextBuilder.create().application(application)
				.scanner(scanner).configuration(configuration).contextId(id).build(true);

		Swagger api = apiContext.read();
		SwaggerValidation.validateApi(api, "Title of " + id, 1);

	}

	@Test
	public void testApplicationScannerConfigPackagesMulti() {

		final String id = TestOpenApiContextScanner.class.getName() + "_4";

		final DefaultSwaggerConfiguration configuration = new DefaultSwaggerConfiguration();
		configuration.setInfo(new Info().title("Title of " + id).version("1"));

		Set<String> rps = new HashSet<>();
		rps.add(Resource2.class.getPackage().getName());
		rps.add(Resource3.class.getPackage().getName());
		configuration.setResourcePackages(rps);

		final ResourceConfig application = new ResourceConfig();
		application.register(TestResource1.class);
		application.register(Resource2.class);
		application.register(Resource3.class);

		final DefaultJaxrsApplicationScanner scanner = new DefaultJaxrsApplicationScanner(() -> configuration);

		final JaxrsSwaggerApiContext apiContext = JaxrsSwaggerApiContextBuilder.create().application(application)
				.scanner(scanner).configuration(configuration).contextId(id).build(true);

		Swagger api = apiContext.read();
		SwaggerValidation.validateApi(api, "Title of " + id, 2);

	}

	@Test
	public void testScannerType() {

		final String id = TestOpenApiContextScanner.class.getName() + "_5";

		final DefaultSwaggerConfiguration configuration = new DefaultSwaggerConfiguration();
		configuration.setInfo(new Info().title("Title of " + id).version("1"));

		final ResourceConfig application = new ResourceConfig();
		application.register(TestResource1.class);
		application.register(Resource2.class);
		application.register(Resource3.class);

		final JaxrsSwaggerApiContext apiContext = JaxrsSwaggerApiContextBuilder.create().application(application)
				.configuration(configuration).contextId(id).scannerType(JaxrsScannerType.APPLICATION).build(true);

		Swagger api = apiContext.read();
		SwaggerValidation.validateApi(api, "Title of " + id, 25);

	}

}
