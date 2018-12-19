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

import java.util.HashSet;
import java.util.Set;

import com.holonplatform.jaxrs.swagger.v2.internal.endpoints.AbstractSwaggerEndpoint;

public class IgnoredPackages {

	public static final Set<String> ignored = new HashSet<>();

    static {
        ignored.add("io.swagger.jaxrs.listing");
        ignored.add("org.glassfish.jersey");
        ignored.add("org.jboss.resteasy");
        ignored.add("com.sun.jersey");
        ignored.add("com.fasterxml.jackson");
        ignored.add("com.fasterxml.jackson");
        ignored.add(AbstractSwaggerEndpoint.class.getPackage().getName());
    }
	
}
