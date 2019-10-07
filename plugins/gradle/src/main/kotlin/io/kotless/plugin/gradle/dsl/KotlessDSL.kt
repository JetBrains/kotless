package io.kotless.plugin.gradle.dsl

import org.gradle.api.Project
import java.io.*

/** Kotless DSL root */
@KotlessDSLTag
class KotlessDSL(project: Project) : Serializable {
    internal var config: KotlessConfig = KotlessConfig(project)
    /** Declaration of Kotless configuration itself */
    @KotlessDSLTag
    fun config(configure: KotlessConfig.() -> Unit) {
        config = config.apply(configure)
    }

    val webapps = ArrayList<Webapp>()
    /** Configuration of Kotless Web application */
    @KotlessDSLTag
    fun webapp(project: Project, configure: Webapp.() -> Unit) {
        webapps.add(Webapp(project).apply(configure))
    }
}
