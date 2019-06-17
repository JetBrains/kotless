package io.kotless

/** Representation of URI path. Removes blank parts. */
data class URIPath(var parts: List<String>) {
    init {
        parts = parts.filter { it.isNotBlank() }
    }

    constructor() : this(emptyList())
    constructor(path: URIPath, part: String) : this(path.parts + part)

    override fun toString() = parts.joinToString(separator = "/")
}

fun List<String>.toURIPath() = URIPath(this)
fun String.toURIPath() = URIPath(this.split("/"))
