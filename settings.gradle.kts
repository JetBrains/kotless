rootProject.name = "kotless"

include(":schema")
include(":model")
include(":engine")

include(":dsl:common:lang-common")
include(":dsl:common:lang-parser-common")

include(":dsl:kotless:lang")
include(":dsl:kotless:lang-local")
include(":dsl:kotless:lang-parser")

include(":dsl:spring:spring-lang")
include(":dsl:spring:spring-boot-lang")
include(":dsl:spring:spring-lang-parser")

include(":dsl:ktor:ktor-lang")
include(":dsl:ktor:ktor-lang-local")
include(":dsl:ktor:ktor-lang-parser")

include(":plugins:gradle")
