import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.72" apply true
}

dependencies {
    api(project(":model"))

    api("com.amazonaws", "aws-java-sdk-core", "1.11.788")

    api("org.jetbrains.kotlinx", "kotlinx-serialization-runtime", "0.20.0")

    api("com.amazonaws", "aws-lambda-java-core", "1.2.0")

    api("org.slf4j", "slf4j-api", "1.7.30")
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

