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
package com.holonplatform.jaxrs.swagger.v3.test;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;

import com.holonplatform.jaxrs.swagger.JaxrsScannerType;
import com.holonplatform.jaxrs.swagger.v3.OpenApi;
import com.holonplatform.jaxrs.swagger.v3.internal.scanner.JaxrsApplicationResourcesScanner;
import com.holonplatform.jaxrs.swagger.v3.test.model.AbstractTestResource;
import com.holonplatform.jaxrs.swagger.v3.test.resources.context2.Resource2;
import com.holonplatform.jaxrs.swagger.v3.test.resources.context3.Resource3;
import com.holonplatform.jaxrs.swagger.v3.test.utils.OpenApiValidation;

import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class TestOpenApiContextScanner {

	@Path("resource1")
	private static class TestResource1 extends AbstractTestResource {

	}

	@Test
	public void testApplicationScanner() {

		final String id = TestOpenApiContextScanner.class.getName() + "_1";

		final SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration.setOpenAPI(new OpenAPI().info(new Info().title("Title of " + id).version("1")));

		final ResourceConfig application = new ResourceConfig();
		application.register(TestResource1.class);
		application.register(Resource2.class);
		application.register(Resource3.class);

		final JaxrsApplicationResourcesScanner scanner = new JaxrsApplicationResourcesScanner();
		scanner.setConfiguration(configuration);
		scanner.setApplication(application);

		final OpenApiContext openApiContext = OpenApi.contextBuilder().application(application).scanner(scanner)
				.configuration(configuration).contextId(id).build(true);

		OpenAPI api = openApiContext.read();
		OpenApiValidation.validateApi(api, "Title of " + id, 25);

	}

	@Test
	public void testApplicationScannerConfigClasses() {

		final String id = TestOpenApiContextScanner.class.getName() + "_2";

		final SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration.setOpenAPI(new OpenAPI().info(new Info().title("Title of " + id).version("1")));

		Set<String> rcs = new HashSet<>();
		rcs.add(TestResource1.class.getName());
		rcs.add(Resource2.class.getName());
		configuration.setResourceClasses(rcs);

		final ResourceConfig application = new ResourceConfig();
		application.register(TestResource1.class);
		application.register(Resource2.class);
		application.register(Resource3.class);

		final JaxrsApplicationResourcesScanner scanner = new JaxrsApplicationResourcesScanner();
		scanner.setConfiguration(configuration);
		scanner.setApplication(application);

		final OpenApiContext openApiContext = OpenApi.contextBuilder().application(application).scanner(scanner)
				.configuration(configuration).contextId(id).build(true);

		OpenAPI api = openApiContext.read();
		OpenApiValidation.validateApi(api, "Title of " + id, 24);

	}

	@Test
	public void testApplicationScannerConfigPackages() {

		final String id = TestOpenApiContextScanner.class.getName() + "_3";

		final SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration.setOpenAPI(new OpenAPI().info(new Info().title("Title of " + id).version("1")));

		Set<String> rps = new HashSet<>();
		rps.add(Resource2.class.getPackage().getName());
		configuration.setResourcePackages(rps);

		final ResourceConfig application = new ResourceConfig();
		application.register(TestResource1.class);
		application.register(Resource2.class);
		application.register(Resource3.class);

		final JaxrsApplicationResourcesScanner scanner = new JaxrsApplicationResourcesScanner();
		scanner.setConfiguration(configuration);
		scanner.setApplication(application);

		final OpenApiContext openApiContext = OpenApi.contextBuilder().application(application).scanner(scanner)
				.configuration(configuration).contextId(id).build(true);

		OpenAPI api = openApiContext.read();
		OpenApiValidation.validateApi(api, "Title of " + id, 1);

	}

	@Test
	public void testApplicationScannerConfigPackagesMulti() {

		final String id = TestOpenApiContextScanner.class.getName() + "_4";

		final SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration.setOpenAPI(new OpenAPI().info(new Info().title("Title of " + id).version("1")));

		Set<String> rps = new HashSet<>();
		rps.add(Resource2.class.getPackage().getName());
		rps.add(Resource3.class.getPackage().getName());
		configuration.setResourcePackages(rps);

		final ResourceConfig application = new ResourceConfig();
		application.register(TestResource1.class);
		application.register(Resource2.class);
		application.register(Resource3.class);

		final JaxrsApplicationResourcesScanner scanner = new JaxrsApplicationResourcesScanner();
		scanner.setConfiguration(configuration);
		scanner.setApplication(application);

		final OpenApiContext openApiContext = OpenApi.contextBuilder().application(application).scanner(scanner)
				.configuration(configuration).contextId(id).build(true);

		OpenAPI api = openApiContext.read();
		OpenApiValidation.validateApi(api, "Title of " + id, 2);

	}

	@Test
	public void testScannerType() {

		final String id = TestOpenApiContextScanner.class.getName() + "_5";

		final SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration.setOpenAPI(new OpenAPI().info(new Info().title("Title of " + id).version("1")));

		final ResourceConfig application = new ResourceConfig();
		application.register(TestResource1.class);
		application.register(Resource2.class);
		application.register(Resource3.class);

		final OpenApiContext openApiContext = OpenApi.contextBuilder().application(application)
				.configuration(configuration).contextId(id).scannerType(JaxrsScannerType.APPLICATION).build(true);

		OpenAPI api = openApiContext.read();
		OpenApiValidation.validateApi(api, "Title of " + id, 25);

	}

}
