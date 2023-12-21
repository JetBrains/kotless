package io.kotless.plugin.gradle.tasks.local

import io.kotless.*
import io.kotless.parser.LocalParser
import io.kotless.plugin.gradle.dsl.*
import io.kotless.plugin.gradle.utils.gradle.*
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.ApplicationPluginConvention
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getPlugin
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

    private val finalizers = ArrayList<() -> Unit>()

    fun onShutDown(vararg finalizer: () -> Unit): KotlessLocalRunTask {
        finalizers.addAll(finalizer)
        return this
    }

    @get:Internal
    lateinit var localstack: LocalStackRunner

    @TaskAction
    @OptIn(InternalAPI::class)
    fun act() = with(project) {
        val dsl = Dependencies.dsl(project)

        require(dsl.isNotEmpty()) { "Cannot find \"kotless-lang\", \"ktor-lang\" or \"spring-boot-lang\" dependencies. One of them required for local start." }
        require(dsl.size <= 1) { "Only one dependency should be used for DSL: either \"kotless-lang\", \"ktor-lang\" or \"spring-boot-lang\"." }

        val (type, dependency) = dsl.entries.single()

        dependencies {
            myLocal("io.kotless", type.descriptor.localLibrary, dependency.version ?: error("Explicit version is required for Kotless DSL dependency."))
        }

        val run = tasks.myGetByName<JavaExec>("run").apply {
            classpath += files(myLocal().files)

            environment[Constants.Local.serverPort] = myKotless.extensions.local.port

            if (type == DSLType.Ktor || type == DSLType.SpringBoot) {
                val local = LocalParser.parse(myAllSources, Dependencies.getDependencies(project))
                environment[Constants.Local.KtorOrSpring.classToStart] = local.entrypoint.qualifiedName.substringBefore("::")
            }

            if (type == DSLType.Kotless) {
                environment[Constants.Local.Kotless.workingDir] = myKotless.config.dsl.resolvedStaticsRoot.canonicalPath
            }

            if (myKotless.config.optimization.autowarm.enable) {
                environment[Constants.Local.autowarmMinutes] = myKotless.config.optimization.autowarm.minutes
            }

            for ((key, value) in myKotless.webapp.lambda.mergedEnvironment) {
                environment[key] = value
            }

            if (myKotless.extensions.local.useAWSEmulation) {
                environment.putAll(localstack.environment)
            }

            isIgnoreExitValue = true

            if(myKotless.extensions.local.debugPort != null) {
                debugOptions {
                    it.enabled.set(true)
                    it.server.set(true)
                    it.port.set(myKotless.extensions.local.debugPort)
                    it.suspend.set(myKotless.extensions.local.suspendDebug)
                }
            }
        }

        try {
            convention.getPlugin<ApplicationPluginConvention>().mainClassName = kotless.config.dsl.typeOrDefault.descriptor.localEntryPoint
            run.exec()
        } catch (e: Throwable) {
            logger.lifecycle("Gracefully shutting down Kotless local")
            //Remove interrupted flag before execution of finalizers
            Thread.interrupted()
            finalizers.forEach { it.invoke() }
            //Rethrow exception after finalizers executed
            throw e
        }
    }
}
