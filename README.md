# Kotless

[![JetBrains incubator project](https://jb.gg/badges/incubator-flat-square.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)

Kotless stands for Kotlin serverless framework. 

It aims to reduce the routine of serverless deployment creation and generates it straight
from the code of the application itself. 

Kotless consists of two main parts:
* Kotless DSL - the way of defining serverless applications, it includes Post/Get annotations, lambda dispatcher, etc.;
* Kotless Gradle Plugin - the way of deploying serverless application, it performs the tasks of generating Terraform 
  code from the application code and, finally, deploying it to AWS.

## Getting started

Kotless uses Gradle to wrap around the existing build process and insert the deployment into it. 

Basically, if you already use Gradle, you will only need to do two things.

Firstly, add Kotless DSL as a library to your application:

`compile("io.kotless", "lang", "0.1.0-SNAPSHOT")`

It will give you access to Kotless DSL annotations in your code and will set up Lambda dispatcher inside of your application.

Secondly, set up Kotless Gradle plugin. You will need to apply the plugin:

```kotlin
plugins {
    id("io.kotless") version "0.1.0-SNAPSHOT" apply true
}
```

Then you will need to set up Kotless in your `build.gradle.kts`. It's rather simple:

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
* bucket, which will be used to store lambdas and configs;
* working directory, against which static resources will be resolved;
* Terraform configuration with a name of the profile to access AWS.

Then we set up webapp - specific application to deploy: 
* set of packages that should be scanned for Kotless DSL annotations;
* Route53 alias for the resulting application (you will need to pre-create ACM certificate for the DNS record).

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

*HTML builder provided by `compile("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.11")` dependency*

## Advanced features

While Kotless can be used as a framework for a rapid creation of serverless
applications, it has *(or will have)* a lot more features covering different areas of application.

Please note that some of the features may be written in *cursive*, which means they are in development or
are undergoing redesign.

Including, but not limited to:
* Lambdas auto-warming - Kotless creates schedulers to execute warming sequences to never leave your lambdas cold. 
  It is possible to add various actions to the warming sequence via `@Warming` annotation;
* Granular permissions - you can declare which permissions to which AWS resources are required for the code that
  calls the function via annotations on kotlin functions. Permissions will be granted automatically.
* Static resources - Kotless will deploy files annotated with `@StaticResource` to S3 and create specified HTTP 
  routes for them.
* Serialization and deserialization - Kotless will automatically deserialize parameters from an HTTP request into 
  function parameters and will serialize the result of the function as well. You can extend the number of supported 
  types of parameters by creating top-level `object` implementing `ConversionService`. It will be automatically
  added to the list of supported conversions.

Kotless is in active development, so we are working on extending this list with such features as:
* Versioned deployment - Kotless will be able to deploy several versions of the application and maintain one of them
  as active.
* Implicit permissions granting - Kotless will be able to deduce permissions from AWS SDK function calls 
  (already implemented, but postponed for now).
* Events handlers support - Kotless will generate events subscriptions for properly annotated events handlers.

## Want to know more?

You may take a look at the `docs` folder with the client documentation on Kotless.

Apart from that, Kotless code itself is widely documented, and you can take a look into its interfaces 
to get to know Kotless better. 

If you still have questions that we weren't able to answer in this README, feel free to contact us at your
convenience!
