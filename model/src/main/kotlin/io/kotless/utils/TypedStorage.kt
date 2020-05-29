package io.kotless.utils

import io.kotless.InternalAPI

/** Interface for type-full singleton storage */
@InternalAPI
class TypedStorage<T>(private val storage: MutableMap<Key<T>, T> = HashMap()) {
    class Key<T>

    val all: Collection<T>
        get() = storage.values

    val entries: Set<Map.Entry<Key<T>, T>>
        get() = storage.entries

    operator fun <K : Key<T>> set(key: K, value: T) {
        storage[key] = value
    }

    operator fun <K : Key<T>> get(key: K): T? {
        return storage[key]
    }

    fun <K : Key<T>> getOrPut(key: K, defaultValue: () -> T): T {
        return storage.getOrPut(key, defaultValue)
    }

    fun addAll(storage: TypedStorage<T>) {
        this.storage.putAll(storage.storage)
    }

    override fun toString(): String {
        return storage.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}: ${it.value}" }
    }
}
