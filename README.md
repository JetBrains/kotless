<h1> <img align="left" width="40" height="40" src="https://site.kotless.io/favicon.apng" alt="Kotless Icon"> Kotless </h1>

[![JetBrains incubator project](https://jb.gg/badges/incubator.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![Download](https://img.shields.io/badge/dynamic/json.svg?label=latest&query=name&style=flat&url=https%3A%2F%2Fapi.bintray.com%2Fpackages%2Ftanvd%2Fio.kotless%2Flang%2Fversions%2F_latest) ](https://bintray.com/tanvd/io.kotless/lang/_latestVersion)
[![CircleCI](https://img.shields.io/circleci/build/github/JetBrains/kotless.svg?style=flat)](https://circleci.com/gh/JetBrains/kotless)
[![gradlePluginPortal](https://img.shields.io/maven-metadata/v.svg?label=gradlePluginPortal&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fio.kotless%2Fio.kotless.gradle.plugin%2Fmaven-metadata.xml?style=flat)](https://plugins.gradle.org/plugin/io.kotless)
[![KotlinLang slack](https://img.shields.io/static/v1?label=kotlinlang&message=kotless&color=brightgreen&logo=slack&style=flat)](https://app.slack.com/client/T09229ZC6/CKS388069)


Kotless stands for Kotlin serverless framework. 

Its main focus lies in reducing the routine of serverless deployment creation and generating it straight
from the code of the application itself. 

Kotless consists of two main parts:
* Kotless DSL provides a way of defining serverless applications. It includes Post/Get annotations, lambda dispatcher, etc.;
* Kotless Gradle Plugin provides a way of deploying serverless application. It performs the tasks of generating Terraform 
  code from the application code and, subsequently, deploying it to AWS.

## Getting started

Kotless uses Gradle to wrap around the existing build process and insert the deployment into it. 

Basically, if you already use Gradle, you only need to do two things.

Firstly, add Kotless DSL as a library to your application:

```kotlin
repositories {
    jcenter()
}

dependencies {
    implementation("io.kotless", "lang", "0.1.1")
}
```

This gives you access to Kotless DSL annotations in your code and sets up Lambda dispatcher inside of your application.

Secondly, set up Kotless Gradle plugin. You need to apply the plugin:

```kotlin
plugins {
    id("io.kotless") version "0.1.1" apply true
}
```

Then you just set up Kotless in your `build.gradle.kts`. It's rather simple:

```kotlin
kotless {
    config {
        bucket = "kotless.s3.example.com"
        workDirectory = File(project.projectDir, "src/main/static")
        terraform {
            profile = "example"
            region = "us-east-1"
        }
    }
    webapp(project) {
        packages = setOf("com.example")
        route53 = Route53("kotless", "example.com")
    }
}
```

Here we set up the config of Kotless itself:
* the bucket, which will be used to store lambdas and configs;
* the working directory, against which static resources will be resolved;
* Terraform configuration with a name of the profile to access AWS.

Then we set up webapp &mdash; a specific application to deploy: 
* a set of packages that should be scanned for Kotless DSL annotations;
* Route53 alias for the resulting application (you need to pre-create ACM certificate for the DNS record).

And that's the whole setup!

Now you can create you first serverless application:

```kotlin

@Get("/")
fun gettingStartedPage() = html {
    body {
        +"Hello world!"
    }
}
```

*HTML builder provided by `implementation("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.11")` dependency*

## Advanced features

While Kotless can be used as a framework for a rapid creation of serverless
applications, it has many more features covering different areas of application.

Including, but not limited to:
* Lambdas auto-warming &mdash; Kotless creates schedulers to execute warming sequences to never leave your lambdas cold. 
  It is possible to add various actions to the warming sequence via `@Warming` annotation;
* Granular permissions &mdash; you can declare which permissions to which AWS resources are required for the code that
  calls the function via annotations on kotlin functions. Permissions will be granted automatically.
* Static resources &mdash; Kotless will deploy files annotated with `@StaticResource` to S3 and create specified HTTP 
  routes for them.
* Scheduled events &mdash; Kotless setups timers to execute `@Scheduled` jobs on schedule;
* Terraform extensions &mdash; Kotless-generated code can be extended by custom Terraform code;
* Serialization and deserialization &mdash; Kotless will automatically deserialize parameters from an HTTP request into 
  function parameters and will serialize the result of the function as well. You can extend the number of supported 
  types of parameters by creating top-level `object` implementing `ConversionService`. It will be automatically
  added to the list of supported conversions.

Kotless is in active development, so we are currently working on extending this list with such features as:
* Support of other clouds &mdash; Kotless is based on a cloud-agnostic schema, so we are working on supporting of other clouds
* Support of multiplatform applications &mdash; Kotless will not use any platform-specific libraries to give you a choice of a Lambda runtime (JVM/JS/Native)
* Versioned deployment &mdash; Kotless will be able to deploy several versions of the application and maintain one of them
  as active.
* Implicit permissions granting &mdash; Kotless will be able to deduce permissions from AWS SDK function calls.
* Events handlers support &mdash; Kotless will generate events subscriptions for properly annotated events handlers.

## Examples

Any explanation becomes much better with a proper example.

In the repository's `examples` folder, you can find example projects built with Kotless:
* `ktls-site` &mdash; a site about Kotless written with Kotless ([site.kotless.io](https://site.kotless.io)). 
This example demonstrates `@StaticGet` and `@Get` (static and dynamic routes), as well as Link API
* `ktls-shortener` &mdash; a simple URL shortener written with Kotless (see the result at [short.kotless.io](https://short.kotless.io)). 
This example demonstrates `@Get` (dynamic routes), `@Scheduled` (scheduled lambdas), Permissions API (for DynamoDB access) and Terraform extensions.

## Want to know more?

You may take a look at [Wiki](https://github.com/JetBrains/kotless/wiki) where the client documentation on Kotless is located.

Apart from that, Kotless code itself is widely documented, and you can take a look into its interfaces to get to know Kotless better. 

You may ask questions and participate in discussions in `#kotless` channel in [KotlinLang slack](http://slack.kotlinlang.org).
