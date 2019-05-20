# Holon platform JAX-RS module

> Latest release: [5.2.4](#obtain-the-artifacts)

This is the __JAX-RS__ module of the [Holon Platform](https://holon-platform.com), which provides support, components and configuration helpers concerning the [JAX-RS](https://github.com/jax-rs/spec/blob/master/spec.pdf) - _Java API for RESTful Web Service_ standard.

The module main features are:

* A _JAX-RS_ implementation of the core platform `RestClient` API, a complete and easy to use _RESTful web services_ Java client, including an __asynchronous__ and a __reactive__ (using Project Reactor) version.
* A set of standard _JAX-RS_ components to implement server-side API __authentication and authorization__ using platform core APIs and services, such as `Realm` and `AuthContext`, in addition to standard `javax.annotation.security` standard annotations.
* A complete integration with [Swagger](http://swagger.io) _OpenAPI Specification_, __both for version 2 and 3__, including support for the `PropertyBox` core platform data container interface (to be exposed as a Swagger _Model_ definition) and for Swagger API listing endpoints (both in _JSON_ and _YAML_ formats) configuration.
* __Spring Boot__ auto-configuration classes to automatically register suitable __Spring beans__ (for example beans annotated with `@Path` or `@Provider`) as resources in a _JAX-RS_ compliant server. 
* __Spring Boot__ auto-configuration artifact to automatically enable and configure a [Resteasy](http://resteasy.jboss.org) server with Spring integration.
* A set of __Spring Boot starters__ to setup _JAX-RS_ compliant clients and servers:
	* Using either [Jersey](https://github.com/jersey) or [Resteasy](http://resteasy.jboss.org) as _JAX-RS_ implementation.
	* Using either [Jackson](http://wiki.fasterxml.com/JacksonHome) or [Gson](https://github.com/google/gson) as JSON provider
	* Using either [Tomcat](http://tomcat.apache.org) or [Undertow](http://undertow.io) as embedded servlet container.

See the module [documentation](https://docs.holon-platform.com/current/reference/holon-jaxrs.html) for details.

Just like any other platform module, this artifact is part of the [Holon Platform](https://holon-platform.com) ecosystem, but can be also used as a _stand-alone_ library.

See [Getting started](#getting-started) and the [platform documentation](https://docs.holon-platform.com/current/reference) for further details.

## At-a-glance overview

_JAX-RS Property model support:_
```java
// Property model
public interface Subject {
		
	static final NumericProperty<Long> ID = NumericProperty.longType("id");
	static final StringProperty NAME = StringProperty.create("name");
	static final StringProperty SURNAME = StringProperty.create("surname");

	static final PropertySet<?> SUBJECT = PropertySet.of(ID, NAME, SURNAME);
		
	static final DataTarget<?> TARGET = DataTarget.named("subjects");

}

// JAX-RS endpoint
@Path("/subjects")
public class SubjectEndpoint {

	@Inject
	private Datastore datastore;
		
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PropertyBox> getSubjects() {
		return datastore.query().target(TARGET).list(SUBJECT);
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSubject(@PathParam("id") Long id) {
		return datastore.query().target(TARGET).filter(ID.eq(id)).findOne(SUBJECT)
		.map(p -> Response.ok(p).build())
		.orElse(Response.status(Status.NOT_FOUND).build());
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addSubject(@PropertySetRef(Subject.class) PropertyBox subject) {
		datastore.save(TARGET, subject, DefaultWriteOption.BRING_BACK_GENERATED_IDS);
		return Response.created(URI.create("/api/subjects/" + subject.getValue(ID))).build();
	}

}
```

_JAX-RS API documentation using Swagger:_
```yaml
holon:
  swagger:
    path: 'docs'
    title: 'API title'
    version: 'v1'
```
```java
@Path("example")
@Component
public class Endpoint {
	/* operations omitted */
}
```

_JAX-RS authentication using JWT and Spring Boot:_
```yaml
holon:
  jwt:
    signature-algorithm: HS256
    sharedkey-base64: eWGZLlCrUjtBZwxgzcLPnA
    expire-hours: 1
    issuer: example-issuer
```
```java		
// Ream with JWT authentication support
@Bean
public Realm realm(JwtConfiguration jwtConfiguration) {
  return Realm.builder().resolver(AuthenticationToken.httpBearerResolver())
  	.authenticator(JwtAuthenticator.builder().configuration(jwtConfiguration).build())
  	.withDefaultAuthorizer().build();
}

// Protected JAX-RS endpoint
@Authenticate
@Path("/api/protected")
public class ApiEndpoint {

	@RolesAllowed("ROLE1")
	@GET
	@Path("/operation")
	public Response userOperation(@Context SecurityContext securityContext) {

		// principal name
		String principalName = securityContext.getUserPrincipal().getName();
		// authentication from JSON Web Token
		Authentication auth = (Authentication) securityContext.getUserPrincipal();

		return Response.ok(principalName).build();
	}

}
```

_JAX-RS RestClient:_
```java
RestClient client = RestClient.forTarget("https://host/api");
		
Optional<TestData> data = client.request().path("data/{id}").resolve("id", 1)
	.accept(MediaType.APPLICATION_JSON).getForEntity(TestData.class);

Optional<PropertyBox> value = client.request().path("get_property_box_json")
	.propertySet(SUBJECT).getForEntity(PropertyBox.class);

ResponseEntity<PropertyBox> response = client.request().path("get_property_box_json")
	.propertySet(SUBJECT).get(PropertyBox.class);

List<PropertyBox> values = client.request().path("get_property_boxes_json")
	.propertySet(SUBJECT).getAsList(PropertyBox.class);

ResponseEntity<Void> postResponse = client.request().path("postbox")
	.post(RequestEntity.json(PropertyBox.builder(SUBJECT).set(ID, 1).set(NAME, "Test").build()));
```

_JAX-RS Asynchronous RestClient (since version 5.2):_
```java
AsyncRestClient client = AsyncRestClient.forTarget("https://host/api");
		
CompletionStage<Optional<TestData>> data = client.request().path("data/{id}").resolve("id", 1)
	.accept(MediaType.APPLICATION_JSON).getForEntity(TestData.class);

CompletionStage<Optional<PropertyBox>> value = client.request().path("get_property_box_json")
	.propertySet(SUBJECT).getForEntity(PropertyBox.class);

CompletionStage<ResponseEntity<PropertyBox>> response = client.request().path("get_property_box_json")
	.propertySet(SUBJECT).get(PropertyBox.class);

CompletionStage<List<PropertyBox>> values = client.request().path("get_property_boxes_json")
	.propertySet(SUBJECT).getAsList(PropertyBox.class);

CompletionStage<ResponseEntity<Void>> postResponse = client.request().path("postbox")
	.post(RequestEntity.json(PropertyBox.builder(SUBJECT).set(ID, 1).set(NAME, "Test").build()));
```

_JAX-RS Reactive RestClient (since version 5.2):_
```java
ReactiveRestClient client = ReactiveRestClient.forTarget("https://host/api");
		
Mono<TestData> data = client.request().path("data/{id}").resolve("id", 1)
	.accept(MediaType.APPLICATION_JSON).getForEntity(TestData.class);

Mono<PropertyBox> value = client.request().path("get_property_box_json")
	.propertySet(SUBJECT).getForEntity(PropertyBox.class);

Mono<ReactiveResponseEntity<PropertyBox>> response = client.request().path("get_property_box_json")
	.propertySet(SUBJECT).get(PropertyBox.class);

Flux<PropertyBox> values = client.request().path("get_property_boxes_json")
	.propertySet(SUBJECT).getAsList(PropertyBox.class);

Mono<ReactiveResponseEntity<Void>> postResponse = client.request().path("postbox")
	.post(RequestEntity.json(PropertyBox.builder(SUBJECT).set(ID, 1).set(NAME, "Test").build()));
```

See the [module documentation](https://docs.holon-platform.com/current/reference/holon-jaxrs.html) for the user guide and a full set of examples.

## Code structure

See [Holon Platform code structure and conventions](https://github.com/holon-platform/platform/blob/master/CODING.md) to learn about the _"real Java API"_ philosophy with which the project codebase is developed and organized.

## Getting started

### System requirements

The Holon Platform is built using __Java 8__, so you need a JRE/JDK version 8 or above to use the platform artifacts.

The __JAX-RS__ specification version __2.0 or above__ is required.

This module is tested against [Jersey](https://github.com/jersey) version __2.x__ and [Resteasy](http://resteasy.jboss.org) version __3.x__.

### Releases

See [releases](https://github.com/holon-platform/holon-jaxrs/releases) for the available releases. Each release tag provides a link to the closed issues.

#### 5.2.x release notes

See [What's new in version 5.2.x](https://docs.holon-platform.com/current/reference/holon-jaxrs.html#WhatsNew52x) to learn about the new features and API operations of the 5.2 minor release.

### Obtain the artifacts

The [Holon Platform](https://holon-platform.com) is open source and licensed under the [Apache 2.0 license](LICENSE.md). All the artifacts (including binaries, sources and javadocs) are available from the [Maven Central](https://mvnrepository.com/repos/central) repository.

The Maven __group id__ for this module is `com.holon-platform.jaxrs` and a _BOM (Bill of Materials)_ is provided to obtain the module artifacts:

_Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform.jaxrs</groupId>
        <artifactId>holon-jaxrs-bom</artifactId>
        <version>5.2.4</version>
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
`holon-jaxrs-client-reactor` | Reactive `RestClient` API JAX-RS implementation using Project Reactor
`holon-jaxrs-server` | JAX-RS server-side __authentication and authorization__ features using platform core APIs and services, such as `Realm` and `AuthContext`
`holon-jaxrs-swagger-core` | Core Swagger integration API and annotations
`holon-jaxrs-swagger-v2` | [Swagger](http://swagger.io) specification version 2 integration: JAX-RS API listing endpoints auto-configuration with `PropertyBox` support
`holon-jaxrs-swagger-v3` | [Swagger/OpenAPI](http://swagger.io) specification version 3 integration: JAX-RS API listing endpoints auto-configuration with `PropertyBox` support
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
