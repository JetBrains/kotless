package io.kotless.plugin.gradle.tasks.gen

import io.kotless.AwsResource
import io.kotless.plugin.gradle.dsl.KotlessDSL
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.utils.myKtSourceSet
import io.kotless.terraform.infra.aws_provider
import io.kotless.terraform.infra.terraform
import io.kotless.terraform.tf
import org.codehaus.plexus.util.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

@CacheableTask
open class KotlessLocalGenerateTask : DefaultTask() {
    init {
        group = "kotless"
    }

    @get:Input
    val myKotless: KotlessDSL
        get() = project.kotless

    @get:Input
    lateinit var services: Map<AwsResource, String>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val allSources: Set<File>
        get() = project.myKtSourceSet.toSet()

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val allTerraformAddition: Set<File>
        get() = project.kotless.extensions.terraform.files.additional

    @get:OutputDirectory
    val myGenDirectory: File
        get() = project.kotless.config.localGenDirectory

    @TaskAction
    fun act() {
        myGenDirectory.deleteRecursively()
        myGenDirectory.mkdirs()

        val infra_file = tf("infra") {
            terraform {
                required_version = myKotless.config.terraform.version
            }


            aws_provider {
                region = "us-east-1"
                version = myKotless.config.terraform.provider.version

                skip_credentials_validation = true
                skip_metadata_api_check = true
                skip_requesting_account_id = true

                endpoints(services.mapKeys { it.key.prefix })
            }
        }

        infra_file.write(File(myGenDirectory, infra_file.nameWithExt))
        for (file in allTerraformAddition) {
            FileUtils.copyFile(file, File(myGenDirectory, file.name))
        }
    }
}
