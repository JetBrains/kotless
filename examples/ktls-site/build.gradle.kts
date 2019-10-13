import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless

group = "io.kotless"
version = "0.1.1"

plugins {
    id("tanvd.kosogor") version "1.0.7" apply true

    kotlin("jvm") version "1.3.50" apply true

    id("io.kotless") version "0.1.1-SNAPSHOT" apply true
}

repositories {
    //artifacts are located at JCenter
    mavenLocal()
    jcenter()
}

dependencies {
    implementation("io.kotless", "lang", "0.1.1-SNAPSHOT")
    implementation("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.11")
}

kotless {
    config {
        bucket = "eu.site.s3.ktls.aws.intellij.net"
        prefix = "site"

        workDirectory = file("src/main/static")

        terraform {
            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        packages = setOf("io.kotless.examples")
        route53 = Route53("site", "kotless.io")
    }
}

