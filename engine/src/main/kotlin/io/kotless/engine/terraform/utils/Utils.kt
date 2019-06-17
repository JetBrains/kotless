package io.kotless.engine.terraform.utils

import io.kotless.engine.terraform.TfEntity


/**
 * Make from a name of tf field value
 *
 * Also will add entity to `uses` field of receiver
 */
fun TfEntity.tf(str: String): String {
    tryRun { TfEntity[TfEntity.TfDescriptor(str)] }?.let { uses.add(it) }
    return "\"\${$str}\""
}

typealias TfFieldValue = TfEntity.() -> String

/**
 * Make from a name of tf field raw value
 *
 * Also will add entity to `uses` field of receiver
 */
fun TfEntity.tfRaw(str: String): String {
    tryRun { TfEntity[TfEntity.TfDescriptor(str)] }?.let { uses.add(it) }
    return "\${$str}"
}

private fun <T> tryRun(body: () -> T): T? = try {
    body()
} catch (e: Exception) {
    null
}

/** Poll first element from mutable list, if it exists. Otherwise, null */
fun <T> MutableList<T>.poll(): T? = if (this.size > 0) this.removeAt(0) else null

private val camelCaseRegex = Regex("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")

fun List<String>.toTfName() = this.joinToString(separator = "_").toTfName()

fun String.toTfName(vararg parts: String) = (this.split(Regex("-|\\s|\\."))
        .flatMap { it.split(camelCaseRegex) } + parts).joinToString(separator = "_") { it.toLowerCase().replace("/", "") }

fun String.toAwsName(vararg parts: String) = (this.split(Regex("_|\\s|\\."))
        .flatMap { it.split(camelCaseRegex) } + parts).joinToString(separator = "-") { it.toLowerCase().replace("/", "") }.prependWithResourcePrefix()

private fun String.prependWithResourcePrefix() = if (!startsWith(TfEntity.resourcePrefix)) {
    TfEntity.resourcePrefix + this
} else {
    this
}

fun String.filterBlankLines() = this.split("\n").filter { it.isNotBlank() }.joinToString(separator = "\n")
