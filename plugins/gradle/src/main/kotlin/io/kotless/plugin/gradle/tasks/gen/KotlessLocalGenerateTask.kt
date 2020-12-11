package io.kotless.plugin.gradle.tasks.gen

import io.kotless.AwsResource
import io.kotless.plugin.gradle.dsl.KotlessDSL
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.utils.gradle.Groups
import io.kotless.plugin.gradle.utils.gradle.clearDirectory
import io.terraformkt.aws.provider.Provider
import io.terraformkt.aws.provider.provider
import io.terraformkt.terraform.TFFile
import io.terraformkt.terraform.terraform
import io.terraformkt.terraform.tf
import org.codehaus.plexus.util.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

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

            provider {
                region = "us-east-1"
                version = myKotless.config.terraform.provider.version

                skip_credentials_validation = true
                skip_metadata_api_check = true
                skip_requesting_account_id = true

                endpoints {
                    val resultedMap = services.mapKeys { it.key.prefix }
                    resultedMap.forEach { (k, v) ->
                        Provider.Endpoints::class.memberProperties.filter {
                            it.name == k
                        }.forEach { (it as KMutableProperty<*>).setter.call(this, v) }
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
