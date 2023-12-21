package io.kotless.plugin.gradle.spring.resources

import com.kotlin.aws.runtime.tasks.GenerateAdapter

object MainSource {
    val className = "com.kotless.spring.boot.graal.MainKt"
    val type = GenerateAdapter.SourceType.Kotlin
    val filePath = "com/kotless/spring/boot/graal/Main.kt"
    val data = { mainClass: String ->
        //language=kotlin
        """                        
            package com.kotless.spring.boot.graal

            import io.kotless.dsl.spring.Kotless

            //running the shadow jar with -agentlib:native-image-agent=config-output-dir={outputDir} to use the generated as resource.
            //example: java -agentlib:native-image-agent=config-output-dir=/tmp/test -cp example-0.0.1-all.jar com.kotless.spring.boot.graal.MainKt
            fun main(args: Array<String>) {
                Kotless.getHandler($mainClass::class)

                val environment = System.getenv()

                if (environment["_HANDLER"]?.isNotEmpty() == true) {
                    com.kotlin.aws.runtime.main()
                }
            }
        """.trimIndent()
    }
}
