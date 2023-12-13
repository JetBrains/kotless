package io.kotless.opt

import io.kotless.InternalAPI
import io.kotless.Schema
import io.kotless.utils.Storage

/** Context of current Schema optimization */
@OptIn(InternalAPI::class)
class OptimizationContext(val schema: Schema) {
    val storage = Storage()
}
