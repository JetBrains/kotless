## Kotless Gradle Plugin

Kotless gradle plugin is a key object in Kotless architecture. It tightens up 
abstract representation of Serverless application and it's actual deployment.

Plugin defines few tasks to deploy Kotless-based application:
* `generate` - task generates terraform code from Kotlin code written using Kotless DSL.
* `plan` - executes `plan` terraform operation on a generated terraform code
* `deploy` - executes `apply` terraform operation on generated terraform code

Note, that actual lambdas jars are prepared using `shadowJar` plugin

Furthermore, Kotless gradle plugin is one of a two available for end-user 
interfaces to configure Kotless-based application deployment (second is Kotless DSL).

Following configurations are available via a plugin:
* Kotless config - configuration of terraform, it's cloud provider, kotless used buckets
  and global optimizations
* WebApp configs - configuration of lambdas used in specific web application, it's alias
  and deployment parameters

Here is the simple snippet of the whole configuration:
```kotlin
config {
    //see details in a Kotless Configuration docs
    bucket = "kotless.s3.example.com"
    resourcePrefix = "dev-"

    workDirectory = File(project.projectDir, "src/main/static")
    
    terraform {
        profile = "example-profile"
        region = "us-east-1"
    }
    
    optimization {
        mergeLambda = MergeLambda.All
    }
    
    //see details in a WebApp Configuration docs
    webapp(project) {
        packages = setOf("org.example.kotless")
        route53 = Route53("kotless", "example.com")
        lambda {
            timeoutSec = 300
            memoryMb = 512
        }
    }
}
```

