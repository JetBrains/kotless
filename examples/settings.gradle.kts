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
        gradlePluginPortal()
    }
}
