package io.kotless

/** Defines entity that can be walked by visitor */
interface Visitable {
    fun visit(visitor: (Any) -> Unit) {
        visitor(this)
    }
}
