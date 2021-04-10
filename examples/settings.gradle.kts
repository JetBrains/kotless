rootProject.name = "examples"

include(":common:site-shared")

include(":kotless:site")
include(":kotless:shortener")

include(":ktor:site")
include(":ktor:shortener")

include(":spring:site")
include(":spring:shortener")


pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}
