package io.kotless.examples.site.pages

import io.kotless.examples.site.bootstrap.doc
import io.kotless.examples.site.bootstrap.kotlin
import kotlinx.html.*

object IntroductionPages {

    fun introduction() = doc {
        h1 {
            +"Getting started"
        }
        p {
            +"In this short tutorial we will overview all steps of Kotless-based application creation. \n"
        }


        h3 {
            +"Preliminaries"
        }
        p {
            +"To use Kotless in you project you will need:"
        }
        ul {
            li {
                +"Route53 DNS zone to create DNS name for application"
            }
            li {
                +"S3 bucket to store kotless-related artifacts"
            }
            li {
                +"ACM certificate for DNS name at US-EAST-1"
            }
        }

        h3 {
            +"Setting up Gradle project"
        }
        p {
            +"""First of all, you will need to set up Gradle for your project. Kotless uses Gradle tasks
                to prepare deployment environment, generate deployment code, prepare lambda JAR and deploy
                it to cloud provider."""
        }

        p {
            +"You will need to apply a plugin to project at build.gradle.kts:"
        }

        p {
            +"""To use Kotless DSL in project you will need to set up a maven repository with DSL and
                        add needed dependency:"""
        }

        kotlin("""
                repositories {
                    jcenter()
                }
                dependencies {
                    implementation("io.kotless", "kotless-lang", "0.1.1")
                }""")

        p {
            +"Now you will need to set up Kotless. Here is a simple configuration with comments:"
        }

        kotlin("""
                kotless {
                    config {
                        //bucket that kotless will use to store its artifacts
                        bucket = "kotless-example-bucket"
                        //prefix that will be added to all resources created in AWS
                        prefix = "dev"

                        //directory against which @StaticGet annotated files are resolved
                        workDirectory = file("src/main/static")

                        terraform {
                            profile = "example-profile"
                            region = "us-east-1"
                        }
                    }

                    //webapp for this gradle project
                    webapp {
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
                }"""
        )

        h3 {
            +"Writing dynamic route"
        }

        p {
            +"""In Kotless terminology "dynamic route" is an HTTP route processed by a lambda.
                On the contrary, "static route" is an HTTP route mapped to S3 object."""
        }

        p {
            +"""Creating of dynamic route is relatively simple. This code snippet will
                create route at HTTP path "/" which will print "Hello world!""""
        }

        kotlin("""
                @Get("/")
                fun root(): String {
                    return "Hello world!"
                }"""
        )


        p {
            +"""Note, that dynamic route function may have parameters. Parameters will be taken from
                URL and body(if presented) and deserialized to parameters of function. Moreover, the
                result of function also will be serialized to HTTP response automatically. In case you need
                full control on the HTTP response you may return `HttpResponse` object,
                which will be passed to ApiGateway without any changes."""
        }

        h3 {
            +"Adding statics to your application"
        }

        p {
            +"""Most of the real world applications contain static resources, e.g. CSS and JS files. It is a very wasteful
                spending of resources to serve static resources with lambdas. So, Kotless proposes solution --
                static resources mapped directly to S3."""
        }

        p {
            +"This code snippet will create route at HTTP path `/file.css` with file `example.css`"
        }

        kotlin("""
                @StaticGet("/file.css", MimeType.CSS)
                val exampleCss = File("example.css")"""
        )

        p {
            +"Note, that file will be resolved against `workingDir` set in Gradle configuration"
        }

        h3 {
            +"Deploying application to AWS"
        }

        p {
            +"""At the end you will need to deploy you Kotless-based application to AWS. Check, that
                        you have fulfilled all the preliminaries and execute a task `deploy` in Gradle."""
        }

        p {
            +"""Kotless will download Terraform, generate deployment code, pack lambda and, at the end,
                will apply all of it to AWS."""
        }

        p {
            +"""At the moment deploy is done your application will start serving requests at the
                Route53 DNS name you have assigned to it."""
        }
    }
}
