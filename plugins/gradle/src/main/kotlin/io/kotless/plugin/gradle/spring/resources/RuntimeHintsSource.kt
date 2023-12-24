package io.kotless.plugin.gradle.spring.resources

import io.kotless.plugin.gradle.graal.tasks.GenerateAdapter

object RuntimeHintsSource {
    val type = GenerateAdapter.SourceType.Kotlin
    val filePath = "io/kotless/graal/aws/runtime/RuntimeHints.kt"
    val data = { additionalPackages: List<String>? ->
        val packages = listOf("com.amazonaws.serverless.proxy.model") + (additionalPackages ?: emptyList())

        """                        
            package io.kotless.graal.aws.runtime

            import org.springframework.aot.hint.MemberCategory
            import org.springframework.aot.hint.RuntimeHints
            import org.springframework.aot.hint.RuntimeHintsRegistrar
            import org.springframework.core.io.support.PathMatchingResourcePatternResolver

            class RuntimeHints : RuntimeHintsRegistrar {
                override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader) {
                    val reflection = hints.reflection()

${packages.joinToString("\n") { readPackageLogic(it) }}
                }

                private fun getPackageClasses(path: String): List<Class<*>> {
                    return PathMatchingResourcePatternResolver()
                        .getResources("classpath*:${'$'}{path.replace(".", "/")}/*.class")
                        .map { path + "." + it.filename }
                        .filter { it.endsWith(".class") }
                        .map { it.substring(0, it.length - ".class".length) }
                        .mapNotNull { Result.runCatching { Class.forName(it) }.getOrNull() }
                }
            }
        """.trimIndent()
    }

    private fun readPackageLogic(pkg: String): String {
        val tabs = "    ".repeat(5)

        return """
            getPackageClasses("$pkg").forEach {
                reflection.registerType(
                    it,
                    MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                    MemberCategory.INVOKE_PUBLIC_METHODS
                )
            }
        """.replaceIndent(tabs)
    }
}
