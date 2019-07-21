package io.kotless

interface Visitable {
    fun visit(visitor: (Any) -> Unit) {
        visitor(this)
    }
}
