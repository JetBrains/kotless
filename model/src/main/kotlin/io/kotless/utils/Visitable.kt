package io.kotless.utils

import io.kotless.InternalAPI

/** Defines entity that can be walked by visitor */
@InternalAPI
interface Visitable {
    fun visit(visitor: (Any) -> Unit) {
        visitor(this)
    }
}
