package io.kotless.utils

object Text {
    private val camelCaseRegex = Regex("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")

    fun decamelize(text: String): List<String> = text.split(camelCaseRegex)
    fun desnake(text: String): List<String> = text.split("_")
    fun dehyphen(text: String): List<String> = text.split("-")
    fun dedot(text: String): List<String> = text.split(".")

    fun deall(text: String) = decamelize(text)
        .flatMap { desnake(it) }
        .flatMap { dehyphen(it) }
        .flatMap { dedot(it) }
        .filter { it.isNotBlank() }

    const val indent = 2

    fun indent(number: Int = indent) = " ".repeat(number)
}

fun String.withIndent(number: Int = Text.indent) = prependIndent(Text.indent(number))

fun String.plusIterable(value: Iterable<String>) = listOf(this) + value.toList()
