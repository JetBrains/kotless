import io.kotless.plugin.gradle.dsl.kotless

group = "io.kotless"
version = "0.1.0"

plugins {
    id("tanvd.kosogor") version "1.0.7" apply true

    kotlin("jvm") version "1.3.41" apply true
    //shadow jar should be applied before kotless
    id("com.github.johnrengelman.shadow") version "5.0.0" apply true

    id("io.kotless") version "0.1.1-SNAPSHOT" apply true
}

repositories {
    //artifacts are located at JCenter
    mavenLocal()
    jcenter()
}

dependencies {
    compile("io.kotless", "lang", "0.1.1-SNAPSHOT")
    compile("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.11")
}

kotless {
    config {
        bucket = "site.s3.ktls.aws.intellij.net"
        resourcePrefix = "ktls-site-prod"

        workDirectory = File(project.projectDir, "src/main/static")

        terraform {
            profile = "kotless-jetbrains"
            region = "us-east-1"
        }
    }
    webapp(project) {
        packages = setOf("io.kotless.examples")
        route53 = Route53("site", "ktls.aws.intellij.net")
    }
}

