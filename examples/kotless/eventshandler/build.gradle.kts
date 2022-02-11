import io.kotless.plugin.gradle.dsl.kotless

group = rootProject.group
version = rootProject.version

plugins {
    id("io.kotless") version "0.2.1" apply true
    kotlin("plugin.serialization") version "1.5.31" apply true
}

dependencies {
    implementation("io.kotless", "kotless-lang-aws", "0.2.1")
    implementation("io.kotless", "kotless-lang", "0.2.1")
    implementation("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.11")


    implementation(project(":common:site-shared"))
}

kotless {
    config {
        aws {
            prefix = "events"

            storage {
                bucket = "eu.events.s3.ktls.aws.intellij.net"
            }

            profile = "kotless-jetbrains"
        }
    }

    webapp {
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
