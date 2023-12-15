package io.kotless.buildsrc

object Versions {
    //Note, that it also should be changed in dependencies of buildSrc and in plugins blocks
    //Due to limitations of Gradle DSL
    const val kotlin = "1.9.21"
    const val serialization = "1.6.2"

    const val aws = "1.12.618"
    const val lambdaJavaCore = "1.2.3"

    const val slf4j = "1.7.30"
    const val logback = "1.2.3"

    const val ktor = "1.5.0"

    const val serverlessContainers = "1.9.3"
    const val springBoot = "2.7.18"
    const val spring = "6.0.11"

    const val quartz = "2.3.2"
}
