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
package com.holonplatform.jaxrs.internal.media;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.Property.PropertyReadException;
import com.holonplatform.jaxrs.media.FormDataPropertyValueDeserializer;

/**
 * Default {@link FormDataPropertyValueDeserializer}.
 * 
 * @since 5.0.0
 */
public enum DefaultFormDataPropertyValueDeserializer implements FormDataPropertyValueDeserializer {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.media.FormDataPropertyValueDeserializer#deserialize(com.holonplatform.core.property.
	 * Property, java.lang.String)
	 */
	@Override
	public Object deserialize(Property<?> property, String value) throws PropertyReadException {
		if (value != null) {
			if (property == null) {
				throw new PropertyReadException(property, "Null property");
			}
			try {
				return deserialize(property, property.getType(), value);
			} catch (PropertyReadException e) {
				throw e;
			} catch (Exception e) {
				throw new PropertyReadException(property, e);
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Object deserialize(Property<?> property, Class<?> targetType, String value) throws ParseException {

		// string
		if (TypeUtils.isString(targetType)) {
			return value;
		}

		// boolean
		if (TypeUtils.isBoolean(targetType)) {
			return Boolean.parseBoolean(value);
		}

		// enum
		if (TypeUtils.isEnum(targetType)) {
			// try by ordinal
			try {
				int ordinal = Integer.parseInt(value);
				return ConversionUtils.convertEnumValue((Class<Enum>) targetType, ordinal);
			} catch (@SuppressWarnings("unused") NumberFormatException e) {
				// ignore
			}
			return ConversionUtils.convertEnumValue((Class<Enum>) targetType, value);
		}

		// number
		if (TypeUtils.isIntegerNumber(targetType)) {
			Number parsed = FormDataFormats.INTEGER_FORMAT.parse(value);
			return ConversionUtils.convertNumberToTargetClass(parsed, (Class<Number>) targetType);
		}
		if (TypeUtils.isDecimalNumber(targetType)) {
			Number parsed = FormDataFormats.DECIMAL_FORMAT.parse(value);
			return ConversionUtils.convertNumberToTargetClass(parsed, (Class<Number>) targetType);
		}

		// date and times
		if (TypeUtils.isDate(targetType)) {
			return parseDateValue(property, value);
		}
		if (TypeUtils.isCalendar(targetType)) {
			Date d = parseDateValue(property, value);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			return calendar;
		}

		if (LocalDate.class.isAssignableFrom(targetType)) {
			return LocalDate.parse(value, FormDataFormats.DATE_FORMATTER);
		}
		if (LocalDateTime.class.isAssignableFrom(targetType)) {
			return LocalDateTime.parse(value, FormDataFormats.DATETIME_FORMATTER);
		}
		if (LocalTime.class.isAssignableFrom(targetType)) {
			return LocalTime.parse(value, FormDataFormats.TIME_FORMATTER);
		}

		return null;
	}

	private static java.util.Date parseDateValue(Property<?> property, String value) {
		if (property.getConfiguration().getTemporalType().isPresent()) {
			switch (property.getConfiguration().getTemporalType().get()) {
			case DATE:
				try {
					return FormDataFormats.getDateFormat().parse(value);
				} catch (@SuppressWarnings("unused") ParseException e) {
					// ignore
				}
				break;
			case DATE_TIME:
				try {
					return FormDataFormats.getDateTimeFormat().parse(value);
				} catch (@SuppressWarnings("unused") ParseException e) {
					// ignore
				}
				break;
			case TIME:
				try {
					return FormDataFormats.getTimeFormat().parse(value);
				} catch (@SuppressWarnings("unused") ParseException e) {
					// ignore
				}
				break;
			default:
				break;
			}
		}

		try {
			return FormDataFormats.getDateTimeFormat().parse(value);
		} catch (@SuppressWarnings("unused") ParseException e) {
			// try date only
			try {
				return FormDataFormats.getDateFormat().parse(value);
			} catch (@SuppressWarnings("unused") ParseException e1) {
				// try time only
				try {
					return FormDataFormats.getTimeFormat().parse(value);
				} catch (@SuppressWarnings("unused") ParseException e2) {
					// ignore
				}
			}
		}
		throw new PropertyReadException(property, "Failed to parse value [" + value + "] as valid Date value");
	}

}
