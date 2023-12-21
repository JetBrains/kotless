package io.kotless.plugin.gradle.spring.resources

import com.kotlin.aws.runtime.tasks.GenerateAdapter

object AotFactoriesResourceSource {
    val type = GenerateAdapter.SourceType.Resource
    val filePath = "META-INF/spring/aot.factories"
    val data =
        """                        
            org.springframework.aot.hint.RuntimeHintsRegistrar=com.kotlin.aws.runtime.RuntimeHints
        """.trimIndent()
}
