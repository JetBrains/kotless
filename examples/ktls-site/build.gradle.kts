import io.kotless.plugin.gradle.dsl.kotless

group = "io.kotless"
version = "0.1.0"

plugins {
    id("tanvd.kosogor") version "1.0.6" apply true

    kotlin("jvm") version "1.3.31" apply true
    //shadow jar should be applied before kotless
    id("com.github.johnrengelman.shadow") version "5.0.0" apply true

    id("io.kotless") version "0.1.0" apply true
}

repositories {
    //artifacts are located at JCenter
    jcenter()
}

dependencies {
    compile("io.kotless", "lang", "0.1.0")
    compile("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.11")
}

kotless {
    config {
        bucket = "ktls-site.s3.aws.kotless.io"
        resourcePrefix = "ktls-site-prod"

        workDirectory = File(project.projectDir, "src/main/static")

        terraform {
            profile = "kotless"
            region = "us-east-1"
        }
    }
    webapp(project) {
        packages = setOf("io.kotless.examples")
        route53 = Route53("prod", "kotless.io")
    }
}

