import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless
import io.reflekt.plugin.reflekt

group = rootProject.group
version = rootProject.version

plugins {
    id("io.kotless") version "0.2.0" apply true
    id("io.reflekt") version "1.5.30" apply true
}

dependencies {
    compileClasspath("io.kotless",  "kotless-lang", "0.2.0")
    api("io.kotless",  "kotless-lang-local", "0.2.0")
    api("io.kotless", "kotless-lang-aws", "0.2.0")

    api(project(":common:site-shared"))

    api("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.11")
}

kotless {
    config {
        bucket = "eu.site.s3.ktls.aws.intellij.net"
        prefix = "site"

        terraform {
            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        route53 = Route53("site", "kotless.io")
    }
}

reflekt {
    // Enable or disable Reflekt plugin
    enabled = true
    // List of external libraries for dependency search
    // Use only DependencyHandlers which has canBeResolve = True
    // Note: Reflekt works only with kt files from libraries
    librariesToIntrospect = listOf("io.kotless:kotless-lang:0.2.0")
}

