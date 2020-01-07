package io.kotless.plugin.gradle.utils

import io.kotless.plugin.gradle.dsl.kotless
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import java.io.File

object Dependencies {
    fun getKotlessDependency(project: Project) = getDependency(project, group = "io.kotless", name = "lang")

    fun getKtorDependency(project: Project) = getDependency(project, group = "io.kotless", name = "ktor-lang")

    fun getDependencies(project: Project): Set<File> {
        return getConfiguration(project).files.toSet()
    }

    fun getConfiguration(project: Project): Configuration {
        with(project) {
            return configurations.getByName(kotless.config.configurationName)
        }
    }

    fun getDependency(project: Project, group: String, name: String): Dependency? {
        val depsConfiguration = getConfiguration(project)
        val deps = depsConfiguration.allDependencies

        return deps.find { it.group == group && it.name == name }
    }
}
