package io.kotless.plugin.gradle.tasks.gen

import io.kotless.*
import io.kotless.parser.KotlessParser
import io.kotless.parser.ktor.KTorParser
import io.kotless.parser.spring.SpringParser
import io.kotless.plugin.gradle.dsl.KotlessDSL
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.dsl.toSchema
import io.kotless.plugin.gradle.utils.*
import io.kotless.terraform.TFFile
import org.codehaus.plexus.util.FileUtils
import org.gradle.api.DefaultTask
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
        val config = myKotless.toSchema()

        val myWebapp = myKotless.webapp

        val jar = (project.tasks[myKotless.config.myArchiveTask] as AbstractArchiveTask).archiveFile.get().asFile

        val lambda = Lambda.Config(myWebapp.lambda.memoryMb, myWebapp.lambda.timeoutSec, myWebapp.lambda.mergedEnvironment)

        val parsed = when (myKotless.config.dsl.typeOrDefault) {
            DSLType.Kotless -> KotlessParser.parse(myAllSources, myAllResources, jar, config, lambda, Dependencies.getDependencies(project))
            DSLType.Ktor -> KTorParser.parse(myAllSources, myAllResources, jar, config, lambda, Dependencies.getDependencies(project))
            DSLType.Spring -> SpringParser.parse(myAllSources, myAllResources, jar, config, lambda, Dependencies.getDependencies(project))
        }

        val webapp = Webapp(
            route53 = myWebapp.route53?.toSchema(),
            api = Webapp.ApiGateway(
                name = project.name,
                deployment = myWebapp.deployment.toSchema(project.path),
                dynamics = parsed.routes.dynamics,
                statics = parsed.routes.statics
            ),
            events = Webapp.Events(parsed.events.scheduled)
        )

        return Schema(
            config = config,
            webapp = webapp,
            lambdas = parsed.resources.dynamics,
            statics = parsed.resources.statics
        )
    }

    private fun dumpGeneratedFiles(generated: Set<TFFile>) {
        val files = KotlessEngine.dump(myGenDirectory, generated)
        for (file in myTerraformAdditional) {
            require(files.all { it.name != file.name }) { "Extending terraform file with name ${file.name} clashes with generated file" }
            FileUtils.copyFile(file, File(myGenDirectory, file.name))
        }
    }
}
