package io.kotless.utils

fun <T> Collection<T>.forEachWithEnd(body: (T, Boolean) -> Unit) {
    for ((ind, value) in withIndex()) {
        body(value, ind == size - 1)
    }
}
