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
package com.holonplatform.jaxrs.swagger.v3.test.resources.context2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.jaxrs.swagger.v3.test.model.Model1;

@Component
@Path("resource2")
public class Resource2 {

	@GET
	@Path("test21")
	@Produces(MediaType.APPLICATION_JSON)
	public @PropertySetRef(Model1.class) PropertyBox test21() {
		return PropertyBox.builder(Model1.PROPERTIES).set(Model1.ID, 1).build();
	}

}
