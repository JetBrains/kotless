package io.kotless.plugin.gradle

import io.kotless.plugin.gradle.KotlessDeployTasks.setupDeployTasks
import io.kotless.plugin.gradle.KotlessLocalTasks.setupLocalTasks
import io.kotless.plugin.gradle.KotlessRuntimeTasks.setupGraal
import io.kotless.plugin.gradle.dsl.*
import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.tasks.terraform.TerraformDownloadTask
import io.kotless.plugin.gradle.utils.gradle.*
import io.kotless.resource.Lambda
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
@Suppress("unused")
internal class KotlessPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            applyPluginSafely("com.github.johnrengelman.shadow")
            applyPluginSafely("application")

            configurations.create(myLocalConfigurationName)

            kotless = KotlessDSL(this)

            with(tasks) {
                val download = myCreate<TerraformDownloadTask>("download_terraform")

                afterEvaluate {
                    if (kotless.webapp.lambda.runtime == Lambda.Config.Runtime.GraalVM) {
                        setupGraal()
                    } else {
                        convention.getPlugin<ApplicationPluginConvention>().mainClassName = kotless.config.dsl.typeOrDefault.descriptor.localEntryPoint
                    }

                    setupDeployTasks(download)
                    setupLocalTasks(download)
                }
            }
        }
    }
}
