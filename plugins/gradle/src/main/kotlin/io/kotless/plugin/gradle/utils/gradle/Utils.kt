package io.kotless.plugin.gradle.utils.gradle

import org.gradle.api.Project
import java.io.File

/** Apply a plugin if it is not already applied. */
internal fun Project.applyPluginSafely(id: String) {
    pluginManager.apply(id)
}

internal fun File.clearDirectory() {
    require(exists().not() || isDirectory) { "Only directory can be cleared with `clearDirectory` call" }
    deleteRecursively()
    mkdirs()
}
