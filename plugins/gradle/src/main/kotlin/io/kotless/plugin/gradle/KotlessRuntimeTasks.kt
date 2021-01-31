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
        if (kotless.config.dsl.typeOrDefault != DSLType.Ktor) {
            project.logger.warn("GraalVM Runtime can be used only with Ktor DSL for now")
            return
        }

        dependencies {
            myImplementation("com.kotlin.aws.runtime", "runtime", "0.1.1")
        }

        applyPluginSafely("io.kcdk")

        runtime {
            handler = LocalParser.parse(project.myKtSourceSet.toSet(), Dependencies.getDependencies(project)).entrypoint.qualifiedName

            config {
                image = "ghcr.io/graalvm/graalvm-ce:java11-21.0.0"
            }
        }

        afterEvaluate {
            val graalShadowJar = tasks.getByName("buildGraalRuntime") as AbstractArchiveTask
            kotless.config.setArchiveTask(graalShadowJar)
            tasks.getByName("initialize").dependsOn(graalShadowJar)
        }
    }
}
