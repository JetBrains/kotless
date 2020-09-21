import com.kotlin.aws.runtime.dsl.runtime
import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.resource.Lambda.Config.Runtime

group = rootProject.group
version = rootProject.version

plugins {
    id("com.kotlin.aws.runtime") version "0.1.0" apply true

    id("io.kotless") version "0.1.7" apply true
}

dependencies {
    implementation("com.kotlin.aws.runtime", "runtime", "0.1.0")

    implementation("io.kotless", "ktor-lang", "0.1.7")

    implementation(project(":common:site-shared"))
}

kotless {
    config {
        bucket = "eu.ktor-site.s3.ktls.aws.intellij.net"
        prefix = "ktor-site"

        terraform {
            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }

        afterEvaluate {
            setArchiveTask(tasks["buildGraalRuntime"] as AbstractArchiveTask)
        }
    }

    webapp {
        route53 = Route53("ktor.site", "kotless.io")

        lambda {
            runtime = Runtime.Provided
        }
    }
}

runtime {
    handler = "io.kotless.examples.Server::handleRequest"
}
