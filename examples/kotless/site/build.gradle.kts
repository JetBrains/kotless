import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless

group = rootProject.group
version = rootProject.version

plugins {
    id("io.kotless") version "0.1.5" apply true
}

dependencies {
    implementation("io.kotless", "lang", "0.1.5")

    implementation(project(":common:site-shared"))

    implementation("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.11")
}

kotless {
    config {
        bucket = "eu.site.s3.ktls.aws.intellij.net"
        prefix = "site"

        terraform {
            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        route53 = Route53("site", "kotless.io")
    }
}

