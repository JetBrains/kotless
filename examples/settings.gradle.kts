rootProject.name = "examples"

include(":common:site-shared")

include(":kotless:site")
include(":kotless:shortener")
include(":kotless:eventshandler")

include(":ktor:site")
include(":ktor:shortener")
include(":ktor:eventshandler")

include(":spring:site")
include(":spring:shortener")


pluginManagement {
    resolutionStrategy {
        this.eachPlugin {
            if (requested.id.id == "io.kotless") {
                useModule("io.kotless:gradle:${this.requested.version}")
            }
        }
    }

    repositories {
        mavenLocal()
        maven(url = uri("https://packages.jetbrains.team/maven/p/ktls/maven"))
        gradlePluginPortal()
        mavenCentral()
    }
}
