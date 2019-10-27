package io.kotless.plugin.gradle.utils

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.accessors.runtime.addExternalModuleDependencyTo
import org.gradle.kotlin.dsl.getByName
import java.io.File
import kotlin.reflect.KClass

//Generated accessors to use in a plugin
internal inline fun <reified T : Any> Project.extByName(name: String): T = extensions.getByName<T>(name)

internal inline fun <reified T : Any> Project.ext(name: String) = myExt[name] as T

internal val Project.myExt: ExtraPropertiesExtension
    get() = extByName("ext")

internal val Project.mySourceSets: SourceSetContainer
    get() = extByName("sourceSets")

internal val Project.myKtSourceSet: Set<File>
    get() = mySourceSets.asMap["main"]!!.allSource.files.filter { it.extension == "kt" }.toSet()

internal fun <T : Task> TaskContainer.myCreate(name: String, klass: KClass<T>, configure: T.() -> Unit = {}): Task = create(name, klass.java) {
    configure(it)
}

internal fun Project.myShadowJar(name: String = "shadowJar"): ShadowJar = tasks.getByName(name) as ShadowJar

const val localConfigurationName = "kotless-local"

internal fun Project._local() = this.configurations.getByName(localConfigurationName)

internal fun DependencyHandlerScope._local(group: String, name: String, version: String) {
    addExternalModuleDependencyTo(this, localConfigurationName, group, name, version, null, null, null, null)
}
