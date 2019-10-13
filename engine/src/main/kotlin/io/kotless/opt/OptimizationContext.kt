package io.kotless.opt

import io.kotless.Schema
import io.kotless.utils.Storage

/** Context of current Schema optimization */
class OptimizationContext(val schema: Schema) {
    val storage = Storage()
}
