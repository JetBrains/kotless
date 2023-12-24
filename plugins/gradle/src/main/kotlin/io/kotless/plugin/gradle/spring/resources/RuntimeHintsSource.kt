package io.kotless.plugin.gradle.spring.resources

import io.kotless.plugin.gradle.graal.tasks.GenerateAdapter

object RuntimeHintsSource {
    val type = GenerateAdapter.SourceType.Kotlin
    val filePath = "io/kotless/graal/aws/runtime/RuntimeHints.kt"
    val data = { apiPackages: List<String>?, modelPackages: List<String>? ->
        val fullModelPackages = listOf("com.amazonaws.serverless.proxy.model") + (modelPackages ?: emptyList())

        val apiPackagesListStr = if (apiPackages != null) "listOf(${apiPackages.joinToString(", ") { "\"$it\"" }})" else "emptyList<String>()"
        val modelPackagesListStr = "listOf(${fullModelPackages.joinToString(", "){ "\"$it\"" }})"

        //language=kotlin
        """                        
            package io.kotless.graal.aws.runtime

            import org.springframework.aot.hint.MemberCategory
            import org.springframework.aot.hint.RuntimeHints
            import org.springframework.aot.hint.RuntimeHintsRegistrar
            import org.springframework.core.io.support.PathMatchingResourcePatternResolver

            class RuntimeHints : RuntimeHintsRegistrar {
                override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader) {
                    val proxies = hints.proxies()
                    val reflection = hints.reflection()

                    val apiPackages = $apiPackagesListStr
                    val modelPackages = $modelPackagesListStr
                    
                    apiPackages.forEach { apiPackage ->
                        getPackageClasses(apiPackage).forEach { clazz ->
                            proxies.registerJdkProxy(clazz)                            
                        }                         
                    }

                    modelPackages.forEach { modelPackage ->
                        getPackageClasses(modelPackage).forEach { clazz ->
                            reflection.registerType(
                                clazz,
                                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                                MemberCategory.INVOKE_PUBLIC_METHODS
                            )
                        }
                    }
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
}
