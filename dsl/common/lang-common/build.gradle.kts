import io.kotless.buildsrc.Versions
import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


plugins {
    kotlin("plugin.serialization") version "1.4.21" apply true
}

dependencies {
    api(project(":model"))

    api("org.slf4j", "slf4j-api", Versions.slf4j)

    api("com.amazonaws", "aws-java-sdk-core", Versions.aws) {
        exclude("com.fasterxml.jackson.core")
        exclude("com.fasterxml.jackson.dataformat")
    }
    //override old jackson version
    api("com.fasterxml.jackson.core", "jackson-databind", "2.10.3")
    api("com.fasterxml.jackson.dataformat", "jackson-dataformat-cbor", "2.10.3")

    api("org.jetbrains.kotlinx", "kotlinx-serialization-json", Versions.serialization)

    api("com.amazonaws", "aws-lambda-java-core", Versions.lambdaJavaCore)
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.kotless"
        info {
            description = "Kotless Lang Common"
            githubRepo = "https://github.com/JetBrains/kotless"
            vcsUrl = "https://github.com/JetBrains/kotless"
            labels.addAll(listOf("kotlin", "serverless", "web", "devops", "faas", "lambda"))
        }
    }
}

