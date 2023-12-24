@file:Suppress("unused")

package io.kotless.plugin.gradle.graal

import io.kotless.plugin.gradle.graal.dsl.RuntimePluginExtension
import io.kotless.plugin.gradle.graal.dsl.runtime
import io.kotless.plugin.gradle.graal.tasks.ConfigureGraal
import io.kotless.plugin.gradle.graal.tasks.GenerateAdapter
import io.kotless.plugin.gradle.graal.tasks.createGraalJar
import io.kotless.plugin.gradle.graal.tasks.createShadowJarGraal
import io.kotless.plugin.gradle.graal.utils.mySourceSets
import io.kotless.plugin.gradle.graal.utils.sourceSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

class RuntimeKotlinGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.github.johnrengelman.shadow")
            pluginManager.apply("com.bmuschko.docker-remote-api")

            runtime = RuntimePluginExtension()

            val jar = createGraalJar()
            val shadow = createShadowJarGraal(jar)

            val generateAdapter = tasks.create("generateAdapter", GenerateAdapter::class.java)

            afterEvaluate {
                target.mySourceSets.apply {
                    tasks.getByName("compileKotlin").dependsOn(generateAdapter)
                    this["main"].sourceSet.srcDir(runtime.kotlinGenerationPathOrDefault(target))

                    if(runtime.additionalSources?.any { it.type == GenerateAdapter.SourceType.Java } == true) {
                        tasks.getByName("compileJava").dependsOn(generateAdapter)
                        this["main"].java.srcDir(runtime.javaGenerationPathOrDefault(target))
                    }

                    if(runtime.additionalSources?.any { it.type == GenerateAdapter.SourceType.Resource } == true) {
                        tasks.getByName("processResources").dependsOn(generateAdapter)
                        this["main"].resources.srcDir(runtime.resourcesGenerationPathOrDefault(target))
                    }
                }
                ConfigureGraal.setupGraalVMTasks(target, shadow)
            }
        }
    }
}
