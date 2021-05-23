import io.kotless.plugin.gradle.dsl.kotless

group = rootProject.group
version = rootProject.version

plugins {
    id("io.kotless") version "0.2.0" apply true
}

dependencies {
    implementation("commons-validator", "commons-validator", "1.6")
    implementation("com.amazonaws", "aws-java-sdk-dynamodb", "1.11.650")

    implementation("io.kotless", "spring-boot-lang", "0.2.0")
    implementation("io.ktor", "ktor-html-builder", "1.5.0")
}

kotless {
    config {

        aws {
            prefix = "spring-short"

            storage {
                bucket = "eu.spring-short.s3.ktls.aws.intellij.net"
            }

            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        dns("spring.short", "kotless.io")
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

