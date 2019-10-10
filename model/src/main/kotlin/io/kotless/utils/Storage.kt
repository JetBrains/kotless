package io.kotless.utils

/** Interface for typeful singleton storage */
class Storage {
    private val storage = HashMap<Key<*>, Any>()

    class Key<T>

    operator fun <E : Any, K : Key<E>> set(key: K, value: E) {
        storage[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <E : Any, K : Key<E>> get(key: K): E? {
        return storage[key] as E?
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : Any, K : Key<E>> getOrPut(key: K, defaultValue: () -> E): E {
        return storage.getOrPut(key, defaultValue) as E
    }
}
