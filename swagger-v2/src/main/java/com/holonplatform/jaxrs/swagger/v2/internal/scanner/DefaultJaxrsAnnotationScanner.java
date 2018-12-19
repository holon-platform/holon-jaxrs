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
package com.holonplatform.jaxrs.swagger.v2.internal.scanner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import javax.ws.rs.core.Application;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.holonplatform.jaxrs.swagger.v2.internal.SwaggerConfiguration;

import io.swagger.annotations.SwaggerDefinition;

public class DefaultJaxrsAnnotationScanner extends AbstractJaxrsScanner {

	public DefaultJaxrsAnnotationScanner(Supplier<SwaggerConfiguration> configurationSupplier) {
		super(configurationSupplier);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.v2.internal.scanner.AbstractJaxrsScanner#scan(javax.ws.rs.core.Application)
	 */
	@Override
	protected Set<Class<?>> scan(Application application) {

		final ConfigurationBuilder config = new ConfigurationBuilder();

		final Set<String> acceptablePackages = new HashSet<>();

		getConfiguration().map(cfg -> cfg.getResourcePackages()).orElse(Collections.emptySet()).forEach(pkg -> {
			if (!isIgnored(pkg)) {
				acceptablePackages.add(pkg);
				config.addUrls(ClasspathHelper.forPackage(pkg));
			}
		});

		config.filterInputsBy(new FilterBuilder().exclude(".*json").exclude(".*yaml"));
		config.setScanners(new ResourcesScanner(), new TypeAnnotationsScanner(), new SubTypesScanner());
		final Reflections reflections;
		reflections = new Reflections(config);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(javax.ws.rs.Path.class);
		classes.addAll(reflections.getTypesAnnotatedWith(SwaggerDefinition.class));

		Set<Class<?>> output = new HashSet<>();
		for (Class<?> cls : classes) {
			for (String pkg : acceptablePackages) {
				if (cls.getPackage().getName().startsWith(pkg)) {
					output.add(cls);
				}
			}
		}
		return output;
		
	}

}
