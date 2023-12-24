package io.kotless.plugin.gradle.graal.dsl

import io.kotless.plugin.gradle.graal.utils.myExt
import org.gradle.api.Project


internal var Project.runtime: RuntimePluginExtension
    get() = this.myExt("runtime")
    set(value) {
        this.myExt["runtime"] = value
    }

@RuntimeDSLTag
fun Project.runtime(configure: RuntimePluginExtension.() -> Unit) {
    runtime = RuntimePluginExtension().apply(configure)
}
