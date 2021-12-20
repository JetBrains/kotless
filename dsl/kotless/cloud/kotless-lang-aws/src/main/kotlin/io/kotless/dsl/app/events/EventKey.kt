package io.kotless.dsl.app.events

class EventKey(val key: String) {
    fun cover(other: EventKey): Boolean {
        val thisParts = key.split(":")
        val otherParts = other.key.split(":")

        if (thisParts.size != otherParts.size) return false

        return thisParts.zip(otherParts).all { (part, other) ->
            part == "*" || part == other
        }
    }
}
