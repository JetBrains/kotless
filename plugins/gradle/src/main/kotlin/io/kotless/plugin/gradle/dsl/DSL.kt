package io.kotless.plugin.gradle.dsl

import io.kotless.plugin.gradle.utils.myExt
import io.kotless.plugin.gradle.utils.ext
import org.gradle.api.Project

var Project.kotless: KotlessDSL
    get() = this.ext("kotless")
    set(value) {
        this.myExt["kotless"] = value
    }

/** Configuration of Kotless application */
@KotlessDSLTag
fun Project.kotless(configure: KotlessDSL.() -> Unit) {
    kotless = KotlessDSL(this).apply(configure)
}


