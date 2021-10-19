import io.kotless.buildsrc.Versions

group = rootProject.group
version = rootProject.version


plugins {
    kotlin("plugin.serialization") version "1.5.31" apply true
}

dependencies {
    api(project(":model"))

    api("org.slf4j", "slf4j-api", Versions.slf4j)

    api("com.amazonaws", "aws-java-sdk-core", Versions.aws) {
        exclude("com.fasterxml.jackson.core")
        exclude("com.fasterxml.jackson.dataformat")
    }
    api("com.microsoft.azure.functions", "azure-functions-java-library", "1.2.2")
    //override old jackson version
    api("com.fasterxml.jackson.core", "jackson-databind", "2.10.3")
    api("com.fasterxml.jackson.dataformat", "jackson-dataformat-cbor", "2.10.3")

    api("org.jetbrains.kotlinx", "kotlinx-serialization-json", Versions.serialization)

    api("com.amazonaws", "aws-lambda-java-core", Versions.lambdaJavaCore)
}
