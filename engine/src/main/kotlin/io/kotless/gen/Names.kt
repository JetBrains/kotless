package io.kotless.gen

import io.kotless.utils.Text

object Names {
    private fun deall(text: String) = Text.decamelize(text)
        .flatMap { Text.desnake(it) }
        .flatMap { Text.dehyphen(it) }
        .flatMap { Text.dedot(it) }
        .filter { it.isNotBlank() }

    fun tf(vararg name: String) = tf(name.toList())
    fun tf(part: String, parts: Iterable<String>) = tf(part + parts.toList())
    fun tf(name: Iterable<String>) = name.flatMap { deall(it) }.joinToString(separator = "_") { it.toLowerCase() }

    fun aws(vararg name: String) = aws(name.toList())
    fun aws(name: Iterable<String>) = name.flatMap { deall(it) }.joinToString(separator = "-") { it.toLowerCase() }
}
