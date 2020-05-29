package io.kotless.examples.site.pages

import io.kotless.examples.site.bootstrap.doc
import io.kotless.examples.site.bootstrap.kotlin
import kotlinx.html.*

object DSLPages {
    fun overview() = doc {
        h1 {
            +"Overview"
        }

        p {
            +"""Kotless DSL is a set of interfaces to various subsystems helping you
                to build your serverless application."""
        }

        p {
            +"It includes:"
        }

        ul {
            li {
                +"HTTP API - DSL to create HTTP-based applications"
            }
            li {
                +"Lifecycle API - interfaces to control and extend lambda lifecycle"
            }
            li {
                +"Permissions API - annotations to grant permissions to resources in a declarative way"
            }
        }

        p {
            +"""Kotless DSL is used during preprocessing and in a runtime. For example, HTTP API
                contains `@Get` annotation, which will be used in a preprocessing time to generate HTTP routes
                deployment description and in a runtime to dispatch requests to it."""
        }

        p {
            +"""Number of provided APIs will continue to grow with growth of Kotless itself. Furthermore, each
                API may grow to support more use cases."""
        }
    }

    fun lifecycle() = doc {
        h1 {
            +"Lifecycle API"
        }

        p {
            +"""Kotless DSL provides an interface to control and extend lambda lifecycle.
                It includes extension points for warming and initialization sequences."""
        }

        h3 {
            +"Warming"
        }

        p {
            +"""Kotless lambdas can be autowarmed. It means, that some scheduler will
                periodically (by default, each 5 minutes) call lambda to be sure, that
                it will not be displaced from hot pool of cloud provider."""
        }

        p {
            +"""Each call executes warming sequence. This sequence consists of all
                existing Kotlin static `object`-s implementing interface `LambdaWarming`."""
        }

        p {
            +"Here is a simple snippet of HTTP connection warming:"
        }

        kotlin("""
                object DbKeepAlive: LambdaWarming {
                    override fun warmup() {
                        Database.sendHeartBeat()
                    }
                }"""
        )

        p {
            +"""Note: by default warming will trigger only initialization of Kotless
                application, if it was not already initialized."""
        }

        h3 {
            +"Initialization"
        }

        p {
            +"On first call (or during first warm-up) Kotless will perform initialization."
        }

        p {
            +"""During initialization Kotless application scans code for Kotless resources
                (like `LambdaWarming`, `ConversionService` and routes) and registers them."""
        }

        p {
            +"""After this Kotless will call `init` on existing Kotlin static `object`-s
                implementing interface `LambdaInit`. Call is guaranteed to happen only once."""
        }

        p {
            +"You may use `LambdaInit` to prepare application before first execution."
        }
    }


    fun permissions() = doc {
        h1 {
            +"Permissions API"
        }

        p {
            +"""Kotless DSL provides annotations to bind access to objects in your code
                with permissions granting on the side of cloud provider."""
        }

        h3 {
            +"Resource annotations"
        }

        p {
            +"""Resource annotations are, basically, annotations for classes, Kotlin static
                `object`-s, functions and properties, which states that access to this particular
                element in Kotlin requires permissions to stated in annotation cloud provider resource."""
        }

        p {
            +"""Here is the simple code snippet, defining Kotlin static `object`, which access DynamoDB
                table with resource annotations permitting such access on AWS side:"""
        }

        kotlin("""
                //Storage have read and write access, so we grant both
                @DynamoDBTable("example-table", PermissionLevel.ReadWrite)
                object Storage {
                    private val table = DynamoTable("example-table")

                    fun add(id: String, value: String) {
                        table.add(id = id, value = mapOf("value" to value))
                    }

                    fun get(id: String): String {
                        return table.get(id = id)["value"]
                    }
                }"""
        )

        p {
            +"Each usage of `Storage` object will grant permission to code in which it was used."
        }

        p {
            +"""It means, that if you use `Storage` object in HTTP route, than lambda serving this route
                will have all permissions defined by annotation."""
        }

        p {
            +"Using resource annotations wisely in your code you will not need to worry about permissions at all."
        }

        h3 {
            +"Permission level"
        }

        p {
            +"""Permissions are granted to one of 3 groups of operations: Read, Write and ReadWrite.
                From the name of each group is pretty clear what operations are covered with it."""
        }

        p {
            +"""Note, that all modification/configuration operations are also covered by Write permission.
                Batch operations included in the same group with single call operations.
                """
        }

        p {
            +"""Permissions were grouped due to two reasons. First of all, we wanted to abstract
                permissions from specific cloud provider. Secondly, permissions in AWS (and in other
                cloud providers) are very complex - we wanted to hide this complexity from the end
                user, so we sacrificed granularity to simplicity.
                """
        }
    }


