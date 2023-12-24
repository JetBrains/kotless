package io.kotless.plugin.gradle.graal.utils

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

internal inline fun <reified T : Any> Project.myExt(name: String) = myExt[name] as T

internal val Project.myExt: ExtraPropertiesExtension
    get() = myExtByName("ext")

internal inline fun <reified T : Any> Project.myExtByName(name: String): T = extensions.getByName(name) as T

internal val Project.mySourceSets: SourceSetContainer
    get() = myExtByName("sourceSets")

val SourceSet.sourceSet: SourceDirectorySet
    get() = extensions.getByType(SourceDirectorySet::class.java)

