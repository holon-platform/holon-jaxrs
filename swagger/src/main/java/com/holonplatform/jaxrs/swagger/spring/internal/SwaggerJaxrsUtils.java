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
package com.holonplatform.jaxrs.swagger.spring.internal;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;

import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.jaxrs.swagger.internal.ApiGroupId;
import com.holonplatform.jaxrs.swagger.internal.SwaggerApiListingResource;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationException;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties;
import com.holonplatform.jaxrs.swagger.spring.SwaggerConfigurationProperties.ApiGroupConfiguration;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

/**
 * Swagger extension utility class.
 *
 * @since 5.0.0
 */
public final class SwaggerJaxrsUtils implements Serializable {

	private static final long serialVersionUID = 1199929442212057271L;

	private SwaggerJaxrsUtils() {
	}

	/**
	 * Build a Swagger API listing JAX-RS endpoint class, binding it to given <code>path</code> using standard JAX-RS
	 * {@link Path} annotation.
	 * @param classLoader ClassLoader to use to create the class proxy
	 * @param apiGroupId API group id
	 * @param path Endpoint path
	 * @param rolesAllowed Optional security roles for endpoint authorization
	 * @return The Swagger API listing JAX-RS endpoint class proxy
	 */
	public static Class<?> buildApiListingEndpoint(ClassLoader classLoader, String apiGroupId, String path,
			String[] rolesAllowed) {
		String configId = (apiGroupId != null && !apiGroupId.trim().equals("")) ? apiGroupId
				: ApiGroupId.DEFAULT_GROUP_ID;
		final ClassLoader cl = (classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader();
		DynamicType.Builder<SwaggerApiListingResource> builder = new ByteBuddy()
				.subclass(SwaggerApiListingResource.class)
				.annotateType(AnnotationDescription.Builder.ofType(Path.class).define("value", path).build())
				.annotateType(AnnotationDescription.Builder.ofType(ApiGroupId.class).define("value", configId).build());
		if (rolesAllowed != null && rolesAllowed.length > 0) {
			builder.annotateType(AnnotationDescription.Builder.ofType(RolesAllowed.class)
					.defineArray("value", rolesAllowed).build());
		}
		return builder.make().load(cl, ClassLoadingStrategy.Default.INJECTION).getLoaded();
	}

	/**
	 * Compose a path
	 * @param basePath Base path
	 * @param path Additinal path
	 * @return Composed path
	 */
	public static String composePath(String basePath, String path) {
		StringBuilder ap = new StringBuilder();
		if (basePath != null) {
			if (!basePath.startsWith("/")) {
				ap.append('/');
			}
			ap.append(basePath);
		}
		if (path != null) {
			final String prefix = ap.toString();
			if (prefix != null && !prefix.endsWith("/")) {
				ap.append('/');
			}
			if (path.startsWith("/")) {
				ap.append(path.substring(1));
			} else {
				ap.append(path);
			}
		}
		return ap.toString();
	}

	/**
	 * Get the {@link ApiGroupConfiguration} which corresponds to given <code>groupId</code>.
	 * @param cfg Swagger configuration properties
	 * @param groupId Group id
	 * @return the {@link ApiGroupConfiguration} which corresponds to given <code>groupId</code>, <code>null</code> if
	 *         not found
	 */
	public static ApiGroupConfiguration getApiGroup(SwaggerConfigurationProperties cfg, String groupId) {
		if (groupId != null) {
			List<ApiGroupConfiguration> groups = cfg.getApiGroups();
			if (groups != null && !groups.isEmpty()) {
				for (ApiGroupConfiguration group : groups) {
					if (groupId.equals(group.getGroupId())) {
						return group;
					}
				}
				// check default
				if (ApiGroupId.DEFAULT_GROUP_ID.equals(groupId)) {
					for (ApiGroupConfiguration group : groups) {
						if (group.getGroupId() == null) {
							return group;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Get the {@link ApiListingDefinition}s from given Swagger configuration properties.
	 * @param cfg Swagger configuration properties
	 * @return The API listing definitions, an empty list if none
	 */
	public static List<ApiListingDefinition> getApiListings(SwaggerConfigurationProperties cfg) {
		List<ApiListingDefinition> definitions = new LinkedList<>();
		if (cfg != null) {
			List<ApiGroupConfiguration> groups = cfg.getApiGroups();
			if (groups != null && !groups.isEmpty()) {
				for (ApiGroupConfiguration group : groups) {
					ApiListingDefinition definition = ApiListingDefinition.create(group.getGroupId(), cfg);
					ApiListingDefinition exists = getByGroupId(definitions, definition.getGroupId());
					if (exists != null) {
						throw new SwaggerConfigurationException("Duplicate API group id: " + exists.getGroupId());
					}
					definitions.add(definition);
				}
			} else {
				// default group
				if (cfg.getResourcePackage() != null && !cfg.getResourcePackage().trim().equals("")) {
					definitions.add(ApiListingDefinition.createDefault(cfg));
				}
			}
		}
		return definitions;
	}

	private static ApiListingDefinition getByGroupId(List<ApiListingDefinition> definitions, String groupId) {
		String gid = (groupId != null && !groupId.trim().equals("")) ? groupId : ApiGroupId.DEFAULT_GROUP_ID;
		for (ApiListingDefinition d : definitions) {
			if (gid.equals(d.getGroupId())) {
				return d;
			}
		}
		return null;
	}

}
