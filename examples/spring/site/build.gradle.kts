import io.kotless.plugin.gradle.dsl.kotless

group = rootProject.group
version = rootProject.version


plugins {
    id("io.kotless") version "0.2.0" apply true
}

dependencies {
    implementation("io.kotless", "spring-boot-lang", "0.2.0")

    implementation(project(":common:site-shared"))
}

kotless {
    config {

        aws {
            prefix = "spring-site"

            storage {
                bucket = "eu.spring-site.s3.ktls.aws.intellij.net"
            }

            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        dns("spring.site", "kotless.io")
    }
}

