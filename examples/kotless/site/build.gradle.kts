import io.kotless.plugin.gradle.dsl.Webapp
import io.kotless.plugin.gradle.dsl.kotless

group = rootProject.group
version = rootProject.version

plugins {
    id("io.kotless") version "0.2.0" apply true
}

dependencies {
    implementation("io.kotless", "kotless-lang-aws", "0.2.0")
    implementation("io.kotless", "kotless-lang", "0.2.0")

    implementation(project(":common:site-shared"))

    implementation("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.11")
}

kotless {
    config {
        bucket = "eu.site.s3.ktls.aws.intellij.net"
        prefix = "site"

        aws {
            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        dns("site", "kotless.io")
    }
}

