package io.kotless.gen

import io.kotless.utils.Text

object Names {
    private fun deall(text: String) = Text.decamelize(text).flatMap { Text.desnake(it) }.flatMap { Text.dehyphen(it) }.flatMap { Text.dedot(it) }.filter { it.isNotBlank() }


    fun tf(vararg name: String) = tf(name.toList())
    fun tf(name: Iterable<String>) = name.flatMap { deall(it) }.map { it.toLowerCase() }.joinToString(separator = "_")

    fun aws(vararg name: String) = aws(name.toList())
    fun aws(name: Iterable<String>) = name.flatMap { deall(it) }.map { it.toLowerCase() }.joinToString(separator = "-")
}
