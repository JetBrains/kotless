## Kotless DSL

Kotless DSL is a set of interfaces to various subsystems helping you
to build your serverless application.

It includes:
* HTTP API - DSL to create HTTP-based applications
* Lifecycle API - interfaces to control and extend lambda lifecycle
* Permissions API - annotations to grant permissions to resources in a declarative way

Kotless DSL is used during preprocessing and in a runtime. For example, HTTP API
contains `@Get` annotation which will be used in a preprocessing time to generate HTTP routes
deployment description and in a runtime to dispatch requests to it.

Number of provided APIs will continue to grow with growth of Kotless itself. Furthermore, each
API may grow to support more use cases.

