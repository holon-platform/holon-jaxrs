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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.property.PropertySetRefIntrospector;
import com.holonplatform.core.internal.property.PropertySetRefIntrospector.PropertySetIntrospectionException;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.Property.PropertyAccessException;
import com.holonplatform.core.property.Property.PropertyReadException;
import com.holonplatform.core.property.Property.PropertyWriteException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertySetRef;
import com.holonplatform.jaxrs.media.FormDataPropertyValueDeserializer;
import com.holonplatform.jaxrs.media.FormDataPropertyValueSerializer;

/**
 * A {@link MessageBodyReader} and {@link MessageBodyWriter} to handle {@link PropertyBox} instances using
 * <code>application/x-www-form-urlencoded</code> media type.
 * 
 * @since 5.0.0
 */
@Produces(MediaType.APPLICATION_FORM_URLENCODED)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class PropertyBoxFormDataProvider implements MessageBodyWriter<PropertyBox>, MessageBodyReader<PropertyBox> {

	private static final Charset CHARSET = StandardCharsets.UTF_8;

	@Context
	private Providers providers;

	private FormDataPropertyValueSerializer _serializer;

	private FormDataPropertyValueDeserializer _deserializer;

	private PropertySetRefIntrospector _propertySetRefIntrospector;

	/**
	 * Get the {@link PropertySetRefIntrospector} instance to use.
	 * @return The {@link PropertySetRefIntrospector} instance to use, from {@link ContextResolver} if available or the
	 *         default one
	 */
	private PropertySetRefIntrospector getPropertySetRefIntrospector() {
		if (_propertySetRefIntrospector == null) {
			// init using a contextresolver, if available
			ContextResolver<PropertySetRefIntrospector> contextResolver = providers
					.getContextResolver(PropertySetRefIntrospector.class, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
			if (contextResolver != null) {
				_propertySetRefIntrospector = contextResolver.getContext(PropertySetRefIntrospector.class);
			}
			if (_propertySetRefIntrospector == null) {
				// use default
				_propertySetRefIntrospector = PropertySetRefIntrospector.getDefault();
			}
		}
		return _propertySetRefIntrospector;
	}

	/**
	 * Get the {@link FormDataPropertyValueSerializer} instance to use.
	 * @return The {@link FormDataPropertyValueSerializer} instance to use, from {@link ContextResolver} if available or
	 *         the default one
	 */
	private FormDataPropertyValueSerializer getSerializer() {
		if (_serializer == null) {
			// init using a contextresolver, if available
			ContextResolver<FormDataPropertyValueSerializer> contextResolver = providers.getContextResolver(
					FormDataPropertyValueSerializer.class, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
			if (contextResolver != null) {
				_serializer = contextResolver.getContext(FormDataPropertyValueSerializer.class);
			}
			if (_serializer == null) {
				// use default
				_serializer = FormDataPropertyValueSerializer.getDefault();
			}
		}
		return _serializer;
	}

	/**
	 * Get the {@link FormDataPropertyValueDeserializer} instance to use.
	 * @return The {@link FormDataPropertyValueDeserializer} instance to use, from {@link ContextResolver} if available
	 *         or the default one
	 */
	private FormDataPropertyValueDeserializer getDeserializer() {
		if (_deserializer == null) {
			// init using a contextresolver, if available
			ContextResolver<FormDataPropertyValueDeserializer> contextResolver = providers.getContextResolver(
					FormDataPropertyValueDeserializer.class, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
			if (contextResolver != null) {
				_deserializer = contextResolver.getContext(FormDataPropertyValueDeserializer.class);
			}
			if (_deserializer == null) {
				// use default
				_deserializer = FormDataPropertyValueDeserializer.getDefault();
			}
		}
		return _deserializer;
	}

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return isPropertyBoxType(type.equals(genericType) ? type : genericType);
	}

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType, jakarta.ws.rs.core.MultivaluedMap,
	 * java.io.InputStream)
	 */
	@Override
	public PropertyBox readFrom(Class<PropertyBox> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		try (InputStreamReader isr = new InputStreamReader(entityStream, CHARSET);
				BufferedReader reader = new BufferedReader(isr)) {

			// read into String
			StringBuffer content = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}

			// check property set
			PropertySet<?> propertySet = null;
			if (!com.holonplatform.core.Context.get().resource(PropertySet.CONTEXT_KEY, PropertySet.class)
					.isPresent()) {
				PropertySetRef propertySetRef = PropertySetRefIntrospector.getPropertySetRef(annotations).orElse(null);
				if (propertySetRef != null) {
					try {
						propertySet = getPropertySetRefIntrospector().getPropertySet(propertySetRef);
					} catch (PropertySetIntrospectionException e) {
						throw new WebApplicationException(e.getMessage(), e, Status.INTERNAL_SERVER_ERROR);
					}
				}
			}
			if (propertySet != null) {
				return propertySet.execute(() -> readPropertyBox(content.toString()));
			} else {
				return readPropertyBox(content.toString());
			}

		}
	}

	/**
	 * Read given <code>application/x-www-form-urlencoded</code> type value into a {@link PropertyBox}.
	 * @param encoded Value to read
	 * @return PropertyBox with current {@link com.holonplatform.core.Context} property set initialized with the values
	 *         read from given form data
	 * @throws IOException If a PropertySet is missing
	 * @throws WebApplicationException If a property read error occurred
	 */
	private PropertyBox readPropertyBox(String encoded) throws IOException, WebApplicationException {
		if (encoded != null && !encoded.trim().equals("")) {
			PropertySet<?> propertySet = com.holonplatform.core.Context.get()
					.resource(PropertySet.CONTEXT_KEY, PropertySet.class)
					.orElseThrow(() -> new IOException("Missing PropertySet instance to build a PropertyBox. "
							+ "A PropertySet instance must be available as context resource to perform PropertyBox deserialization."));

			try {
				final PropertyBox.Builder builder = PropertyBox.builder(propertySet).invalidAllowed(true);

				// decode
				String[] pairs = encoded.split("\\&");
				if (pairs != null) {
					for (String pair : pairs) {
						String[] fields = pair.split("=");
						if (fields != null && fields.length > 1) {
							final String name = decode(fields[0]);
							if (name != null) {
								final String value = decode(fields[1]);
								getPropertyByName(propertySet, name).ifPresent(p -> {
									builder.set(p, deserialize(p, value));
								});
							}
						}
					}
				}

				return builder.build();

			} catch (PropertyAccessException e) {
				throw new WebApplicationException(e.getMessage(), e, Status.BAD_REQUEST);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return isPropertyBoxType(type.equals(genericType) ? type : genericType);
	}

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType)
	 */
	@Override
	public long getSize(PropertyBox t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type,
	 * java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType, jakarta.ws.rs.core.MultivaluedMap,
	 * java.io.OutputStream)
	 */
	@Override
	public void writeTo(final PropertyBox t, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		try (final Writer writer = new OutputStreamWriter(entityStream, CHARSET)) {
			try {
				if (t != null) {

					// encode
					String value = t.stream().filter(p -> p instanceof Path)
							.filter(p -> t.getValue((Property<?>) p) != null)
							.map(p -> encode(p, ((Path<?>) p).getName()) + "="
									+ encode(p, serialize(p, t.getValue((Property<?>) p))))
							.collect(Collectors.joining("&"));
					if (value != null) {
						writer.write(value);
					}

				}
			} catch (PropertyAccessException e) {
				throw new WebApplicationException(e.getMessage(), e, Status.BAD_REQUEST);
			} catch (Exception e) {
				throw new IOException("Failed to encode given PropertyBox as " + MediaType.APPLICATION_FORM_URLENCODED
						+ ": [" + ((t != null) ? t.toString() : "NULL") + "]", e);
			}
		}
	}

	/**
	 * Serialize given property <code>value</code> to <code>application/x-www-form-urlencoded</code> media type.
	 * @param property Property to serialize
	 * @param value Value to serialize
	 * @return Serialized value
	 * @throws PropertyWriteException If an error occurred
	 */
	private String serialize(final Property<?> property, final Object value) throws PropertyWriteException {
		if (value != null) {
			return getSerializer().serialize(property, value);
		}
		return null;
	}

	/**
	 * Deserialize given property <code>value</code> from <code>application/x-www-form-urlencoded</code> media type.
	 * @param property Property to deserialize
	 * @param value Value to deserialize
	 * @return Deserialized value
	 * @throws PropertyReadException If an error occurred
	 */
	@SuppressWarnings("unchecked")
	private <T> T deserialize(final Property<?> property, final String value) throws PropertyReadException {
		if (value != null) {
			return (T) getDeserializer().deserialize(property, value);
		}
		return null;
	}

	/**
	 * URL-encode given value using {@link #CHARSET}.
	 * @param value Value to encode
	 * @return Encoded value
	 * @throws IOException Encoding not supported
	 */
	private static String encode(final Property<?> property, final String value) throws PropertyWriteException {
		if (value != null) {
			try {
				return URLEncoder.encode(value, CHARSET.name());
			} catch (UnsupportedEncodingException e) {
				throw new PropertyWriteException(property, "Falied to encode value [" + value + "]", e);
			}
		}
		return null;
	}

	/**
	 * URL-decode given value using {@link #CHARSET}.
	 * @param value Value to decode
	 * @return Decoded value
	 * @throws IOException Encoding not supported
	 */
	private static String decode(final String value) throws IOException {
		if (value != null) {
			try {
				return URLDecoder.decode(value, CHARSET.name());
			} catch (UnsupportedEncodingException e) {
				throw new IOException("Falied to decode value [" + value + "]", e);
			}
		}
		return null;
	}

	/**
	 * Get the {@link Property} in given <code>propertySet</code> which corresponds to given <code>name</code>.
	 * @param propertySet Property set
	 * @param name Property name
	 * @return The {@link Property} in given <code>propertySet</code> which corresponds to given <code>name</code>, if
	 *         available
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Optional<Property<?>> getPropertyByName(PropertySet propertySet, String name) {
		if (propertySet != null && name != null) {
			return propertySet.stream().filter(p -> p instanceof Path).filter(p -> name.equals(((Path<?>) p).getName()))
					.findFirst();
		}
		return Optional.empty();
	}

	/**
	 * Checks whether given <code>type</code> is a {@link PropertyBox} type.
	 * @param type Type to check
	 * @return <code>true</code> if given <code>type</code> is a {@link PropertyBox} type
	 */
	private static boolean isPropertyBoxType(Type type) {
		if (type != null) {
			if (PropertyBox.class == type) {
				return true;
			}
			if (type instanceof Class && PropertyBox.class.isAssignableFrom((Class<?>) type)) {
				return true;
			}
		}
		return false;
	}

}
