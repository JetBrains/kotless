import io.kotless.buildsrc.Versions

group = rootProject.group
version = rootProject.version


plugins {
    kotlin("plugin.serialization") version "1.5.31" apply true
}

dependencies {
    api(project(":model"))

    api("org.slf4j", "slf4j-api", Versions.slf4j)

    api("org.jetbrains.kotlinx", "kotlinx-serialization-json", Versions.serialization)
}
