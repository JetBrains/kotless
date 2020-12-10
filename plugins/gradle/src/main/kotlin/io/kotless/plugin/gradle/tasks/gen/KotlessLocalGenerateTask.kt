package io.kotless.plugin.gradle.tasks.gen

import io.kotless.AwsResource
import io.kotless.hcl.HCLEntity
import io.kotless.hcl.HCLTextField
import io.kotless.plugin.gradle.dsl.KOTLESS_ENVIRONMENT
import io.kotless.plugin.gradle.dsl.KotlessDSL
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.utils.gradle.Groups
import io.kotless.plugin.gradle.utils.gradle.clearDirectory
import io.kotless.terraform.TFFile
import io.kotless.terraform.infra.aws_provider
import io.kotless.terraform.infra.locals
import io.kotless.terraform.infra.terraform
import io.kotless.terraform.tf
import org.codehaus.plexus.util.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

@CacheableTask
internal open class KotlessLocalGenerateTask : DefaultTask() {
    init {
        group = Groups.`kotless setup`
    }

    @get:Input
    val myKotless: KotlessDSL
        get() = project.kotless

    @get:Input
    lateinit var services: Map<AwsResource, String>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val myTerraformAdditional: Set<File>
        get() = project.kotless.extensions.terraform.files.additional

    @get:OutputDirectory
    val myGenDirectory: File
        get() = project.kotless.config.localGenDirectory

    @TaskAction
    fun act() {
        myGenDirectory.clearDirectory()

        val infra = tf("infra") {
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

            val localVariables = myKotless.extensions.terraform.locals + (KOTLESS_ENVIRONMENT to "local")

            locals {
                variables = object : HCLEntity() {
                    init {
                        for ((key, value) in localVariables) {
                            fields.add(HCLTextField(key, false, this, value))
                        }
                    }
                }
            }
        }

        dumpGeneratedFiles(infra)
    }

    private fun dumpGeneratedFiles(infra: TFFile) {
        infra.writeToDirectory(myGenDirectory)
        for (file in myTerraformAdditional) {
            FileUtils.copyFile(file, File(myGenDirectory, file.name))
        }
    }
}
