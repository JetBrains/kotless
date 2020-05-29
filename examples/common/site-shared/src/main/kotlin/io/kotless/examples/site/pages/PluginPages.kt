package io.kotless.examples.site.pages

import io.kotless.examples.site.bootstrap.doc
import io.kotless.examples.site.bootstrap.kotlin
import kotlinx.html.*

object PluginPages {
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

            li {
                +"`destroy` - executes `destroy` terraform operation, if enabled in `extensions`"
            }
        }

        p {
            +"Note, that actual lambdas jars are prepared using `shadowJar` plugin"
        }

        p {
            +"""Furthermore, Kotless gradle plugin defines a part of configurations of 
                a Kotless-based application deployment."""
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
            li {
                +"""Extensions configs - configuration that defines different extensions for Kotless pipeline.
                    For example, it may enable `destroy` task or add user's terraform files to deployment code."""
            }
        }

        p {
            +"Here is the simple snippet of the whole configuration:"
        }
        kotlin("""
                config {
                    //see details in a Kotless Configuration docs
                    bucket = "kotless.s3.example.com"
                    prefix = "dev"

                    workDirectory = file("src/main/static")

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
                    prefix = "dev"

                    workDirectory = file("src/main/static")

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
            +"""Note, that Kotless supports deployment to any region, but requires certificate
                to be created in us-east-1. It is AWS limitation and cannot be fixed in Kotless."""
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
                    prefix = "dev"

                    workDirectory = file("src/main/static")
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

        h4 {
            +"Lambda AutoWarming Optimization"
        }

        p {
            +"""This optimization sets up a timer that will autowarm lambda, by default each 5 minutes.
                Such an optimization makes cold start less frequent. """
        }

        p {
            +"""Each timer event executes warming sequence. This sequence triggers `LambdaWarming` objects
                and is described in a Lifecycle API section."""
        }

        kotlin("""
            optimization {
                //default config
                autowarm = Autowarm(enable = true, minutes = 5)
            }
        """)

        p {
            +"""Kotless lambdas can be autowarmed. It means, that some scheduler will
                periodically (by default, each 5 minutes) call lambda to be sure, that
                it will not be displaced from hot pool of cloud provider."""
        }


        h2 {
            +"WebApp Configuration"
        }

        p {
            +"""WebApp configuration in a Kotless gradle plugin defines project-specific
                deployment configuration."""
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
                webapp {
                    packages = setOf("org.example.kotless")
                    route53 = Route53("kotless", "example.com")
                    lambda {
                        timeoutSec = 300
                        memoryMb = 512
                    }
                }
        """)

    }

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
            li {
                +"""`destroy` - task that "destroys" the deployment. This task destroys all the resources
                create by `deploy` task. By default, task is hidden and can be enabled through extensions."""
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

    fun extensions() = doc {
        h1 {
            +"Extensions"
        }

        p {
            +"""Extensions configuration in a Kotless gradle plugin defines different 
                extensions for Kotless pipeline."""
        }

        p {
            +"""Via Extensions API you may add user's terraform files to generated terraform code.
                It may be useful in case you have some resource, that cannot be created by Kotless
                itself, but is used by Kotless based application. For example, via terraform extension
                you can create a DynamoDB table that will be used by Kotless-based application."""
        }

        p {
            +"Also, via Extensions API you can enable `destroy` task in Gradle. It is hidden by default for safety reasons"
        }

        kotlin("""
            extensions {
                terraform {
                    //Enable back Destroy task
                    allowDestroy = true
            
                    files {
                        // Add file to deployment code
                        add(file("src/main/tf/extensions.tf"))
                    }
                }
            }
        """)

    }
}

