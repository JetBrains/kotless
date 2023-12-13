package io.kotless

import io.kotless.resource.Lambda
import io.kotless.resource.StaticResource
import io.kotless.utils.TypedStorage
import io.kotless.utils.Visitable


/**
 * Definition of Kotless application.
 *
 * It may include few Web applications with
 * routes served from static and via lambdas.
 *
 * @param config configuration of kotless itself
 * @param application application defined by project
 * @param lambdas lambdas used in application
 * @param statics static resources used in application
 */
@OptIn(InternalAPI::class)
data class Schema(
    val config: KotlessConfig, val application: Application, val lambdas: TypedStorage<Lambda>, val statics: TypedStorage<StaticResource>
) : Visitable {

    override fun visit(visitor: (Any) -> Unit) {
        config.visit(visitor)
        lambdas.all.forEach { it.visit(visitor) }
        statics.all.forEach { it.visit(visitor) }
        application.visit(visitor)

        visitor(this)
    }
}
