import io.kotless.buildsrc.Versions
import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.72" apply true
}

dependencies {
    api(project(":model"))

    api("com.amazonaws", "aws-java-sdk-core", Versions.aws)

    api("org.jetbrains.kotlinx", "kotlinx-serialization-runtime", Versions.serialization)

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

