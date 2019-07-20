package io.kotless.gen

import io.kotless.utils.Text

object Names {
    fun tf(vararg name: String) =  tf(name.toList())
    fun tf(name: Iterable<String>) = name.flatMap { Text.decamelize(it) }.flatMap { it.split("-") }.joinToString(separator = "_")
    fun aws(vararg name: String) = name.flatMap { Text.decamelize(it) }.flatMap { it.split("_") }.joinToString(separator = "-")
}
