import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless

group = "io.kotless.examples"
version = "0.1.3"

plugins {
    id("tanvd.kosogor") version "1.0.7" apply true

    kotlin("jvm") version "1.3.61" apply true

    id("io.kotless") version "0.1.3" apply true
}

repositories {
    //artifacts are located at JCenter
    jcenter()
}

dependencies {
    implementation("io.kotless", "lang", "0.1.3")
    implementation("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.11")
}

kotless {
    config {
        bucket = "eu.site.s3.ktls.aws.intellij.net"
        prefix = "site"

        dsl {
            workDirectory = file("src/main/static")
        }

        terraform {
            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        route53 = Route53("site", "kotless.io")
    }
}

