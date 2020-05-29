package io.kotless

/** Representation of URI path. Removes blank parts. */
data class URIPath(var parts: Iterable<String>) {
    init {
        parts = parts.flatMap { it.split("/") }.map { it.trim() }.filter { it.isNotBlank() }
    }

    constructor() : this(emptyList())
    constructor(path: URIPath, part: String) : this(path.parts + part)
    constructor(path: URIPath, other: URIPath) : this(path.parts + other.parts)
    constructor(path: Sequence<URIPath>) : this(path.toList().flatMap { it.parts })
    constructor(part: String, path: URIPath) : this(listOf(part) + path.parts)
    constructor(path: String) : this(path.split("/"))

    override fun toString() = parts.joinToString(separator = "/")
    fun toAbsoluteString() = parts.joinToString(prefix = "/", separator = "/")
}

/** Convert string representation of URI path to [URIPath] */
fun String.toURIPath() = URIPath(this.split("/"))
