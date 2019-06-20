package io.kotless.plugin.gradle

import io.kotless.plugin.gradle.tasks.*
import io.kotless.plugin.gradle.utils._create
import io.kotless.plugin.gradle.utils.applyPluginSafely
import org.gradle.api.Plugin
import org.gradle.api.Project

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

            with(tasks) {
                val shadowJar = getByName("shadowJar")

                val generate = _create("generate", KotlessGenerate::class)
                val download = _create("download_terraform", TerraformDownload::class)

                val init = _create("initialize", TerraformOperation::class) {
                    dependsOn(download, generate, shadowJar)

                    operation = TerraformOperation.Operation.INIT
                }
                _create("plan", TerraformOperation::class) {
                    dependsOn(init)

                    operation = TerraformOperation.Operation.PLAN
                }

                _create("deploy", TerraformOperation::class) {
                    dependsOn(init)

                    operation = TerraformOperation.Operation.APPLY
                }
            }
        }
    }
}
