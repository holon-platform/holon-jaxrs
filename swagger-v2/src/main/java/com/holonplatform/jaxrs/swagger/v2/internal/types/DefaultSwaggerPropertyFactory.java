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
package com.holonplatform.jaxrs.swagger.v2.internal.types;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Map.Entry;

import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.BuiltinValidator;
import com.holonplatform.core.internal.ValidatorDescriptor;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyValueConverter.PropertyConversionException;
import com.holonplatform.core.temporal.TemporalType;

import io.swagger.converter.ModelConverters;
import io.swagger.models.Model;
import io.swagger.models.Swagger;
import io.swagger.models.properties.AbstractNumericProperty;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.DateTimeProperty;
import io.swagger.models.properties.StringProperty;

/**
 * Default {@link SwaggerPropertyFactory} implementation.
 *
 * @since 5.0.0
 */
public enum DefaultSwaggerPropertyFactory implements SwaggerPropertyFactory {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.swagger.internal.SwaggerPropertyFactory#create(io.swagger.models.Swagger,
	 * com.holonplatform.core.property.Property)
	 */
	@Override
	public io.swagger.models.properties.Property create(Swagger swagger, Property<?> property)
			throws PropertyConversionException {

		ObjectUtils.argumentNotNull(property, "Property must be not null");

		// temporals
		if (TypeUtils.isDate(property.getType()) || TypeUtils.isCalendar(property.getType())) {
			TemporalType type = property.getConfiguration().getTemporalType().orElse(TemporalType.DATE);
			io.swagger.models.properties.Property p;
			if (type == TemporalType.DATE) {
				p = new DateProperty();
			} else {
				p = new DateTimeProperty();
			}
			configureProperty(p, property);
			return p;
		}

		if (LocalDate.class.isAssignableFrom(property.getType())) {
			DateProperty p = new DateProperty();
			configureProperty(p, property);
			return p;
		}
		if (LocalDateTime.class.isAssignableFrom(property.getType())) {
			DateTimeProperty p = new DateTimeProperty();
			configureProperty(p, property);
			return p;
		}
		if (OffsetDateTime.class.isAssignableFrom(property.getType())) {
			DateTimeProperty p = new DateTimeProperty();
			configureProperty(p, property);
			return p;
		}

		// dft
		io.swagger.models.properties.Property p = ModelConverters.getInstance().readAsProperty(property.getType());
		if (p != null) {
			configureProperty(p, property);

			// check model definitions
			if (swagger != null) {
				Map<String, Model> models = ModelConverters.getInstance().readAll(property.getType());
				if (models != null) {
					for (Entry<String, Model> entry : models.entrySet()) {
						if (swagger.getDefinitions() != null && !swagger.getDefinitions().containsKey(entry.getKey())) {
							swagger.addDefinition(entry.getKey(), entry.getValue());
						}
					}
				}
			}

			return p;
		}
		return null;
	}

	private static void configureProperty(io.swagger.models.properties.Property property, Property<?> source) {
		// read-only
		if (source.isReadOnly()) {
			property.setReadOnly(Boolean.TRUE);
		}
		// i18n
		String message = LocalizationContext.translate(source, true);
		if (message != null) {
			property.setTitle(message);
		}
		// validators
		source.getValidators().forEach(v -> {
			if (v instanceof BuiltinValidator) {
				((BuiltinValidator<?>) v).getDescriptor().ifPresent(d -> {
					configurePropertyValidation(property, d);
				});
			}
		});
	}

	private static void configurePropertyValidation(io.swagger.models.properties.Property property,
			ValidatorDescriptor d) {
		// required
		if (d.isRequired()) {
			property.setRequired(true);
		}
		// min
		if (d.getMin() != null) {
			if (property instanceof AbstractNumericProperty) {
				(((AbstractNumericProperty) property)).setMinimum(new BigDecimal(d.getMin().doubleValue()));
				(((AbstractNumericProperty) property)).setExclusiveMinimum(d.isExclusiveMin());
			} else if (property instanceof StringProperty) {
				((StringProperty) property)
						.minLength(d.isExclusiveMin() ? d.getMin().intValue() + 1 : d.getMin().intValue());
			} else if (property instanceof ArrayProperty) {
				((ArrayProperty) property)
						.setMinItems(d.isExclusiveMin() ? d.getMin().intValue() + 1 : d.getMin().intValue());
			}
		}
		// max
		if (d.getMax() != null) {
			if (property instanceof AbstractNumericProperty) {
				(((AbstractNumericProperty) property)).setMaximum(new BigDecimal(d.getMax().doubleValue()));
				(((AbstractNumericProperty) property)).setExclusiveMaximum(d.isExclusiveMax());
			} else if (property instanceof StringProperty) {
				((StringProperty) property)
						.maxLength(d.isExclusiveMax() ? d.getMax().intValue() - 1 : d.getMax().intValue());
			} else if (property instanceof ArrayProperty) {
				((ArrayProperty) property)
						.setMaxItems(d.isExclusiveMax() ? d.getMax().intValue() - 1 : d.getMax().intValue());
			}
		}
		// pattern
		if (d.getPattern() != null) {
			if (property instanceof StringProperty) {
				((StringProperty) property).setPattern(d.getPattern());
			}
		}
		// email
		if (d.isEmail()) {
			if (property instanceof StringProperty) {
				((StringProperty) property).setFormat("email");
			}
		}
	}

}
