## Getting started

In this short tutorial we will overview all steps of Kotless-based application creation. 

### Preliminaries

To use Kotless in you project you will need:
* Route53 DNS zone to create DNS name for application
* S3 bucket to store kotless-related artifacts
* ACM certificate for DNS name at US-EAST-1

Note: right now Kotless supports deployment only to US-EAST-1 region. We are working on a fix of this issue.

### Setting up Gradle project

First of all, you will need to set up Gradle for your project. Kotless uses Gradle tasks
to prepare deployment environment, generate deployment code, prepare lambda JAR and deploy
it to cloud provider.

You will need to apply a plugin to project at `build.gradle.kts`:

```kotlin
id("org.jetbrains.kotless") version "0.1.0-SNAPSHOT" apply true
```

To use Kotless DSL in project you will need to add a repository with DSL and
add needed compile dependency:

```kotlin
repositories {
    jcenter()
}
dependencies {
    compile("org.jetbrains.kotless", "lang", "0.1.0-SNAPSHOT")
}
```

Now you will need to set up Kotless. Here is a simple configuration with comments:

```kotlin
kotless {
    config {
        //bucket that kotless will use to store its artifacts
        bucket = "kotless-example-bucket"
        //prefix that will be added to all resources created in AWS
        resourcePrefix = "dev-"

        //directory against which @StaticGet annotated files are resolved
        workDirectory = File(project.projectDir, "src/main/static")

        terraform {
            profile = "example-profile"
            region = "us-east-1"
        }
    }
    //webapp for this gradle project
    webapp(project) {
        //configuration of lambda created
        lambda {
            memoryMb = 1024
            timeoutSec = 120
        }

        //packages in which kotless should search for code
        packages = setOf("org.example.kotless")
        //route53 alias for deployed application
        route53 = Route53("kotless", "example.org")
    }
}
```

### Writing dynamic route

Let's write your first dynamic route. 

In Kotless terminology "dynamic route" is an HTTP route processed by a lambda. 
On the contrary, "static route" is an HTTP route mapped to S3 file. 

Creating of dynamic route is relatively simple. This code snippet will
create route at HTTP path `/` which will print "Hello world!"

```kotlin
@Get("/")
fun root(): String {
    return "Hello world!"
}
```

Note, that dynamic route function may have parameters. Parameters will be taken from
URL and body (if presented) and deserialized to parameters of function. Moreover, the
result of function also will be serialized to HTTP response automatically. 

In case you need full control on the HTTP response you may return `HttpResponse` object,
which will be passed to ApiGateway without any changes.

### Adding statics to your application

Often applications includes static resources, e.g. CSS and JS files. It is a very wasteful
spending of resources to serve static resources with lambdas. So, Kotless proposes solution --
static resources mapped directly to S3.

This code snippet will create route at HTTP path `/file.css` with file `example.css`

```kotlin
@StaticGet("/file.css", MimeType.CSS)
val exampleCss = File("example.css")
```

Note, that file will be resolved against `workingDir` set in Gradle configuration

### Deploying application to AWS

At the end you will need to deploy you Kotless-based application to AWS. Check, that
you have fulfilled all the preliminaries and execute task `deploy` in Gradle.

Kotless will download Terraform, generate deployment code, pack lambda and, at the end,
will apply all of it to AWS. 

At the moment deploy is done your application will start serving requests at the
Route53 DNS name you have assigned to it. 


