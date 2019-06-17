## Kotless Configuration

Kotless configuration of Gradle plugin consists of a few globally applied parts:
* Terraform configuration
* Kotless service configuration
* Optimization configuration

Here is a simple snippet of the whole configuration:
```kotlin
config {
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
}
```

We will take a look at parts of this configuration one by one.

### Terraform configuration

In a `terraform` configuration you can set up version of Terraform used, it's
AWS provider and bucket for `tfstate`.

You will need to set up `region` and `profile` -- region for deployment
and local profile used for it accordingly.

Note, that right now Kotless supports deployment only to `us-east-1` region. 
We are working on a fix of this issue.

Here is the simplest possible snippet with a setup of terraform configuration:

```kotlin
terraform {
    //Will use for terraform state buckets used by kotless service configuration
    //version of terraform and aws provider will be default
    profile = "example-profile"
    region = "us-east-1"
}
```

Note, that needed version of terraform will be downloaded automatically.

### Kotless service configuration

Kotless service configuration is a set of directories, S3 bucket and few values
used by Kotless to deploy your application.

You will need to set up `bucket` -- it is a name of bucket that Kotless will use to 
store all files. For example, Kotless will store there packed jars and static files.

You can set `resource-prefix` variable -- it is a prefix  with which all created 
in a cloud resources will be prepended. `resource-prefix` can be used to deploy few
environments of one application.

Probably, you will need to change `workDirectory` variable -- it is a folder, that
Kotless will use as a root to resolve all static route files.

Here is a simple snippet with a setup of service configuration:

```kotlin
config {
    bucket = "kotless.s3.example.com"
    resourcePrefix = "dev-"

    workDirectory = File(project.projectDir, "src/main/static")
}
```

Note, that `bucket` value will be used for terraform state, if it is not set explicitly
in a terraform configuration.

### Optimization configuration

We are doing our best to make Kotless-based lambdas as fast as possible. 

There are plenty of optimizations that are embedded in a Kotless synthesizer and runtime.
Some of them can be configured to align with you needs.

#### Lambda Merge Optimization
Optimization defines if different lambdas should be merged into one and when.

Basically, lambda serving few endpoints is more likely to be warm.

There are 3 levels of merge optimization:
* None -- lambdas will never be merged
* PerPermissions -- lambdas will be merged, if they have equal permissions
* All -- all lambdas in context are merged in one

Here is a simple snipped of optimization configuration:

```kotlin
optimization {
    mergeLambda = MergeLambda.All
}
```
