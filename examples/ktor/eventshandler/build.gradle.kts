import io.kotless.plugin.gradle.dsl.kotless

group = rootProject.group
version = rootProject.version

plugins {
    id("io.kotless") version "0.2.1" apply true
    kotlin("plugin.serialization") version "1.5.31" apply true
}

dependencies {
    implementation("commons-validator", "commons-validator", "1.6")
    implementation("com.amazonaws", "aws-java-sdk-dynamodb", "1.11.650")

    implementation("io.kotless", "ktor-lang-aws", "0.2.1")
    implementation("io.kotless", "ktor-lang", "0.2.1")
    implementation("io.ktor", "ktor-html-builder", "1.5.0")
}

kotless {
    config {

        aws {
            prefix = "ktor-events-handler"

            storage {
                bucket = "eu.ktor-events.s3.ktls.aws.intellij.net"
            }

            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
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
