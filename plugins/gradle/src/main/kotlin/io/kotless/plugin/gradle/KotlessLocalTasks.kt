package io.kotless.plugin.gradle

import io.kotless.*
import io.kotless.plugin.gradle.dsl.KotlessDSL
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.tasks.gen.KotlessLocalGenerateTask
import io.kotless.plugin.gradle.tasks.local.KotlessLocalRunTask
import io.kotless.plugin.gradle.tasks.local.LocalStackRunner
import io.kotless.plugin.gradle.tasks.terraform.TerraformOperationTask
import io.kotless.plugin.gradle.utils.AWSUtils
import io.kotless.plugin.gradle.utils.gradle.Groups
import io.kotless.plugin.gradle.utils.gradle.myCreate
import io.kotless.resource.Lambda
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import java.io.File

/**
 * Utils to setup local tasks for all DSLs
 */
internal object KotlessLocalTasks {
    @OptIn(InternalAPI::class)
    fun Project.setupLocalTasks(download: Task) {
        with(tasks) {
            val local = LocalStackRunner(kotless.extensions.local.useAWSEmulation, AwsResource.forLocalStart)

            if (kotless.extensions.local.useAWSEmulation) {
                setupLocalWithAWSEmulation(kotless, buildDir, local, download)
            } else {
                setupLocal(kotless, buildDir, local)
            }
        }
    }

    private fun TaskContainer.setupLocal(kotless: KotlessDSL, buildDir: File, local: LocalStackRunner) {
        val classes = getByName("classes")

        myCreate<KotlessLocalRunTask>("local") {
            localstack = local
            dependsOn(classes)
        }

        val graalAgent = graalAgent(kotless, buildDir)

        if(graalAgent != null) {
            myCreate<KotlessLocalRunTask>("local-agent") {
                localstack = local
                customAgent = graalAgent

                if (kotless.config.dsl.typeOrDefault == DSLType.SpringBoot) {
                    additionalEnvironment = mapOf("spring.aot.enabled" to "true")
                }

                dependsOn(classes)
            }
        }
    }

    private fun Project.setupLocalWithAWSEmulation(kotless: KotlessDSL, buildDir: File, local: LocalStackRunner, download: Task) {
        with(tasks) {
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
            }.onShutDown({
                stopLocalStack.act()
            })

            val graalAgent = graalAgent(kotless, buildDir)

            if(graalAgent != null) {
                myCreate<KotlessLocalRunTask>("local-agent") {
                    localstack = local
                    customAgent = graalAgent

                    if (kotless.config.dsl.typeOrDefault == DSLType.SpringBoot) {
                        additionalEnvironment = mapOf("spring.aot.enabled" to "true")
                    }

                    dependsOn(classes, configure)
                }.onShutDown({
                    stopLocalStack.act()
                })
            }
        }
    }

    private fun graalAgent(kotless: KotlessDSL, buildDir: File): String? {
        if (kotless.webapp.lambda.runtime == Lambda.Config.Runtime.GraalVM) {
            val graalAgentDir = File(buildDir, "graal-agent")
            graalAgentDir.deleteRecursively()
            return "-agentlib:native-image-agent=config-output-dir=${graalAgentDir.absolutePath}"
        }

        return null
    }
}
