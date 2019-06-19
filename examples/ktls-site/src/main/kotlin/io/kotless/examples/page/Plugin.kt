package io.kotless.examples.page

import io.kotless.dsl.lang.http.Get
import io.kotless.examples.bootstrap.doc
import io.kotless.examples.bootstrap.kotlin
import kotlinx.html.*


object Plugin {
    @Get("/plugin/overview")
    fun overview() = doc {
        h1 {
            +"Overview"
        }

        p {
            +"""Kotless gradle plugin is a key object in Kotless architecture. It tightens up
                abstract representation of Serverless application, and its actual deployment."""
        }

        p {
            +"Plugin defines few tasks to deploy Kotless-based application"
        }
        ul {
            li {
                +"`generate` - task generates terraform code from Kotlin code written using Kotless DSL."
            }
            li {
                +"`plan` - executes `plan` terraform operation on a generated terraform code"
            }
            li {
                +"`deploy` - executes `apply` terraform operation on generated terraform code"
            }
        }

        p {
            +"Note, that actual lambdas jars are prepared using `shadowJar` plugin"
        }

        p {
            +"""Furthermore, Kotless gradle plugin is one of a two available for end-user
                interfaces to configure Kotless-based application deployment (second is Kotless DSL)."""
        }

        p {
            +"Following configurations are available via a plugin:"
        }

        ul {
            li {
                +"""Kotless config - configuration of terraform, it's cloud provider, kotless used buckets
                    and global optimizations"""
            }
            li {
                +"""WebApp configs - configuration of lambdas used in specific web application, it's alias
                    and deployment parameters"""
            }
        }

        p {
            +"Here is the simple snippet of the whole configuration:"
        }
        kotlin("""
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
        """)
    }

