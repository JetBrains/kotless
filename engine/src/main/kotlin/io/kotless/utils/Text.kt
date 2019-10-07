package io.kotless.utils

object Text {
    private val camelCaseRegex = Regex("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")

    fun decamelize(text: String): List<String> = text.split(camelCaseRegex)
    fun desnake(text: String): List<String> = text.split("_")
    fun dehyphen(text: String): List<String> = text.split("-")
    fun dedot(text: String): List<String> = text.split(".")

    const val indent = 2

    fun indent(number: Int = indent) = " ".repeat(number)
}

fun String.withIndent(number: Int = Text.indent) = prependIndent(Text.indent(number))
