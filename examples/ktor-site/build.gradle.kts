import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.kotless"
version = "0.1.3"

plugins {
    id("tanvd.kosogor") version "1.0.7" apply true

    kotlin("jvm") version "1.3.61" apply true

    id("io.kotless") version "0.1.3" apply true
}

repositories {
    mavenLocal()
    //artifacts are located at JCenter
    jcenter()
}

dependencies {
    implementation("io.kotless", "ktor-lang", "0.1.3")
    implementation("io.ktor", "ktor-html-builder", "1.2.5")
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.3"
        apiVersion = "1.3"
    }
}

kotless {
    config {
        bucket = "eu.ktor-site.s3.ktls.aws.intellij.net"
        prefix = "ktor-site"

        terraform {
            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        route53 = Route53("ktor.site", "kotless.io")
    }
}

