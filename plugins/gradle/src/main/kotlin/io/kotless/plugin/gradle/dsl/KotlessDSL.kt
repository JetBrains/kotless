package io.kotless.plugin.gradle.dsl

import org.gradle.api.Project
import java.io.Serializable

/** Kotless DSL root */
@KotlessDSLTag
class KotlessDSL(project: Project) : Serializable {
    internal var config: KotlessConfig = KotlessConfig(project)
    /** Declaration of Kotless configuration itself */
    @KotlessDSLTag
    fun config(configure: KotlessConfig.() -> Unit) {
        config = config.apply(configure)
    }

    lateinit var webapp: Webapp
    /** Configuration of Kotless Web application */
    @KotlessDSLTag
    fun Project.webapp(configure: Webapp.() -> Unit) {
        webapp = Webapp(this).apply(configure)
    }
}
