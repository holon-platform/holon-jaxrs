# Holon platform JAX-RS module

> Latest release: [5.1.2](#obtain-the-artifacts)

This is the __JAX-RS__ module of the [Holon Platform](https://holon-platform.com), which provides support, components and configuration helpers concerning the [JAX-RS](https://github.com/jax-rs/spec/blob/master/spec.pdf) - _Java API for RESTful Web Service_ standard.

The module main features are:

* A _JAX-RS_ implementation of the core platform `RestClient` API, a complete and easy to use _RESTful web services_ Java client.
* A set of standard _JAX-RS_ components to implement server-side API __authentication and authorization__ using platform core APIs and services, such as `Realm` and `AuthContext`, in addition to standard `javax.annotation.security` standard annotations.
* A complete integration with [Swagger](http://swagger.io) (_OpenAPI Specification_), including support for the `PropertyBox` core platform data container interface (to be exposed as a Swagger _Model_ definition) and for Swagger API listing endpoints (both in _JSON_ and _YAML_ formats) configuration.
* __Spring Boot__ auto-configuration classes to automatically register suitable __Spring beans__ (for example beans annotated with `@Path` or `@Provider`) as resources in a _JAX-RS_ compliant server. 
* __Spring Boot__ auto-configuration artifact to automatically enable and configure a [Resteasy](http://resteasy.jboss.org) server with Spring integration.
* A set of __Spring Boot starters__ to setup _JAX-RS_ compliant clients and servers:
	* Using either [Jersey](https://github.com/jersey) or [Resteasy](http://resteasy.jboss.org) as _JAX-RS_ implementation.
	* Using either [Jackson](http://wiki.fasterxml.com/JacksonHome) or [Gson](https://github.com/google/gson) as JSON provider
	* Using either [Tomcat](http://tomcat.apache.org) or [Undertow](http://undertow.io) as embedded servlet container.

See the module [documentation](https://docs.holon-platform.com/current/reference/holon-jaxrs.html) for details.

Just like any other platform module, this artifact is part of the [Holon Platform](https://holon-platform.com) ecosystem, but can be also used as a _stand-alone_ library.

See the [platform documentation](https://docs.holon-platform.com/current/reference) for further details.

## Code structure

See [Holon Platform code structure and conventions](https://github.com/holon-platform/platform/blob/master/CODING.md) to learn about the _"real Java API"_ philosophy with which the project codebase is developed and organized.

## Getting started

### System requirements

The Holon Platform is built using __Java 8__, so you need a JRE/JDK version 8 or above to use the platform artifacts.

The __JAX-RS__ specification version __2.0 or above__ is required.

This module is tested against [Jersey](https://github.com/jersey) version __2.x__ and [Resteasy](http://resteasy.jboss.org) version __3.x__.

### Releases

See [releases](https://github.com/holon-platform/holon-jaxrs/releases) for the available releases. Each release tag provides a link to the closed issues.

#### 5.1.x release notes

See [What's new in version 5.1.x](https://docs.holon-platform.com/current/reference/holon-jaxrs.html#WhatsNew51x) to learn about the new features and API operations of the 5.1 minor release.

### Obtain the artifacts

The [Holon Platform](https://holon-platform.com) is open source and licensed under the [Apache 2.0 license](LICENSE.md). All the artifacts (including binaries, sources and javadocs) are available from the [Maven Central](https://mvnrepository.com/repos/central) repository.

The Maven __group id__ for this module is `com.holon-platform.jaxrs` and a _BOM (Bill of Materials)_ is provided to obtain the module artifacts:

_Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform.jaxrs</groupId>
        <artifactId>holon-jaxrs-bom</artifactId>
        <version>5.1.2</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Using the Platform BOM

The [Holon Platform](https://holon-platform.com) provides an overall Maven _BOM (Bill of Materials)_ to easily obtain all the available platform artifacts:

_Platform Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform</groupId>
        <artifactId>bom</artifactId>
        <version>${platform-version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Build from sources

You can build the sources using Maven (version 3.3.x or above is recommended) like this: 

`mvn clean install`

## Getting help

* Check the [platform documentation](https://docs.holon-platform.com/current/reference) or the specific [module documentation](https://docs.holon-platform.com/current/reference/holon-jaxrs.html).

* Ask a question on [Stack Overflow](http://stackoverflow.com). We monitor the [`holon-platform`](http://stackoverflow.com/tags/holon-platform) tag.

* Report an [issue](https://github.com/holon-platform/holon-jaxrs/issues).

* A [commercial support](https://holon-platform.com/services) is available too.

## Examples

See the [Holon Platform examples](https://github.com/holon-platform/holon-examples) repository for a set of example projects.

## Contribute

See [Contributing to the Holon Platform](https://github.com/holon-platform/platform/blob/master/CONTRIBUTING.md).

[![Gitter chat](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/holon-platform/contribute?utm_source=share-link&utm_medium=link&utm_campaign=share-link) 
Join the __contribute__ Gitter room for any question and to contact us.

## License

All the [Holon Platform](https://holon-platform.com) modules are _Open Source_ software released under the [Apache 2.0 license](LICENSE).

## Artifacts list

Maven _group id_: `com.holon-platform.jaxrs`

Artifact id | Description
----------- | -----------
`holon-jaxrs-commons` | Common JAX-RS support classes
`holon-jaxrs-client` | JAX-RS implementation of the core platform `RestClient` API
`holon-jaxrs-server` | JAX-RS server-side __authentication and authorization__ features using platform core APIs and services, such as `Realm` and `AuthContext`
`holon-jaxrs-swagger` | [Swagger](http://swagger.io) configuration and JAX-RS API listing endpoints with `PropertyBox` support
`holon-jaxrs-spring-boot-client` | `JaxrsClientBuilder` __Spring Boot__ auto-configuration
`holon-jaxrs-spring-boot-jersey` | [Jersey](https://github.com/jersey) auto-configuration with automatic registration of Spring beans as JAX-RS resources 
`holon-jaxrs-spring-boot-resteasy` | [Resteasy](http://resteasy.jboss.org) auto-configuration with Spring integration
`holon-starter-jersey` | Spring Boot JAX-RS _server_ starter using __Jersey__, __Tomcat__ and __Jackson__ as JSON provider
`holon-starter-jersey-gson` | Spring Boot JAX-RS _server_ starter using __Jersey__, __Tomcat__ and __Gson__ as JSON provider
`holon-starter-jersey-undertow` | Spring Boot JAX-RS _server_ starter using __Jersey__, __Undertow__ and __Jackson__ as JSON provider
`holon-starter-jersey-undertow-gson` | Spring Boot JAX-RS _server_ starter using __Jersey__, __Undertow__ and __Gson__ as JSON provider
`holon-starter-jersey-client` | Spring Boot JAX-RS _client_ starter using __Jersey__ and __Jackson__ as JSON provider
`holon-starter-jersey-client-gson` | Spring Boot JAX-RS _client_ starter using __Jersey__ and __Gson__ as JSON provider
`holon-starter-resteasy` | Spring Boot JAX-RS _server_ starter using __Resteasy__, __Tomcat__ and __Jackson__ as JSON provider
`holon-starter-resteasy-gson` | Spring Boot JAX-RS _server_ starter using __Resteasy__, __Tomcat__ and __Gson__ as JSON provider
`holon-starter-resteasy-undertow` | Spring Boot JAX-RS _server_ starter using __Resteasy__, __Undertow__ and __Jackson__ as JSON provider
`holon-starter-resteasy-undertow-gson` | Spring Boot JAX-RS _server_ starter using __Resteasy__, __Undertow__ and __Gson__ as JSON provider
`holon-starter-resteasy-client` | Spring Boot JAX-RS _client_ starter using __Resteasy__ and __Jackson__ as JSON provider
`holon-starter-resteasy-client-gson` | Spring Boot JAX-RS _client_ starter using __Resteasy__ and __Gson__ as JSON provider
`holon-jaxrs-bom` | Bill Of Materials
`documentation-jaxrs` | Documentation
