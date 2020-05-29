package io.kotless.plugin.gradle.tasks.local

import io.kotless.Constants
import io.kotless.DSLType
import io.kotless.InternalAPI
import io.kotless.parser.LocalParser
import io.kotless.plugin.gradle.dsl.KotlessDSL
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.utils.*
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
internal open class KotlessLocalRunTask : DefaultTask() {

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

    @get:Internal
    lateinit var localstack: LocalStackRunner

    @TaskAction
    @OptIn(InternalAPI::class)
    fun act() = with(project) {
        val dsl = Dependencies.dsl(project)

        require(dsl.isNotEmpty()) { "Cannot find \"lang\", \"ktor-lang\" or \"spring-boot-lang\" dependencies. One of them required for local start." }
        require(dsl.size <= 1) { "Only one dependency should be used for DSL: either \"lang\", \"ktor-lang\" or \"spring-boot-lang\"." }

        val (type, dependency) = dsl.entries.single()

        dependencies {
            myLocal("io.kotless", type.lib, dependency.version.toString())
        }

        tasks.myGetByName<JavaExec>("run").apply {
            classpath += files(myLocal().files)

            environment[Constants.Local.serverPort] = myKotless.extensions.local.port

            if (type != DSLType.Ktor || type != DSLType.SpringBoot) {
                val local = LocalParser.parse(myAllSources, Dependencies.getDependencies(project))
                environment[Constants.Local.KtorOrSpring.classToStart] = local.entrypoint.qualifiedName.substringBefore("::")
            }

            if (type != DSLType.Kotless) {
                environment[Constants.Local.Kotless.workingDir] = myKotless.config.dsl.staticsRoot.canonicalPath
            }

            if (myKotless.config.optimization.autowarm.enable) {
                environment[Constants.Local.autowarmMinutes] = myKotless.config.optimization.autowarm.minutes
            }

            for ((key, value) in myKotless.webapp.lambda.mergedEnvironment) {
                environment[key] = value
            }

            if (myKotless.extensions.local.useAWSEmulation) {
                environment.putAll(localstack.envMap)
            }
        }
    }
}
