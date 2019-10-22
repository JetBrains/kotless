package io.kotless.parser.utils

fun <T> buildSet(body: HashSet<T>.() -> Unit): HashSet<T> {
    val set = HashSet<T>()
    body(set)
    return set
}
