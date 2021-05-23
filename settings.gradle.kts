rootProject.name = "kotless"

include(":schema")
include(":model")
include(":engine")

include(":dsl:common:dsl-common")
include(":dsl:common:cloud:dsl-common-aws")
include(":dsl:common:cloud:dsl-common-azure")
include(":dsl:common:dsl-parser-common")

include(":dsl:kotless:kotless-lang")
include(":dsl:kotless:kotless-lang-local")
include(":dsl:kotless:kotless-lang-parser")
include(":dsl:kotless:cloud:kotless-lang-aws")
include(":dsl:kotless:cloud:kotless-lang-azure")

include(":dsl:spring:spring-boot-lang")
include(":dsl:spring:spring-boot-lang-local")
include(":dsl:spring:spring-lang-parser")

include(":dsl:ktor:ktor-lang")
include(":dsl:ktor:ktor-lang-local")
include(":dsl:ktor:ktor-lang-parser")
include(":dsl:ktor:cloud:ktor-lang-aws")
include(":dsl:ktor:cloud:ktor-lang-azure")

include(":plugins:gradle")
