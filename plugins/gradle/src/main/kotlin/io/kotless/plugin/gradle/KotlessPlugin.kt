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
import org.gradle.api.Plugin
import org.gradle.api.Project
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

            with(tasks) {
                val shadowJar = getByName("shadowJar")

                val generate = myCreate("generate", KotlessGenerateTask::class)
                val download = myCreate("download_terraform", TerraformDownloadTask::class)

                afterEvaluate {
                    val init = myCreate("initialize", TerraformOperationTask::class) {
                        dependsOn(download, generate, shadowJar)

                        root = kotless.config.deployGenDirectory

                        operation = TerraformOperationTask.Operation.INIT
                    }

                    myCreate("plan", TerraformOperationTask::class) {
                        dependsOn(init)

                        root = kotless.config.deployGenDirectory

                        operation = TerraformOperationTask.Operation.PLAN
                    }

                    myCreate("deploy", TerraformOperationTask::class) {
                        dependsOn(init)

                        root = kotless.config.deployGenDirectory

                        operation = TerraformOperationTask.Operation.APPLY
                    }

                    if (kotless.extensions.terraform.allowDestroy) {
                        myCreate("destroy", TerraformOperationTask::class) {
                            dependsOn(init)

                            root = kotless.config.deployGenDirectory

                            operation = TerraformOperationTask.Operation.DESTROY
                        }
                    }

                    run {
                        val localStackRunner = LocalStackRunner(kotless.extensions.local.useAwsEmulation, setOf(AwsResource.S3, AwsResource.DynamoDB))



                        configurations.create(myLocalConfigurationName)

                        convention.getPlugin<ApplicationPluginConvention>().mainClassName = when (kotless.config.dsl.type) {
                            DSLType.Kotless -> "io.kotless.local.MainKt"
                            DSLType.Ktor -> "io.kotless.local.ktor.MainKt"
                        }

                        val startLocalStack = myCreate("local_start", LocalStackRunner.Start::class) {
                            localstack = localStackRunner
                        }
                        val stopLocalStack = myCreate("local_stop", LocalStackRunner.Stop::class) {
                            localstack = localStackRunner
                        }

                        val localGenerate = myCreate("local_generate", KotlessLocalGenerateTask::class) {
                            dependsOn( startLocalStack)

                            services = localStackRunner.serviceMap
                        }

                        val initLocal = myCreate("local_initialize", TerraformOperationTask::class) {
                            dependsOn(download, localGenerate)

                            root = kotless.config.localGenDirectory

                            operation = TerraformOperationTask.Operation.INIT
                        }

                        val applyLocal = myCreate("local_deploy", TerraformOperationTask::class) {
                            dependsOn(initLocal)

                            root = kotless.config.localGenDirectory

                            environment = mapOf(
                                "AWS_SECRET_ACCESS_KEY" to "secretkey",
                                "AWS_ACCESS_KEY_ID" to "accesskey"
                            )

                            operation = TerraformOperationTask.Operation.APPLY
                        }

                        myCreate("local", KotlessLocalRunTask::class) {
                            localstack = localStackRunner

                            dependsOn(tasks.getByName("classes"), applyLocal)
                        }.finalizedBy("run", stopLocalStack)

                    }
                }
            }
        }
    }
}
