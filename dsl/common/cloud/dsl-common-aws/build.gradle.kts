group = rootProject.group
version = rootProject.version


plugins {
    kotlin("plugin.serialization") version "1.9.21" apply true
}

dependencies {
    api(project(":dsl:common:dsl-common"))


    //override old jackson version
    api("com.fasterxml.jackson.core", "jackson-databind", "2.10.3")
    api("com.fasterxml.jackson.dataformat", "jackson-dataformat-cbor", "2.10.3")

    api("com.amazonaws", "aws-java-sdk-core", io.kotless.buildsrc.Versions.aws) {
        exclude("com.fasterxml.jackson.core")
        exclude("com.fasterxml.jackson.dataformat")
    }

    api("com.amazonaws", "aws-lambda-java-core", io.kotless.buildsrc.Versions.lambdaJavaCore)
}

