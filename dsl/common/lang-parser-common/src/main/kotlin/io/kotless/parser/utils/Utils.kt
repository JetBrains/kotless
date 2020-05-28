package io.kotless.parser.utils

fun <T> Sequence<T>.reversed() = this.toList().asReversed().asSequence()
