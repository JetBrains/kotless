package io.kotless.plugin.gradle

import com.kotlin.aws.runtime.dsl.runtime
import io.kotless.DSLType
import io.kotless.parser.LocalParser
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.utils.gradle.*
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.dependencies

object KotlessRuntimeTasks {
    fun Project.setupGraal() {
        if (kotless.config.dsl.typeOrDefault != DSLType.Ktor && kotless.config.dsl.typeOrDefault != DSLType.SpringBoot) {
            project.logger.warn("GraalVM Runtime can be used only with Ktor DSL for now")
            return
        }

        dependencies {
            myImplementation("com.kotlin.aws.runtime", "runtime-graalvm", "0.1.3")
        }

        applyPluginSafely("com.kotlin.aws.runtime")

        val projectFlags = if(kotless.config.dsl.typeOrDefault == DSLType.SpringBoot) listOf("-Dspring.aot.enabled=true") else null

        runtime {
            handler = LocalParser.parse(project.myKtSourceSet.toSet(), Dependencies.getDependencies(project)).entrypoint.qualifiedName
            classAnnotations = "@OptIn(io.kotless.InternalAPI::class)"
            config {
                image = "ghcr.io/graalvm/graalvm-community:21"
                flags = projectFlags
                useFullFlgas = true
            }
        }

        afterEvaluate {
            val graalShadowJar = tasks.getByName("buildGraalRuntime") as AbstractArchiveTask
            kotless.config.setArchiveTask(graalShadowJar)
            tasks.getByName("initialize").dependsOn(graalShadowJar)

            val generateAdapter = tasks.getByName("generateAdapter")
            tasks.getByName("generate").dependsOn(generateAdapter)
        }
    }
}
