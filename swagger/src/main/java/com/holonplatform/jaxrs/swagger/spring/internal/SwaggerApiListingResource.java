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
package com.holonplatform.jaxrs.swagger.spring.internal;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiOperation;

/**
 * JAX-RS resource to provide Swagger API documentation.
 * 
 * @since 5.0.0
 */
public class SwaggerApiListingResource extends AbstractApiListingResource {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, "application/yaml" })
	@ApiOperation(value = "Swagger API documentation in either JSON or YAML", hidden = true)
	public Response getListing(@QueryParam("type") String type) {
		if (StringUtils.isNotBlank(type) && type.trim().equalsIgnoreCase("yaml")) {
			return asYaml();
		} else {
			return asJson();
		}
	}

}