    fun http() = doc {
        h1 {
            +"HTTP API"
        }

        p {
            +"""Kotless DSL provides simple, but yet powerful DSL to create HTTP-based
                applications. It includes annotations to create HTTP routes, deploy
                static files, extensible serialization, HTTP request interceptors
                and rich links API."""

        }

        p {
            +"We will take a look at these features one by one."
        }

        h2 {
            +"Dynamic"
        }

        p {
            +"""Dynamic HTTP API of Kotless includes everything related to dynamic
                processing of HTTP requests. Basically it is everything excluding
                static files located right at S3."""
        }

        h3 {
            +"Routes"
        }

        p {
            +"""Kotless supports `Get` and `Post` function annotations. Each function
                annotated with them will create new HTTP route with appropriate method."""
        }

        p {
            +"""Functions may have parameters. Deserialization of parameters, in case of
                    primitive types, will be done automatically. Also, you can """
            a(href = "#Serialization") {
                +"extend deserialization "
            }
            +"to support non-primitive types as function parameters."
        }

        p {
            +"""Serialization of the result also will be done automatically. Nevertheless, it is
                possible to return data as `HttpResponse` -- in this case no serialization will
                be performed."""
        }

        p {
            +"Here is the simple snippet of dynamic `Get` route:"
        }

        kotlin("""
                @Get("/")
                fun root(): String {
                    return "Hello world!"
                }
                """
        )

        h3 {
            +"Context"
        }

        p {
            +"""During processing of HTTP request `KotlessContext.HTTP` object is available
                for user."""
        }

        p {
            +"Context includes current HTTP request at field `request`."
        }

        p {
            +"""Note, that call to `request` may throw `KotlinNullPointerException` if this
                call is performed not in context of HTTP request processing (basically, not
                in an HTTP route)"""
        }


        h3 {
            +"Interceptor"
        }

        p {
            +"""Kotless will pass all HTTP requests through interceptors. Each interceptor
                is a Kotlin static `object` implementing interface `HttpRequestInterceptor`."""
        }

        p {
            +"""In case there is more than one interceptor, Kotless will pass request to
                first of them (ascending order of priority) and will pass as `next` continuation
                pipeline of next interceptors."""
        }

        p {
            +"""Here is the simple snippet of request interceptor, which is checking that
                header auth token is valid:"""
        }

        kotlin("""
                object AuthInterceptor: HttpRequestInterceptor {
                    override val priority = 0

                    override fun intercept(request: HttpRequest, key: RouteKey,
                                           next: (HttpRequest, RouteKey) -> HttpResponse): HttpResponse {
                        if (AuthTokens.isValid(request.headers["auth"])) {
                            //Pass request to next interceptor or handler, if authentication is valid
                            return next(request, key)
                        }
                        //Redirect to login if authentication is not valid
                        return redirect("/login")
                    }
                }"""
        )

        h3 {
            id = "Serialization"
            +"Serialization"
        }

        p {
            +"""Kotless uses `ConversionService` implementations to deserialize parameters of
                request and serialize them in links. `DefaultConversionService` supports
                serialization and deserialization of primitive Kotlin types."""
        }

        p {
            +"""In case you need to support custom types deserialization, you will need to
                create Kotlin static `object` implementing interface `ConversionService`."""
        }

        p {
            +"Here is the simple snippet, which deserialize entities by ID from a database:"
        }

        kotlin("""
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
                }"""
        )

        h3 {
            +"Links"
        }

        p {
            +"Kotless provides extension functions to create links (href-s) to Kotless-based routes."
        }

        p {
            +"""Basically, there are two types of such links - base links, without any parameters and
                prepared links with parameters already embedded in a link itself.
                    """
        }

        p {
            +"""Here is the simple snippet creating base and prepared link (pretend that exists annotated
                function `root(str: String)` in object `API`)"""
        }

        kotlin("""
                val baseLink = API::root.href
                val preparedLink = API::root.href("parameter")"""
        )

        h2 {
            +"Static"
        }

        p {
            +"""Static HTTP API of Kotless is a convenient way of creating HTTP routes
                which are served via static files."""
        }

        p {
            +"To create static route you need to use `@StaticGet` annotation."
        }

        p {
            +"Note, that this annotation has severe limitations on an annotated object."
        }

        p {
            +"""Under annotation should be a property, which value is instantiated directly
                with `java.io.File(String)` constructor and path should be relative to
                `workingDir` of Kotless."""
        }

        p {
            +"Here is the simple snippet of static route creation:"
        }

        kotlin("""
                @StaticGet("/file.css", MimeType.CSS)
                val exampleCss = File("example.css")"""
        )

        p {
            +"Links API also works for static routes, but only base link can be constructed:"
        }

        kotlin("val baseLink = ::exampleCss.href")
    }


    fun events() = doc {
        h1 {
            +"Events API"
        }

        p {
            +"""Kotless DSL provides number of annotations to work with different
                Events sources in the cloud."""
        }

        h2 {
            +"Scheduled events"
        }

        p {
            +"""`Scheduled` annotation declares that function, annotated with it,
                 should be triggered by a timer."""
        }

        p {
            +"Mostly, it should be used to setup scheduled jobs."
        }

        p {
            +"""Note, that function should not have any parameters, since it will
                be called via crontab-like service that passes no context."""
        }

        p {
            +"""Annotation requires `cron` argument - it is a cron expression
                that defines trigger behavior. Its syntax is taken from """
            a {
                href = "https://docs.aws.amazon.com/AmazonCloudWatch/latest/events/ScheduledEvents.html#CronExpressions"
                +"AWS Scheduled Events"
            }
        }

        p {
            +"""Also, you can provide `id` argument -- it will be used to create
                trigger on a cloud side. If case no `id` is provided it will be
                generated by Kotless during deployment.
            """
        }

        p {
            +"Here is an example of Scheduled job:"
        }

        kotlin("""
            @Scheduled(Scheduled.everyHour)
            fun storageCleanup() {
                logger.info("Starting storage cleanup")
                Storage.cleanup()
                logger.info("Ended storage cleanup")
            }  
        """.trimIndent())
    }
}


