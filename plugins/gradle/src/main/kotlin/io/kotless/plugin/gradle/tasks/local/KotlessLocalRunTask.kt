package io.kotless.plugin.gradle.tasks.local

import io.kotless.parser.LocalParser
import io.kotless.plugin.gradle.dsl.KotlessDSL
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.utils.*
import io.kotless.plugin.gradle.utils.myKtSourceSet
import io.kotless.plugin.gradle.utils.myLocal
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.dependencies
import java.io.File

/**
 * KotlessLocal task runs Kotless application locally
 *
 * @see kotless
 *
 * Note: Task is cacheable and will regenerate code only if sources or configuration has changed.
 */
@CacheableTask
open class KotlessLocalRunTask : DefaultTask() {

    init {
        group = Groups.kotless
    }

    @get:Input
    val myKotless: KotlessDSL
        get() = project.kotless

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val myAllSources: Set<File>
        get() = project.myKtSourceSet.toSet()

    lateinit var localstack: LocalStackRunner

    @TaskAction
    fun act() = with(project) {
        val depsConfiguration = configurations.getByName(myKotless.config.configurationName)
        val deps = depsConfiguration.allDependencies

        val ktorVersion = deps.find { it.group == "io.kotless" && (it.name == "ktor-lang") }?.version
        val kotlessVersion = deps.find { it.group == "io.kotless" && (it.name == "lang") }?.version

        require(ktorVersion != null || kotlessVersion != null) { "Cannot find \"lang\" or \"ktor-lang\" dependencies. One of them required for local start." }
        require(ktorVersion == null || kotlessVersion == null) { "Both \"lang\" and \"ktor-lang\" dependencies found. Only one of them should be used." }


        dependencies {
            when {
                ktorVersion != null -> myLocal("io.kotless", "ktor-lang-local", ktorVersion)
                kotlessVersion != null -> myLocal("io.kotless", "lang-local", kotlessVersion)
            }
        }

        tasks.myGetByName<JavaExec>("run").apply {
            classpath += files(myLocal().files)

            environment["SERVER_PORT"] = myKotless.extensions.local.port

            if (ktorVersion != null) {
                val local = LocalParser.parse(myAllSources, depsConfiguration.files.toSet())
                environment["CLASS_TO_START"] = local.entrypoint.qualifiedName.substringBefore("::")
            }

            if (kotlessVersion != null) {
                environment["WORKING_DIR"] = myKotless.config.dsl.workDirectory.canonicalPath
            }

            if (myKotless.config.optimization.autowarm.enable) {
                environment["AUTOWARM_MINUTES"] = myKotless.config.optimization.autowarm.minutes
            }

            for ((key, value) in myKotless.webapp.lambda.environment) {
                environment[key] = value
            }

            if (myKotless.extensions.local.useAWSEmulation) {
                environment.putAll(localstack.envMap)
            }
        }
    }
}