    @Get("/plugin/configuration")
    fun configuration() = doc {
        h1 {
            +"Configuration"
        }

        p {
            +"Configuration of plugin consists of two main parts - Kotless configuration and per-webapp configuration."
            +"We will take a look at them one by one."
        }

        h2 {
            +"Kotless Configuration"
        }

        p {
            +"Kotless configuration of Gradle plugin consists of a few globally applied parts:"
        }
        ul {
            li {
                +"Terraform configuration"
            }
            li {
                +"Kotless service configuration"
            }
            li {
                +"Optimization configuration"
            }
        }
        p {
            +"Here is a simple snippet of the whole configuration:"
        }

        kotlin("""
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
        """)

        p {
            +"We will take a look at parts of this configuration one by one."
        }

        h3 {
            +"Terraform configuration"
        }

        p {
            +"""In a `terraform` configuration you can set up version of Terraform used, it's
                AWS provider and bucket for `tfstate`."""
        }

        p {
            +"""You will need to set up `region` and `profile` -- region for deployment
                and local profile used for it accordingly."""
        }

        p {
            +"""Note, that right now Kotless supports deployment only to `us-east-1` region.
                We are working on a fix of this issue."""
        }

        p {
            +"Here is the simplest possible snippet with a setup of terraform configuration:"
        }

        kotlin("""
                terraform {
                    //Will use for terraform state buckets used by kotless service configuration
                    //version of terraform and aws provider will be default
                    profile = "example-profile"
                    region = "us-east-1"
                }
        """)

        p {
            +"Note, that needed version of terraform will be downloaded automatically."
        }

        h3 {
            +"Kotless service configuration"
        }

        p {
            +"""Kotless service configuration is a set of directories, S3 bucket and few values
                used by Kotless to deploy your application."""
        }

        p {
            +"""You will need to set up `bucket` -- it is a name of bucket that Kotless will use to
                store all files. For example, Kotless will store there packed jars and static files."""
        }

        p {
            +"""You can set `resource-prefix` variable -- it is a prefix  with which all created
                in a cloud resources will be prepended. `resource-prefix` can be used to deploy few
                environments of one application."""
        }

        p {
            +"""Probably, you will need to change `workDirectory` variable -- it is a folder, that
                Kotless will use as a root to resolve all static route files."""
        }

        p {
            +"Here is a simple snippet with a setup of service configuration:"
        }

        kotlin("""
                config {
                    bucket = "kotless.s3.example.com"
                    resourcePrefix = "dev-"

                    workDirectory = File(project.projectDir, "src/main/static")
                }
        """)

        p {
            +"""Note, that `bucket` value will be used for terraform state, if it is not set explicitly
                in a terraform configuration."""
        }

        h3 {
            +"Optimization configuration"
        }

        p {
            +"We are doing our best to make Kotless-based lambdas as fast as possible."
        }

        p {
            +"""There are plenty of optimizations that are embedded in a Kotless synthesizer and runtime.
                Some of them can be configured to align with you needs."""
        }

        h4 {
            +"Lambda Merge Optimization"
        }

        p {
            +"Optimization defines if different lambdas should be merged into one and when."
        }

        p {
            +"Basically, lambda serving few endpoints is more likely to be warm."
        }

        p {
            +"There are 3 levels of merge optimization:"
        }

        ul {
            li {
                +"None - lambdas will never be merged"
            }
            li {
                +"PerPermissions -- lambdas will be merged, if they have equal permissions"
            }
            li {
                +"All -- all lambdas in context are merged in one"
            }
        }

        p {
            +"Here is a simple snipped of optimization configuration:"
        }

        kotlin("""
            optimization {
                mergeLambda = MergeLambda.All
            }
        """)


        h2 {
            +"WebApp Configuration"
        }

        p {
            +"""WebApp configuration in a Kotless gradle plugin defines project-specific
                deployment configuration."""
        }

        p {
            +"""First of all, `webapp` function receives `Project` as a first argument.
                This project will be considered as a source of defined web application.
                It means, that in sources of this project will be performed a lookup
                for DSL usage."""
        }

        p {
            +"""In WebApp configuration you must set `packages` variable -- it is a set
                of packages under which Kotless will look for usages of DSL. All other
                usages will be ignored."""
        }

        p {
            +"""You may set `route53` variable -- it defines a Route53 alias for your
                application. You will need an ACM certificate in us-east-1 to create it."""
        }

        p {
            +"""It is possible to configure lambda parameters -- you should use `lambda`
                function for it. You can set timeout of lambda, memory in MB and autowarm
                configuration."""
        }

        p {
            +"Here is the simple snippet of the webapp configuration:"
        }

        kotlin("""
                webapp(project) {
                    packages = setOf("org.example.kotless")
                    route53 = Route53("kotless", "example.com")
                    lambda {
                        timeoutSec = 300
                        memoryMb = 512
                    }
                }
        """)

    }

    @Get("/plugin/tasks")
    fun tasks() = doc {
        h1 {
            +"Tasks"
        }

        p {
            +"""Kotless have two types of tasks -- service and end-user. In spite of fact that service
            tasks are also available for end-user, you should not call them explicitly. All of them will
            be called as dependencies of end-user tasks."""
        }

        p {
            +"Note, that 99% percent of the time you should execute just `./gradlew deploy`"
        }

        p {
            +"End-user tasks:"
        }

        ul {
            li {
                +"""`deploy` - task that actually deploy your Kotless-based application to cloud provider.
                 Task will call all other needed tasks by dependencies, and most of the time you should use
                 only this task."""
            }
            li {
                +"""`plan` - task that "plans" the deployment. The result of this task is a log of changes
                that will be applied to cloud provider (terraform generated)"""
            }
        }

        p {
            +"Service tasks:"
        }

        ul {
            li {
                +"`generate` - task that generates deployment definition (terraform code) for your application"
            }
            li {
                +"`init` - task that performs `terraform init` on generated terraform code"
            }
            li {
                +"`download_terraform` - task that downloads required version of terraform from HashiCorp site"
            }
        }
    }
}

