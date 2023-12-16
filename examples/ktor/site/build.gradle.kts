import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.resource.Lambda.Config.Runtime.GraalVM

group = rootProject.group
version = rootProject.version


plugins {
    id("io.kotless") version "0.3.0" apply true
}

dependencies {
    implementation("io.kotless", "ktor-lang-aws", "0.3.0")
    implementation("io.kotless", "ktor-lang", "0.3.0")

    implementation(project(":common:site-shared"))
}

kotless {
    config {

        aws {
            prefix = "ktor-site"

            storage {
                bucket = "eu.ktor-site.s3.ktls.aws.intellij.net"
            }

            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        dns("ktor.site", "kotless.io")

//        lambda {
//            runtime = GraalVM
//        }
    }
}
