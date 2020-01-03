package io.kotless.plugin.gradle.utils

import org.gradle.api.Project

/** Apply a plugin if it is not already applied. */
internal fun Project.applyPluginSafely(id: String) {
    pluginManager.apply(id)
}
