package io.kotless.plugin.gradle.tasks

import io.kotless.*
import io.kotless.parser.KotlessDslParser
import io.kotless.plugin.gradle.dsl.*
import io.kotless.plugin.gradle.utils._ktSourceSet
import io.kotless.plugin.gradle.utils._shadowJar
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

/**
 * KotlessGenerate task generates terraform code from Kotlin code written with Kotless.
 *
 * It takes all the configuration from global KotlessDSL configuration (at `kotless` field)
 *
 * @see kotless
 *
 * Note: Task is cacheable and will regenerate code only if sources or configuration has changed.
 */
@CacheableTask
open class KotlessGenerate : DefaultTask() {
    init {
        group = "kotless"
    }

    @get:Input
    val dsl: KotlessDsl
        get() = project.kotless

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val allSources: Set<File>
        get() = project.kotless.webapps.flatMap { project._ktSourceSet }.toSet()

    @get:OutputDirectory
    val genDir: File
        get() = project.kotless.kotlessConfig.genDirectory

    @TaskAction
    fun generate() {
        genDir.deleteRecursively()

        val config = dsl.toSchema()

        val lambdas = HashSet<Lambda>()
        val statics = HashSet<StaticResource>()

        val webapps = dsl.webapps.map { webapp ->
            val project = webapp.project(project)
            val sources = project._ktSourceSet
            val shadowJar = project._shadowJar().archiveFile.get().asFile

            val lambdaConfig = Lambda.Config(webapp.lambda.memoryMb, webapp.lambda.timeoutSec, webapp.lambda.autowarm, webapp.lambda.autowarmMinutes, webapp.packages)

            val parsedWebapp = KotlessDslParser(project.configurations.getByName("compile").files.toSet())
                .parseFromFiles(shadowJar, lambdaConfig, config.bucket, config.workDirectory, sources)

            lambdas += parsedWebapp.lambdas
            statics += parsedWebapp.statics

            val route53 = webapp.route53?.toSchema()
            Webapp(route53, Webapp.ApiGateway(project.name,
                webapp.deployment.toSchema(),
                parsedWebapp.dynamicRoutes,
                parsedWebapp.staticRoutes
            ))
        }.toSet()

        val schema = Schema(config, webapps, lambdas, statics)

        KotlessEngine.generate(schema)
    }
}
