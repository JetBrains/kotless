import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.resource.Lambda.Config.Runtime

group = rootProject.group
version = rootProject.version

plugins {
    id("io.kotless") version "0.1.7-beta-2" apply true
}

dependencies {
    implementation("io.kotless", "ktor-lang", "0.1.7-beta-2")

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
    }

    webapp {
        route53 = Route53("ktor.site", "kotless.io")

        lambda {
            runtime = Runtime.GraalVM
        }
    }
}
