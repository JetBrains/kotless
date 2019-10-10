package io.kotless.utils

/** Defines entity that can be walked by visitor */
interface Visitable {
    fun visit(visitor: (Any) -> Unit) {
        visitor(this)
    }
}
