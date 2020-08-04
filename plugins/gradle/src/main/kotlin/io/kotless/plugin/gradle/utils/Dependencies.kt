package io.kotless.plugin.gradle.utils

import io.kotless.DSLType
import io.kotless.plugin.gradle.dsl.kotless
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import java.io.File

internal object Dependencies {
    fun dsl(project: Project): Map<DSLType, Dependency> {
        val hasDSL = DSLType.values().filter { hasDependency(project, it) }
        return hasDSL.map { it to getDependency(project, it)!! }.toMap()
    }

    fun hasDependency(project: Project, type: DSLType) = getDependency(project, type) != null
    fun getDependency(project: Project, type: DSLType) = getDependency(project, type.descriptor.apiLibrary)

    fun getDependencies(project: Project): Set<File> {
        return getConfiguration(project).files.toSet()
    }

    private fun getConfiguration(project: Project): Configuration {
        with(project) {
            return configurations.getByName(kotless.config.configurationName)
        }
    }

    private fun getDependency(project: Project, name: String): Dependency? {
        val depsConfiguration = getConfiguration(project)
        val deps = depsConfiguration.allDependencies

        return deps.find { it.group == "io.kotless" && it.name == name }
    }
}
