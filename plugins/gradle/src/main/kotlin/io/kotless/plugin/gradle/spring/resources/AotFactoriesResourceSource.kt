package io.kotless.plugin.gradle.spring.resources

import io.kotless.plugin.gradle.graal.tasks.GenerateAdapter

object AotFactoriesResourceSource {
    val type = GenerateAdapter.SourceType.Resource
    val filePath = "META-INF/spring/aot.factories"
    val data =
        """                        
            org.springframework.aot.hint.RuntimeHintsRegistrar=io.kotless.graal.aws.runtime.RuntimeHints
        """.trimIndent()
}
