package io.kotless.plugin.gradle.tasks.gen

import io.kotless.Application
import io.kotless.KotlessEngine
import io.kotless.Schema
import io.kotless.plugin.gradle.dsl.KotlessDSL
import io.kotless.plugin.gradle.dsl.descriptor
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.dsl.toSchema
import io.kotless.plugin.gradle.utils.getRuntimeVersion
import io.kotless.plugin.gradle.utils.getTargetVersion
import io.kotless.plugin.gradle.utils.gradle.*
import io.kotless.plugin.gradle.utils.isCompatible
import io.kotless.resource.Lambda
import io.kotless.terraform.TFFile
import org.codehaus.plexus.util.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.get
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
internal open class KotlessGenerateTask : DefaultTask() {
    init {
        group = Groups.kotless
    }

    @get:Input
    val myKotless: KotlessDSL
        get() = project.kotless

    @get:Input
    val myTargetVersion: JavaVersion?
        get() = project.getTargetVersion()

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val myAllSources: Set<File>
        get() = project.myKtSourceSet.toSet()

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val myAllResources: Set<File>
        get() = project.myResourcesSet.toSet()


    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val myTerraformAdditional: Set<File>
        get() = project.kotless.extensions.terraform.files.additional

    @get:OutputDirectory
    val myGenDirectory: File
        get() = project.kotless.config.deployGenDirectory

    @TaskAction
    fun act() {
        myGenDirectory.clearDirectory()

        val schema = parseSources()
        val generated = KotlessEngine.generate(schema)
        dumpGeneratedFiles(generated)
    }

    private fun parseSources(): Schema {
        val dsl = myKotless.config.dsl.typeOrDefault
        val config = myKotless.toSchema()
        val webapp = myKotless.webapp
        val jar = (project.tasks[myKotless.config.myArchiveTask] as AbstractArchiveTask).archiveFile.get().asFile
        val target = myTargetVersion ?: error("Unable to find Kotlin compile-target version.")

        val runtime = webapp.lambda.runtime
            ?: project.getRuntimeVersion(target) ?: error("Kotless was unable to deduce Lambda Runtime for $target. Please, set it directly.")

        require(runtime.isCompatible(target)) {
            "Stated in Gradle DSL runtime $runtime is not compatible with current compile target $target"
        }

        val lambda = Lambda.Config(webapp.lambda.memoryMb, webapp.lambda.timeoutSec, runtime, webapp.lambda.mergedEnvironment)

        val parsed = dsl.descriptor.parser.parse(myAllSources, myAllResources, jar, config, lambda, Dependencies.getDependencies(project))

        val app = Application(
            route53 = webapp.route53?.toSchema(),
            api = Application.ApiGateway(
                name = project.name,
                deployment = webapp.deployment.toSchema(project.path),
                dynamics = parsed.routes.dynamics,
                statics = parsed.routes.statics
            ),
            events = Application.Events(parsed.events.scheduled)
        )

        return Schema(
            config = config,
            application = app,
            lambdas = parsed.resources.dynamics,
            statics = parsed.resources.statics
        )
    }

    private fun dumpGeneratedFiles(generated: Set<TFFile>) {
        val files = KotlessEngine.dump(myGenDirectory, generated)
        for (file in myTerraformAdditional) {
            require(files.all { it.name != file.name }) { "Extending terraform file `${file.absolutePath}` clashes with generated file" }
            FileUtils.copyFile(file, File(myGenDirectory, file.name))
        }
    }
}
