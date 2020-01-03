package io.kotless.plugin.gradle

import io.kotless.AwsResource
import io.kotless.DSLType
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.tasks.local.KotlessLocalTask
import io.kotless.plugin.gradle.tasks.gen.KotlessGenerateTask
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

                val init = myCreate("initialize", TerraformOperationTask::class) {
                    dependsOn(download, generate, shadowJar)

                    operation = TerraformOperationTask.Operation.INIT
                }

                myCreate("plan", TerraformOperationTask::class) {
                    dependsOn(init)

                    operation = TerraformOperationTask.Operation.PLAN
                }

                myCreate("deploy", TerraformOperationTask::class) {
                    dependsOn(init)

                    operation = TerraformOperationTask.Operation.APPLY
                }

                afterEvaluate {
                    if (kotless.extensions.terraform.allowDestroy) {
                        myCreate("destroy", TerraformOperationTask::class) {
                            dependsOn(init)

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

                        val startLocalStack = myCreate("localstack_start", LocalStackRunner.Start::class) {
                            localstack = localStackRunner
                        }
                        val stopLocalStack = myCreate("localstack_stop", LocalStackRunner.Stop::class) {
                            localstack = localStackRunner
                        }

                        myCreate("local", KotlessLocalTask::class) {
                            localstack = localStackRunner

                            dependsOn(tasks.getByName("classes"), startLocalStack)
                        }.finalizedBy("run", stopLocalStack)

                    }
                }
            }
        }
    }
}
