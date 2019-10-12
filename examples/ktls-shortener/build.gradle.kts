import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless

group = "io.kotless"
version = "0.1.0"

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
    compile("io.kotless", "lang", "0.1.1-SNAPSHOT")

    compile("commons-validator", "commons-validator", "1.6")
    compile("com.amazonaws", "aws-java-sdk-dynamodb", "1.11.650")

    compile("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.11")
}

kotless {
    config {
        bucket = "eu.short.s3.ktls.aws.intellij.net"
        prefix = "short"

        workDirectory = file("src/main/static")

        terraform {
            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        packages = setOf("io.kotless.examples")
        route53 = Route53("short", "kotless.io")
    }

    extensions {
        terraform {
            files {
                add(file("src/main/tf/extensions.tf"))
            }
        }
    }
}

