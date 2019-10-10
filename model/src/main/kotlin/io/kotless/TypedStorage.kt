package io.kotless

/** Interface for typeful singleton storage */
class TypedStorage<T>(private val storage: MutableMap<Key<T>, T> = HashMap()) {

    class Key<T>

    val all: Collection<T>
        get() = storage.values

    val entries: Set<Map.Entry<Key<T>, T>>
        get() = storage.entries

    operator fun <K : Key<T>> set(key: K, value: T) {
        storage[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <K : Key<T>> get(key: K): T? {
        return storage[key]
    }

    @Suppress("UNCHECKED_CAST")
    fun <K : Key<T>> getOrPut(key: K, defaultValue: () -> T): T {
        return storage.getOrPut(key, defaultValue) as T
    }

    fun copy() = TypedStorage(HashMap(storage))

    fun addAll(storage: TypedStorage<T>) {
        this.storage.putAll(storage.storage)
    }
}
