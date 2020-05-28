package io.kotless.plugin.gradle.tasks.local

import io.kotless.Constants
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

    @get:Internal
    lateinit var localstack: LocalStackRunner

    @TaskAction
    @OptIn(InternalAPI::class)
    fun act() = with(project) {
        val ktorVersion = Dependencies.getKtorDependency(this)?.version
        val kotlessVersion = Dependencies.getKotlessDependency(this)?.version
        val springVersion = Dependencies.getSpringBootDependency(this)?.version

        val all = listOf(ktorVersion, kotlessVersion, springVersion)

        require(all.any { it != null }) { "Cannot find \"lang\", \"ktor-lang\" or \"spring-boot-lang\" dependencies. One of them required for local start." }
        require(all.count { it != null } <= 1) { "Only one dependency should be used for DSL: either \"lang\", \"ktor-lang\" or \"spring-boot-lang\"." }

        dependencies {
            when {
                ktorVersion != null -> myLocal("io.kotless", "ktor-lang-local", ktorVersion)
                kotlessVersion != null -> myLocal("io.kotless", "lang-local", kotlessVersion)
                springVersion != null -> myLocal("io.kotless", "spring-boot-lang-local", springVersion)
            }
        }

        tasks.myGetByName<JavaExec>("run").apply {
            classpath += files(myLocal().files)

            environment[Constants.Local.serverPort] = myKotless.extensions.local.port

            if (ktorVersion != null || springVersion != null) {
                val local = LocalParser.parse(myAllSources, Dependencies.getDependencies(project))
                environment[Constants.Local.KtorOrSpring.classToStart] = local.entrypoint.qualifiedName.substringBefore("::")
            }

            if (kotlessVersion != null) {
                environment[Constants.Local.Kotless.workingDir] = myKotless.config.dsl.workDirectory.canonicalPath
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
