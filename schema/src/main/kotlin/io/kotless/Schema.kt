package io.kotless


/**
 * Definition of Kotless application.
 *
 * It may include few Web applications with
 * routes served from static and via lambdas.
 *
 * @param config configuration of kotless itself
 * @param webapps web applications defined by application
 * @param lambdas lambdas used in application
 * @param statics static resources used in application
 */
data class Schema(val config: KotlessConfig, val webapps: Set<Webapp>,
                  val lambdas: TypedStorage<Lambda>, val statics: TypedStorage<StaticResource>) : Visitable {
    override fun visit(visitor: (Any) -> Unit) {
        config.visit(visitor)
        lambdas.all.forEach { it.visit(visitor) }
        statics.all.forEach { it.visit(visitor) }
        webapps.forEach { it.visit(visitor) }

        visitor(this)
    }
}
