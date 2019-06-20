package io.kotless.utils

object Text {
    private val camelCaseRegex = Regex("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")

    fun decamelize(text: String): List<String> = text.split(camelCaseRegex)

    const val indent = 4
}

fun indent(number: Int) = " ".repeat(number)

