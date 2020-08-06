package io.kotless.plugin.gradle.utils.gradle

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.accessors.runtime.addExternalModuleDependencyTo
import org.gradle.kotlin.dsl.getByName
import org.jetbrains.kotlin.incremental.isKotlinFile
import java.io.File

//Generated accessors to use in a plugin
internal inline fun <reified T : Any> Project.myExtByName(name: String): T = extensions.getByName<T>(name)

internal inline fun <reified T : Any> Project.myExt(name: String) = myExt[name] as T

internal val Project.myExt: ExtraPropertiesExtension
    get() = myExtByName("ext")

internal val Project.mySourceSets: SourceSetContainer
    get() = myExtByName("sourceSets")

internal val Project.myKtSourceSet: Set<File>
    get() = mySourceSets.asMap["main"]!!.allSource.files.filter { it.isKotlinFile(sourceFilesExtensions = listOf("kt")) }.toSet()

internal val Project.myResourcesSet: Set<File>
    get() = mySourceSets.asMap["main"]!!.resources.files.toSet()

internal inline fun <reified T : Task> TaskContainer.myCreate(name: String, crossinline configure: T.() -> Unit = {}): Task = create(name, T::class.java) {
    configure(it)
}

internal fun Project.myShadowJar(name: String = "shadowJar"): ShadowJar = tasks.myGetByName(name)

internal inline fun <reified T : Task> TaskContainer.myGetByName(name: String) = getByName(name) as T

internal const val myLocalConfigurationName = "kotless-local"

internal fun Project.myLocal() = this.configurations.getByName(myLocalConfigurationName)

internal fun DependencyHandlerScope.myLocal(group: String, name: String, version: String) {
    addExternalModuleDependencyTo(this, myLocalConfigurationName, group, name, version, null, null, null, null)
}
