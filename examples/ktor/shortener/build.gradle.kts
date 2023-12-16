import io.kotless.plugin.gradle.dsl.kotless

group = rootProject.group
version = rootProject.version

plugins {
    id("io.kotless") version "0.3.0" apply true
}

dependencies {
    implementation("commons-validator", "commons-validator", "1.6")
    implementation("com.amazonaws", "aws-java-sdk-dynamodb", "1.12.618")

    implementation("io.kotless", "ktor-lang-aws", "0.3.0")
    implementation("io.kotless", "ktor-lang", "0.3.0")
    implementation("io.ktor", "ktor-html-builder", "1.5.0")
}

kotless {
    config {

        aws {
            prefix = "ktor-short"

            storage {
                bucket = "eu.ktor-short.s3.ktls.aws.intellij.net"
            }

            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        dns("ktor.short", "kotless.io")
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
