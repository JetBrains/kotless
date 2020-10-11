import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless

group = rootProject.group
version = rootProject.version


plugins {
    id("io.kotless") version "0.1.7-beta-4" apply true
}

dependencies {
    implementation("io.kotless", "spring-boot-lang", "0.1.7-beta-4")

    implementation(project(":common:site-shared"))
}

kotless {
    config {
        bucket = "eu.spring-site.s3.ktls.aws.intellij.net"
        prefix = "spring-site"

        terraform {
            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        route53 = Route53("spring.site", "kotless.io")
    }
}

