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
package com.holonplatform.jaxrs.client.internal;

import java.io.InputStream;
import java.util.Optional;

import javax.ws.rs.core.Response;

import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.http.exceptions.HttpEntityProcessingException;
import com.holonplatform.http.rest.ResponseEntity;
import com.holonplatform.http.rest.ResponseType;

/**
 * JAX-RS <em>raw</em> {@link ResponseEntity} implementation, which returns a <code>byte[]</code> as payload.
 * 
 * @since 5.0.0
 */
public class JaxrsRawResponseEntity extends JaxrsResponseEntity<byte[]> {

	/**
	 * Constructor
	 * @param response JAX-RS response (not null)
	 */
	public JaxrsRawResponseEntity(Response response) {
		super(response, ResponseType.of(byte[].class));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.client.internal.JaxrsResponseEntity#getPayload()
	 */
	@SuppressWarnings("resource")
	@Override
	public Optional<byte[]> getPayload() throws UnsupportedOperationException {
		if (getResponse().hasEntity()) {
			try {
				InputStream is = getResponse().readEntity(InputStream.class);
				if (is != null) {
					return Optional.ofNullable(ConversionUtils.convertInputStreamToBytes(is));
				}
			} catch (Exception e) {
				// check zero-lenght response content
				if (isNoContentException(e)) {
					return Optional.empty();
				}
				throw new HttpEntityProcessingException("Failed to read HTTP entity input stream", e);
			}
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.jaxrs.client.internal.JaxrsResponseEntity#getPayloadType()
	 */
	@Override
	public Class<? extends byte[]> getPayloadType() throws UnsupportedOperationException {
		return byte[].class;
	}

}
