rootProject.name = "kotless"

include(":schema")
include(":model")
include(":engine")
include(":ktls-dsl:lang")
include(":ktls-dsl:lang-parser")
include(":plugins:gradle")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlinx-serialization") {
                useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
            }
        }
    }
}

