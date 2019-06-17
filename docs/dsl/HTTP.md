## HTTP Server API

Kotless DSL provides simple, but yet powerful DSL to create HTTP-based
applications. It includes annotations to create HTTP routes, deploy 
static files, extensible serialization, HTTP request interceptors
and rich links API. 

We will take a look at these features one by one.

### Dynamic

Dynamic HTTP API of Kotless includes everything related to dynamic
processing of HTTP requests. Basically it is everything excluding
static files located right at S3.

#### Routes

Kotless supports `Get` and `Post` function annotations. Each function
annotated with them will create new HTTP route with appropriate method.

Functions may have parameters. Deserialization of parameters, if it is 
primitive types, will be done automatically. Also, you can [extend
deserialization](#Serialization) to support non-primitive types as function parameters.

Serialization of the result also will be done automatically. Nevertheless, it is
possible to return data as `HttpResponse` -- in this case no serialization will 
be performed.

Here is the simple snippet of dynamic `Get` route: 

```kotlin
@Get("/")
fun root(): String {
    return "Hello world!"
}
```

#### Context

During processing of HTTP request `KotlessContext.HTTP` object is available
for user. 

Context includes current HTTP request processed at field `request`. 

Note, that call to `request` may throw `KotlinNullPointerException` if this
call is performed not in context of HTTP request processing (basically, not 
in an HTTP route)

#### Interceptor

Kotless will pass all HTTP requests through interceptors. Each interceptor
is a Kotlin static `object` implementing interface `HttpRequestInterceptor`.

In case there is more than one interceptor, Kotless will pass request to
first of them (ascending order of priority) and will pass as `next` continuation
pipeline of next interceptors.

Here is the simple snippet of request interceptor, which is checking that
header auth token is valid:

```kotlin
object AuthInterceptor: HttpRequestInterceptor {
    override val priority = 0

    override fun intercept(request: HttpRequest, key: RouteKey, next: (HttpRequest, RouteKey) -> HttpResponse): HttpResponse {
        if (AuthTokens.isValid(request.headers["auth"])) {
            //Pass request to next interceptor or handler, if authentication is valid
            return next(request, key)
        }
        //Redirect to login if authentication is not valid
        return redirect("/login")
    }
}
```

#### Serialization

Kotless uses `ConversionService` implementations to deserialize parameters of
request and serialize them in links. `DefaultConversionService` supports
serialization and deserialization of primitive Kotlin types. 

In case you need to support custom types deserialization, you will need to
create Kotlin static `object` implementing interface `ConversionService`.

Here is the simple snippet, which deserialize entities by ID from a database:

```kotlin
object EntityConversionService: ConversionService {
    override fun convertFrom(value: String, type: Type): Any {
        if (type !is Class<*>) throw ConversionException("Type is not supported")
        if (Entity::class.java.isAssignableFrom(type)) {
            return Database.load(value.toLong())
        }
        throw ConversionException("Type is not supported")
    }

    override fun convertTo(value: Any): String {
        if (value is Entity) {
            return value.id
        }
        throw ConversionException("Type is not supported")
    }
}
```


#### Links

Kotless provides extension functions to create links (href-s) to Kotless-based routes.

Basically, there are two types of such links - base links, without any parameters and
prepared links with parameters already embedded in a link itself.

Here is the simple snippet creating base and prepared link (pretend that exists annotated
function `root(str: String)` in object `API`)

```kotlin
val baseLink = API::root.href

val preparedLink = API::root.href("parameter")
```

### Static

Static HTTP API of Kotless is, basically, a convenient way of creating HTTP routes
which are served via static files. 

To create static route you need to use `@StaticGet` annotation.

Note, that this annotation has severe limitations on an annotated object.

Under annotation should be a property, which value is instantiated directly 
with `java.io.File(String)` constructor and path should be relative to 
`workingDir` of Kotless.

Here is the simple snippet of static route creation:

```kotlin
@StaticGet("/file.css", MimeType.CSS)
val exampleCss = File("example.css")
```

Links API also works for static routes, but only base link can be constructed:

```kotlin
val baseLink = ::exampleCss.href
```

