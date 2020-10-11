import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless

group = rootProject.group
version = rootProject.version

plugins {
    id("io.kotless") version "0.1.7-beta-4" apply true
}


dependencies {
    implementation("io.kotless", "kotless-lang", "0.1.7-beta-4")

    implementation("commons-validator", "commons-validator", "1.6")
    implementation("com.amazonaws", "aws-java-sdk-dynamodb", "1.11.650")

    implementation("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.11")
}

kotless {
    config {
        bucket = "eu.short.s3.ktls.aws.intellij.net"
        prefix = "short"

        terraform {
            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        route53 = Route53("short", "kotless.io")
    }

    extensions {
        local {
            useAWSEmulation = true
        }

        terraform {
            files {
                add(file("src/main/tf/extensions.tf"))
            }
        }
    }
}

