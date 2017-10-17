/*
 * Copyright 2016-2017 Axioma srl.
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

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * A Spring Boot condition to check if the <code>holon.swagger.resourcePackage</code> or the
 * <code>holon.swagger.apiGroups.*</code> configuration properties are available.
 * 
 * @since 5.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class SwaggerApiAutoDetectCondition extends SpringBootCondition {

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.boot.autoconfigure.condition.SpringBootCondition#getMatchOutcome(org.springframework.context.
	 * annotation.ConditionContext, org.springframework.core.type.AnnotatedTypeMetadata)
	 */
	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		final RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(context.getEnvironment(),
				"holon.swagger.");
		if (resolver.containsProperty("resourcePackage")) {
			return ConditionOutcome.noMatch(
					ConditionMessage.forCondition("SwaggerApiAutoDetectCondition").available("resourcePackage"));
		}
		Map<String, Object> ag = resolver.getSubProperties("apiGroups");
		if (ag != null && ag.size() > 0) {
			return ConditionOutcome
					.noMatch(ConditionMessage.forCondition("SwaggerApiAutoDetectCondition").available("apiGroups"));
		}
		return ConditionOutcome.match();
	}

}
