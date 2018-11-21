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
package com.holonplatform.jaxrs.examples;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.holonplatform.jaxrs.swagger.annotations.ApiDefinition;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

public class ExampleSwagger {

	// tag::swagger1[]
	@Api // <1>
	@ApiDefinition(title = "Example title") // <2>
	@Path("example")
	@Component // <3>
	class ExampleEndpoint1 {

		@ApiOperation("Test description") // <4>
		@GET
		@Path("test")
		@Produces(MediaType.TEXT_PLAIN)
		public String test() {
			return "test";
		}

	}
	// end::swagger1[]

	// tag::swagger2[]
	@Api
	@ApiDefinition("/docs") // <1>
	@Path("example")
	@Component
	class ExampleEndpoint2 {

		@ApiOperation("Test description")
		@GET
		@Path("test")
		@Produces(MediaType.TEXT_PLAIN)
		public String test() {
			return "test";
		}

	}
	// end::swagger2[]

	// tag::swagger3[]
	@Api
	@ApiDefinition(value = "/docs1", title = "Title 1") // <1>
	@Path("example3a")
	@Component
	class ExampleEndpoint3a {

		/* operations omitted */

	}

	@Api
	@ApiDefinition(value = "/docs1") // <2>
	@Path("example3b")
	@Component
	class ExampleEndpoint3b {

		/* operations omitted */

	}

	@Api
	@ApiDefinition(value = "/docs2", title = "Title 2") // <3>
	@Path("example3c")
	@Component
	class ExampleEndpoint3c {

		/* operations omitted */

	}
	// end::swagger3[]

}
