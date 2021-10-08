rootProject.name = "examples"

include(":common:site-shared")

include(":kotless:site")
include(":kotless:shortener")

include(":ktor:site")
include(":ktor:shortener")

include(":spring:site")
include(":spring:shortener")


pluginManagement {
    resolutionStrategy {
        this.eachPlugin {

            if (requested.id.id == "io.reflekt") {
                useModule("io.reflekt:gradle-plugin:${this.requested.version}")
            }
        }
    }

    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))
    }
}


