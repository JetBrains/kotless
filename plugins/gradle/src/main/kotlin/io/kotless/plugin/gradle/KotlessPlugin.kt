package io.kotless.plugin.gradle

import io.kotless.AwsResource
import io.kotless.DSLType
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.tasks.gen.KotlessGenerateTask
import io.kotless.plugin.gradle.tasks.gen.KotlessLocalGenerateTask
import io.kotless.plugin.gradle.tasks.local.KotlessLocalRunTask
import io.kotless.plugin.gradle.tasks.local.LocalStackRunner
import io.kotless.plugin.gradle.tasks.terraform.TerraformDownloadTask
import io.kotless.plugin.gradle.tasks.terraform.TerraformOperationTask
import io.kotless.plugin.gradle.utils.*
import org.gradle.api.*
import org.gradle.api.plugins.ApplicationPluginConvention
import org.gradle.kotlin.dsl.getPlugin

/**
 * Implementation of Kotless plugin
 *
 * It defines tasks to generate and then deploy code written with Kotless.
 *
 * Note: Kotless is using own terraform binary that will be downloaded
 * with `download_terraform` task
 *
 * Also note: Plugin depends on shadowJar plugin and if it was not applied
 * already KotlessPlugin will apply it to project.
 */
class KotlessPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyPluginSafely("com.github.johnrengelman.shadow")
            applyPluginSafely("application")

            configurations.create(myLocalConfigurationName)

            with(tasks) {
                val download = myCreate<TerraformDownloadTask>("download_terraform")

                afterEvaluate {
                    setupDeployTasks(download)
                    setupLocalTasks(download)
                }
            }
        }
    }

    private fun Project.setupLocalTasks(download: Task) {
        with(tasks) {
            val run = tasks.getByName("run")
            val classes = tasks.getByName("classes")

            val local = LocalStackRunner(kotless.extensions.local.useAwsEmulation, AwsResource.forLocalStart)

            convention.getPlugin<ApplicationPluginConvention>().mainClassName = when (kotless.config.dsl.type) {
                DSLType.Kotless -> "io.kotless.local.MainKt"
                DSLType.Ktor -> "io.kotless.local.ktor.MainKt"
            }

            val startLocalStack = myCreate<LocalStackRunner.Start>("localstack_start") {
                localstack = local
            }
            val stopLocalStack = myCreate<LocalStackRunner.Stop>("localstack_stop") {
                localstack = local
            }

            val generate = myCreate<KotlessLocalGenerateTask>("local_generate") {
                dependsOn(startLocalStack)

                services = local.serviceMap
            }

            val initialize = myCreate<TerraformOperationTask>("local_initialize") {
                group = Groups.`kotless setup`

                dependsOn(download, generate)

                root = kotless.config.localGenDirectory

                operation = TerraformOperationTask.Operation.INIT
            }

            val configure = myCreate<TerraformOperationTask>("local_configure") {
                group = Groups.`kotless setup`

                dependsOn(initialize)

                root = kotless.config.localGenDirectory

                environment = AWSUtils.fakeCredentials

                operation = TerraformOperationTask.Operation.APPLY
            }

            myCreate<KotlessLocalRunTask>("local") {
                localstack = local

                dependsOn(classes, configure)
            }.finalizedBy(run, stopLocalStack)
        }
    }

    private fun Project.setupDeployTasks(download: Task) {
        with(tasks) {
            val generate = myCreate<KotlessGenerateTask>("generate")

            val init = myCreate<TerraformOperationTask>("initialize") {
                group = Groups.`kotless setup`

                dependsOn(download, generate, myShadowJar())

                root = kotless.config.deployGenDirectory

                operation = TerraformOperationTask.Operation.INIT
            }

            myCreate<TerraformOperationTask>("plan") {
                group = Groups.kotless

                dependsOn(init)

                root = kotless.config.deployGenDirectory

                operation = TerraformOperationTask.Operation.PLAN
            }

            myCreate<TerraformOperationTask>("deploy") {
                group = Groups.kotless

                dependsOn(init)

                root = kotless.config.deployGenDirectory

                operation = TerraformOperationTask.Operation.APPLY
            }

            if (kotless.extensions.terraform.allowDestroy) {
                myCreate<TerraformOperationTask>("destroy") {
                    group = Groups.kotless

                    dependsOn(init)

                    root = kotless.config.deployGenDirectory

                    operation = TerraformOperationTask.Operation.DESTROY
                }
            }
        }
    }
}
