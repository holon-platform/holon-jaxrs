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
package com.holonplatform.jaxrs.spring.boot.resteasy;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto configuration class to provide a default {@link ResteasyConfig} bean instance when no other
 * is declared as a Spring bean.
 * 
 * @since 5.0.0
 */
@AutoConfiguration
@ConditionalOnClass(ResteasyConfig.class)
@AutoConfigureBefore(ResteasyAutoConfiguration.class)
public class ResteasyConfigAutoConfiguration {

	@Configuration
	@ConditionalOnMissingBean(ResteasyConfig.class)
	static class ResteasyApplicationConfiguration {

		@Bean
		public ResteasyConfig resteasyApplicationConfig() {
			return new ResteasyConfig();
		}

	}

}
