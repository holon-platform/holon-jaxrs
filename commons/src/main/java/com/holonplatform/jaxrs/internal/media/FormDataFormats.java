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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

/**
 * Utility class to manage data formats using <code>application/x-www-form-urlencoded</code> media type.
 * 
 * @since 5.0.0
 */
public final class FormDataFormats implements Serializable {

	private static final long serialVersionUID = 4239757839791285002L;

	private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
	private static final String TIME_FORMAT_PATTERN = "HH:mm:ss";

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.US);
	public static final DateFormat TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT_PATTERN, Locale.US);
	public static final DateFormat DATETIME_FORMAT = new SimpleDateFormat(
			DATE_FORMAT_PATTERN + "'T'" + TIME_FORMAT_PATTERN, Locale.US);

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN, Locale.US);
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN, Locale.US);
	public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter
			.ofPattern(DATE_FORMAT_PATTERN + "'T'" + TIME_FORMAT_PATTERN, Locale.US);

	public static final DateTimeFormatter ANSI_OFFSET_ID_FORMATTER = new DateTimeFormatterBuilder()
			.parseCaseInsensitive().appendOffsetId().toFormatter();

	public static final NumberFormat INTEGER_FORMAT = NumberFormat.getIntegerInstance(Locale.US);
	public static final NumberFormat DECIMAL_FORMAT = NumberFormat.getNumberInstance(Locale.US);

	static {
		INTEGER_FORMAT.setGroupingUsed(false);
		INTEGER_FORMAT.setParseIntegerOnly(true);
		DECIMAL_FORMAT.setGroupingUsed(false);
	}

	private FormDataFormats() {
	}

}
