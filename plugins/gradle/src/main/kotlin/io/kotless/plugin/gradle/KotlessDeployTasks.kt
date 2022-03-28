package io.kotless.plugin.gradle

import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.tasks.gen.KotlessGenerateTask
import io.kotless.plugin.gradle.tasks.terraform.TerraformOperationTask
import io.kotless.plugin.gradle.utils.gradle.Groups
import io.kotless.plugin.gradle.utils.gradle.myCreate
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Utils to setup deploy tasks for all DSLs
 */
internal object KotlessDeployTasks {
    private fun taskNameFromDirectory(name: String): String =
        name.split("-").joinToString(separator = "") { it.capitalize() }.decapitalize()

    fun Project.setupDeployTasks(download: Task) {
//        if (kotless.config.bucket.isEmpty()) {
//            logger.warn("Configuration succeeded, but Kotless requires `kotless { config { bucket = \"...\" } }` for actual deployment")
//            logger.warn("Deployment tasks will NOT be added to this project")
//            return
//        }

        with(tasks) {
            val generate = myCreate<KotlessGenerateTask>("generate")

            val directories = listOf(kotless.config.preDeployGenDirectory, kotless.config.deployGenDirectory, kotless.config.postDeployGenDirectory)

            val initTasks = directories.map { directory ->
                myCreate<TerraformOperationTask>(taskNameFromDirectory("${directory.name}Initialize")) {
                    group = Groups.`kotless setup`

                    dependsOn(download, generate, kotless.config.myArchiveTask)

                    root = directory

                    operation = TerraformOperationTask.Operation.INIT
                }
            }
            val initTask = myCreate<Task>("init") {
                group = Groups.kotless
                dependsOn(*initTasks.toTypedArray())
            }

            val planTasks = directories.map { directory ->
                myCreate<TerraformOperationTask>(taskNameFromDirectory("${directory.name}Plan")) {
                    group = Groups.kotlessSteps

                    dependsOn(*initTasks.toTypedArray())

                    root = directory

                    operation = TerraformOperationTask.Operation.PLAN
                }
            }
            val planTask = myCreate<Task>("plan") {
                group = Groups.kotless
                dependsOn(*planTasks.toTypedArray())
            }


            val deployTasks = directories.map { directory ->
                myCreate<TerraformOperationTask>(taskNameFromDirectory("${directory.name}Deploy")) {
                    group = Groups.kotlessSteps

                    dependsOn(*initTasks.toTypedArray())

                    root = directory

                    operation = TerraformOperationTask.Operation.APPLY
                }
            }
            val deployTask = myCreate<Task>("deploy") {
                group = Groups.kotless
                dependsOn(*deployTasks.toTypedArray())
            }

            if (kotless.extensions.terraform.allowDestroy) {
                val destroyTasks = directories.map { directory ->
                    myCreate<TerraformOperationTask>(taskNameFromDirectory("${directory.name}Destroy")) {
                        group = Groups.kotlessSteps

                        dependsOn(*initTasks.toTypedArray())

                        root = directory

                        operation = TerraformOperationTask.Operation.DESTROY
                    }
                }
                val destroyTask = myCreate<Task>("destroy") {
                    group = Groups.kotless
                    dependsOn(*destroyTasks.reversed().toTypedArray())
                }
            }
        }
    }
}
