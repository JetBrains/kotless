rootProject.name = "kotless"

include(":schema")
include(":model")
include(":engine")

include(":dsl:utils:parser-utils")
include(":dsl:utils:lang-utils")

include(":dsl:kotless:lang")
include(":dsl:kotless:lang-parser")

include(":dsl:ktor:ktor-lang")

include(":plugins:gradle")
