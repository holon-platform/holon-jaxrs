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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.Property.PropertyWriteException;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.jaxrs.media.FormDataPropertyValueSerializer;

/**
 * Default {@link FormDataPropertyValueSerializer}.
 * 
 * @since 5.0.0
 */
public enum DefaultFormDataPropertyValueSerializer implements FormDataPropertyValueSerializer {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.jaxrs.media.FormDataPropertyValueSerializer#serialize(com.holonplatform.core.property.Property,
	 * java.lang.Object)
	 */
	@Override
	public String serialize(Property<?> property, Object value) throws PropertyWriteException {
		if (value != null) {

			try {

				// CharSequence
				if (TypeUtils.isCharSequence(value.getClass())) {
					return value.toString();
				}

				// Boolean
				if (TypeUtils.isBoolean(value.getClass())) {
					return Boolean.toString((Boolean) value);
				}

				// Number
				if (TypeUtils.isNumber(value.getClass())) {
					if (TypeUtils.isDecimalNumber(value.getClass())) {
						return FormDataFormats.DECIMAL_FORMAT.format(value);
					}
					return FormDataFormats.INTEGER_FORMAT.format(value);
				}

				// Enum (by default, ordinal value is used)
				if (TypeUtils.isEnum(value.getClass())) {
					return String.valueOf(((Enum<?>) value).ordinal());
				}

				// Date and times
				Optional<String> serializedDate = serializeDateTime(property, value);
				if (serializedDate.isPresent()) {
					return serializedDate.get();
				}

				// defaults to toString()
				return value.toString();

			} catch (Exception e) {
				throw new PropertyWriteException(property, e);
			}
		}

		return null;
	}

	private static Optional<String> serializeDateTime(Property<?> property, Object value) {
		if (value != null) {

			TemporalType temporalType = null;

			if (TypeUtils.isDate(value.getClass()) || TypeUtils.isCalendar(value.getClass())) {
				final Date date = TypeUtils.isCalendar(value.getClass()) ? ((Calendar) value).getTime() : (Date) value;
				temporalType = (property != null)
						? property.getConfiguration().getTemporalType().orElse(TemporalType.DATE_TIME)
						: TemporalType.DATE_TIME;

				LocalDate datePart = null;
				LocalTime timePart = null;

				switch (temporalType) {
				case DATE_TIME:
					datePart = ConversionUtils.toLocalDate(date);
					timePart = ConversionUtils.toLocalTime(date);
					break;
				case TIME:
					timePart = ConversionUtils.toLocalTime(date);
					break;
				case DATE:
				default:
					datePart = ConversionUtils.toLocalDate(date);
					break;
				}

				return Optional.of(serializeDateTimeValue(datePart, timePart, null));
			}

			if (TemporalAccessor.class.isAssignableFrom(value.getClass())) {

				LocalDate datePart = null;
				LocalTime timePart = null;
				ZoneOffset offset = null;

				if (value instanceof LocalDate) {
					temporalType = TemporalType.DATE;
					datePart = (LocalDate) value;
				} else if (value instanceof LocalTime) {
					temporalType = TemporalType.TIME;
					timePart = (LocalTime) value;
				} else if (value instanceof LocalDateTime) {
					temporalType = TemporalType.DATE_TIME;
					datePart = ((LocalDateTime) value).toLocalDate();
					timePart = ((LocalDateTime) value).toLocalTime();
				} else if (value instanceof OffsetTime) {
					temporalType = TemporalType.TIME;
					timePart = ((OffsetTime) value).toLocalTime();
					offset = ((OffsetTime) value).getOffset();
				} else if (value instanceof OffsetDateTime) {
					temporalType = TemporalType.DATE_TIME;
					datePart = ((OffsetDateTime) value).toLocalDate();
					timePart = ((OffsetDateTime) value).toLocalTime();
					offset = ((OffsetDateTime) value).getOffset();
				} else if (value instanceof ZonedDateTime) {
					temporalType = TemporalType.DATE_TIME;
					datePart = ((ZonedDateTime) value).toLocalDate();
					timePart = ((ZonedDateTime) value).toLocalTime();
					offset = ((ZonedDateTime) value).getOffset();
				}

				if (datePart != null || timePart != null) {
					LocalDate serializeDate = datePart;
					LocalTime serializeTime = timePart;

					if (temporalType != null) {
						if (temporalType == TemporalType.DATE) {
							serializeTime = null;
						} else if (temporalType == TemporalType.TIME) {
							serializeDate = null;
						}
					}

					return Optional.of(serializeDateTimeValue(serializeDate, serializeTime, offset));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Serialize a date/time value using given {@link LocalDate} part, {@link LocalTime} part and zone offset.
	 * @param datePart Date part
	 * @param timePart Time part
	 * @param offset Zone offset
	 * @return Serialized date/time value
	 */
	private static String serializeDateTimeValue(LocalDate datePart, LocalTime timePart, ZoneOffset offset) {
		final StringBuilder sb = new StringBuilder();

		boolean appendSeparator = false;
		if (datePart != null) {
			sb.append(FormDataFormats.DATE_FORMATTER.format(datePart));
			appendSeparator = true;
		}
		if (timePart != null) {
			if (appendSeparator) {
				sb.append("T");
			}
			sb.append(FormDataFormats.TIME_FORMATTER.format(timePart));

			if (offset != null) {
				sb.append(" ");
				sb.append(FormDataFormats.ANSI_OFFSET_ID_FORMATTER.format(offset));
			}
		}

		return sb.toString();
	}

}
