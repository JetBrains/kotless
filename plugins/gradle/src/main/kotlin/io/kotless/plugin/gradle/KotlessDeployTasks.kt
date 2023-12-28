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
    fun Project.setupDeployTasks(download: Task) {
//        if (kotless.config.bucket.isEmpty()) {
//            logger.warn("Configuration succeeded, but Kotless requires `kotless { config { bucket = \"...\" } }` for actual deployment")
//            logger.warn("Deployment tasks will NOT be added to this project")
//            return
//        }

        with(tasks) {
            val generate = myCreate<KotlessGenerateTask>("generate")

            val init = myCreate<TerraformOperationTask>("initialize") {
                group = Groups.`kotless setup`

                dependsOn(download, generate, kotless.config.myArchiveTask)

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

            val initializeExistingBuild = myCreate<TerraformOperationTask>("initializeExistingBuild") {
                group = Groups.`kotless setup`

                dependsOn(download, generate)

                root = kotless.config.deployGenDirectory

                operation = TerraformOperationTask.Operation.INIT
            }

            myCreate<TerraformOperationTask>("deployExistingBuild") {
                group = Groups.kotless

                dependsOn(initializeExistingBuild)

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
