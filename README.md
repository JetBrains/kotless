<h1> <img width="40" height="40" src="https://s3-eu-west-1.amazonaws.com/public.s3.ktls.aws.intellij.net/resources/favicon.apng" alt="Kotless Icon"> Kotless </h1>

[![JetBrains incubator project](https://jb.gg/badges/incubator.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![KotlinLang slack](https://img.shields.io/static/v1?label=kotlinlang&message=kotless&color=brightgreen&logo=slack&style=flat)](https://app.slack.com/client/T09229ZC6/CKS388069)

Kotless stands for Kotlin serverless framework.

Its focus lies in reducing the routine of serverless deployment creation by generating it straight
from the code of the application itself.

So, simply speaking, Kotless gives you one magic button to deploy your Web application as a
serverless application on AWS and Azure!

Kotless consists of two main parts:

* DSL provides a way of defining serverless applications. There are three DSLs supported:
    * **Kotless DSL** &mdash; Kotless own DSL that provides annotations to declare routing,
      scheduled events, etc.
    * **Ktor** &mdash; Ktor engine that is introspected by Kotless. Use standard Ktor syntax and
      Kotless will generate deployment.
    * **Spring Boot** &mdash; Spring Boot serverless container that is introspected by Kotless. Use
      standard Spring syntax and Kotless will generate deployment.
* Kotless Gradle Plugin provides a way of deploying serverless application. For that, it:
    * performs the tasks of generating Terraform code from the application code and, subsequently,
      deploying it to AWS or Azure;
    * runs application locally, emulates the AWS environment (if necessary) and provides the
      possibility for IDE debugging.

One of the key features of Kotless is its ability to embed into existing applications. Kotless makes
super easy deployment of existing Spring and Ktor applications to AWS and Microsoft Azure serverless
platforms.

## Getting started

### Setting up project

Kotless uses Gradle to wrap around the existing building process and insert the deployment into it.

Consider using one of the latest versions of Gradle, starting with the **8.5** version.

Basically, if you already use Gradle, you only need to do two things.

Firstly, set up the Kotless Gradle plugin.

You will have to tell Gradle where to find the plugin by editing `settings.gradle.kts`:

```kotlin
pluginManagement {
    resolutionStrategy {
        this.eachPlugin {
            if (requested.id.id == "io.kotless") {
                useModule("io.kotless:gradle:${this.requested.version}")
            }
        }
    }

    repositories {
        maven(url = uri("https://packages.jetbrains.team/maven/p/ktls/maven"))
        gradlePluginPortal()
        mavenCentral()
    }
}
```

And apply the plugin:

```kotlin
//Imports are necessary, for this example
import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless

//Group may be used by Kotless DSL to reduce the number of introspected classes by package
//So, don't forget to set it
group = "org.example"
version = "0.1.0"


plugins {
    //Version of Kotlin should be 1.9.21+
    kotlin("jvm") version "1.9.21" apply true

    id("io.kotless") version "0.3.2" apply true
}
```

Secondly, add Kotless DSL (or Ktor, or Spring Boot) as a library to your application:

```kotlin
repositories {
    mavenCentral()
    //Kotless repository
    maven(url = uri("https://packages.jetbrains.team/maven/p/ktls/maven"))
}

dependencies {
    implementation("io.kotless", "kotless-lang", "0.3.2")
    implementation("io.kotless", "kotless-lang-aws", "0.3.2")
//    if you want to deploy to Microsoft Azure, just replace -aws with -azure    
//    implementation("io.kotless", "ktor-lang-azure", "0.3.2")


    //or for Ktor (Note, that `ktor-lang` depends on Ktor version 1.5.0)
    //implementation("io.kotless", "ktor-lang", "0.3.2")
    //implementation("io.kotless", "ktor-lang-aws", "0.3.2")
    //implementation("io.kotless", "ktor-lang-azure", "0.3.2")

    //or for Spring Boot (Note, that `spring-boot-lang` depends on Spring Boot version 3.2.0)
    //implementation("io.kotless", "spring-boot-lang", "0.3.2")
    //implementation("io.kotless", "spring-boot-lang-aws", "0.3.2")
    //implementation("io.kotless", "spring-boot-lang-azure", "0.3.2")
}
```

*Please note that if you use Ktor or Spring Boot you will need to replace existing in your project
dependency with a special Kotless `*-lang` dependency. Also, after that you will need to align
version of dependent libraries (like Spring Security) with version bundled in `*-lang`
(see this [paragraph](#integration-with-existing-applications))*

This gives you access to DSL interfaces in your code and sets up a Lambda dispatcher inside your
application.

### Deploying to the cloud

Depending on a use case, you may want to deploy application either in an AWS or Microsoft Azure.

Note, that if you even don't have a cloud account, you can still use Kotless locally to run and
debug your application -- just use `local` Gradle task.

#### Deploying to AWS

If you don't have an AWS account, you can create it following simple
[instruction](https://hadihariri.com/2020/05/12/from-zero-to-lamda-with-kotless/) by Hadi Hariri.

If you have an AWS account and want to perform the real deployment &mdash; let's set up everything
for it! It's rather simple:

```kotlin

kotless {
    config {

        aws {
            storage {
                bucket = "kotless.s3.example.com"
            }

            profile = "example"
            region = "eu-west-1"
        }
    }

    webapp {
        dns("kotless", "example.com")
    }
}
```

Here we set up the config of Kotless itself:

* the bucket, which will be used to store lambdas and configs;
* Terraform configuration with the name of the profile to access AWS.

Then we set up a specific application to deploy:

* Route53 alias for the resulting application (you need to pre-create an ACM certificate for the DNS
  record).

And that's the whole setup!

#### Deploying to Azure

Deployment to Microsoft Azure is also pretty straightforward and simple:

```kotlin
kotless {
    config {
        azure {
            storage {
                storageAccount = "your-storage-account"
                container = "container-which-kotless-would-use"
            }

            terraform {
                backend {
                    resourceGroup = "your-resource-group"
                }
            }
        }
    }

    webapp {
        dns("kotless", "example.com")
    }
}

```

Here we set up the config of Kotless itself:

* the storage, which will be used to store lambdas and configs;
* Terraform configuration with the name of the profile to access Azure.

Then we set up a specific application to deploy:

* Azure DNS alias for the resulting application (you need to pre-create certificate for the DNS
  record).

And that's the whole setup!

### Creating application

Now you can create your first serverless application with Kotless DSL:

```kotlin
@Get("/")
fun main() = "Hello world!"
```

Or with Ktor:

```kotlin
class Server : Kotless() {
    override fun prepare(app: Application) {
        app.routing {
            get("/") {
                call.respondText { "Hello World!" }
            }
        }
    }
}
```

Or with Spring Boot:

```kotlin
@SpringBootApplication
open class Application : Kotless() {
    override val bootKlass: KClass<*> = this::class
}

@RestController
object Pages {
    @GetMapping("/")
    fun main() = "Hello World!"
}
```

## Local start

Kotless-based applications can start locally as an HTTP server. This functionality is supported by
all DSLs.

Moreover, Kotless local start may spin up an AWS emulation (docker required). Just instantiate your
AWS service client using override for Kotless local starts:

```kotlin
val client = AmazonDynamoDBClientBuilder.standard().withKotlessLocal(AwsResource.DynamoDB).build()
```

And enable it in Gradle:

```kotlin
kotless {
    //<...>
    extensions {
        local {
            //enables AWS emulation (disabled by default)
            useAWSEmulation = true

            //8080 is default if not supplied, this is mainly used when u want to run multiple kotless services local (give different port to each)
            port = 8080
            
            //when supplying debug port the app with allow remote debug via this port (if you want to run multiple kotless locally provide different debug ports to each)
            debugPort = 5005

            //when set to 'true' the local kotless will wait until remote debug is attached 
            suspendDebug = false
        }
    }
}
```

During the local run, LocalStack will be started and all clients will be pointed to its endpoint
automatically.

Local start functionality does not require any access to cloud provider, so you may check how your
application behaves without an AWS account. Also, it gives you the possibility to debug your
application locally from your IDE.

## Integration with existing applications

Kotless is able to deploy existing Spring Boot or Ktor application to AWS serverless platform. To do
it, you'll need to set up a plugin and replace existing dependency with the appropriate Kotless DSL.

For **Ktor**, you should replace existing engine (
e.g. `implementation("io.ktor", "ktor-server-netty", "1.5.0")`)
with `implementation("io.kotless", "ktor-lang", "0.1.6")`. Note that this dependency bundles Ktor of
version
`1.5.0`, so you may need to upgrade other Ktor libraries (like `ktor-html-builder`) to this version.

For **Spring Boot** you should replace the starter you use (
e.g. `implementation("org.springframework.boot", "spring-boot-starter-web", "3.2.0)`)
with `implementation("io.kotless", "spring-boot-lang", "0.3.2")`. Note that this dependency bundles
Spring Boot of version `3.2.0`, so you also may need to upgrade other Spring Boot libraries to this
version.

Once it is done, you may hit `deploy` task and make your application serverless. Note, that you will
still be able to run application locally via `local` Gradle task.

## Advanced features

While Kotless can be used as a framework for the rapid creation of serverless applications, it has
many more features covering different areas of application.

Including, but not limited to:

* **Lambdas auto-warming** &mdash; Kotless creates schedulers to execute warming sequences to never
  leave your lambdas cold. As a result, applications under moderate load are not vulnerable to
  cold-start problem.
* **Permissions management** &mdash; you can declare which permissions to which AWS resources are
  required for application via annotations on Kotlin functions, classes or objects. Permissions
  will be granted automatically.
* **Static resources** &mdash; Kotless will deploy static resources to S3 and set up CDN for them.
  It may greatly improve the response time of your application and is supported by all DSLs.
* **Scheduled events** &mdash; Kotless sets up timers to execute `@Scheduled` jobs on schedule;
* **Terraform extensions** &mdash; Kotless-generated code can be extended by custom Terraform code;

Kotless is in active development, so we are currently working on extending this list with such
features as:

* Support of other clouds &mdash; Kotless is based on a cloud-agnostic schema, so we are working on
  support of other clouds.
* Support of multiplatform applications &mdash; Kotless will not use any platform-specific libraries
  to give you a choice of a Lambda runtime (JVM/JS/Native).
* Versioned deployment &mdash; Kotless will be able to deploy several versions of the application
  and maintain one of them as active.
* Implicit permissions granting &mdash; Kotless will be able to deduce permissions from AWS SDK
  function calls.
* Events handlers support &mdash; Kotless will generate events subscriptions for properly annotated
  events handlers.

## Examples

Any explanation becomes much better with a proper example.

In the repository's `examples` folder, you can find example projects built with Kotless DSL:

* `kotless/site` &mdash; a site about Kotless written with Kotless
  DSL ([site.kotless.io](https://site.kotless.io)). This example demonstrates `@StaticGet`
  and `@Get` (static and dynamic routes) usage, as well as Link API.
* `kotless/shortener` &mdash; a simple URL shortener written with Kotless
  DSL ([short.kotless.io](https://short.kotless.io)). This example demonstrates `@Get` (
  dynamic routes), `@Scheduled` (scheduled lambdas), Permissions API (for DynamoDB access), and
  Terraform extensions.

Similar examples exist for Ktor:

* `ktor/site` &mdash; a site about Kotless written with
  Ktor ([ktor.site.kotless.io](https://ktor.site.kotless.io)). This example
  demonstrates `static {...}`
  and `routing {...}` usage.
* `ktor/shortener` &mdash; a simple URL shortener written with
  Ktor ([ktor.short.kotless.io](https://ktor.short.kotless.io)). This example
  demonstrates `routing { ... }` (dynamic routes), Permissions API (for DynamoDB access), and
  Terraform extensions.

And for Spring Boot:

* `spring/site` &mdash; a site about Kotless written with Spring
  Boot ([spring.site.kotless.io](https://spring.site.kotless.io)). This example demonstrates usage
  of statics and `@RestController`.
* `spring/shortener` &mdash; a simple URL shortener written with Spring
  Boot ([spring.short.kotless.io](https://spring.short.kotless.io)). This example demonstrates usage
  of `@RestController` (dynamic routes), Permissions API (for DynamoDB access), and Terraform
  extensions.

## Want to know more?

You may take a look at [Wiki](https://github.com/JetBrains/kotless/wiki) where the client
documentation on Kotless is located.

Apart from that, the Kotless code itself is widely documented, and you can take a look into its
interfaces to get to know Kotless better.

You may ask questions and participate in discussions on `#kotless` channel
in [KotlinLang slack](http://slack.kotlinlang.org).

## Acknowledgements

Special thanks to:

* Alexandra Pavlova (aka sunalex) for our beautiful logo;
* Yaroslav Golubev for help with documentation;
* [Gregor Billing](https://github.com/suushiemaniac) for help with the Gradle plugin and more.
