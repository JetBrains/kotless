package io.kotless.plugin.gradle

import io.kotless.AwsResource
import io.kotless.DSLType
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.tasks.gen.KotlessLocalGenerateTask
import io.kotless.plugin.gradle.tasks.local.KotlessLocalRunTask
import io.kotless.plugin.gradle.tasks.local.LocalStackRunner
import io.kotless.plugin.gradle.tasks.terraform.TerraformOperationTask
import io.kotless.plugin.gradle.utils.AWSUtils
import io.kotless.plugin.gradle.utils.Groups
import io.kotless.plugin.gradle.utils.myCreate
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ApplicationPluginConvention
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.getPlugin

/**
 * Utils to setup local tasks for all DSLs
 */
internal object KotlessLocalTasks {
    fun Project.setupLocalTasks(download: Task) {
        with(tasks) {
            val local = LocalStackRunner(kotless.extensions.local.useAWSEmulation, AwsResource.forLocalStart)

            convention.getPlugin<ApplicationPluginConvention>().mainClassName = when (kotless.config.dsl.typeOrDefault) {
                DSLType.Kotless -> "io.kotless.local.MainKt"
                DSLType.Ktor -> "io.kotless.local.ktor.MainKt"
                DSLType.SpringBoot -> "io.kotless.local.spring.MainKt"
            }

            if (kotless.extensions.local.useAWSEmulation) {
                setupLocalWithAWSEmulation(local, download)
            } else {
                setupLocal(local)
            }
        }
    }

    private fun TaskContainer.setupLocal(local: LocalStackRunner) {
        val run = getByName("run")
        val classes = getByName("classes")

        myCreate<KotlessLocalRunTask>("local") {
            localstack = local

            dependsOn(classes)
        }.finalizedBy(run)
    }

    private fun Project.setupLocalWithAWSEmulation(local: LocalStackRunner, download: Task) {
        with(tasks) {
            val run = getByName("run")
            val classes = getByName("classes")

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

                root = project.kotless.config.localGenDirectory

                operation = TerraformOperationTask.Operation.INIT
            }

            val configure = myCreate<TerraformOperationTask>("local_configure") {
                group = Groups.`kotless setup`

                dependsOn(initialize)

                root = project.kotless.config.localGenDirectory

                environment = AWSUtils.fakeCredentials

                operation = TerraformOperationTask.Operation.APPLY
            }

            myCreate<KotlessLocalRunTask>("local") {
                localstack = local

                dependsOn(classes, configure)
            }.finalizedBy(run, stopLocalStack)
        }
    }
}
