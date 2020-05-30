package io.kotless.utils

import io.kotless.InternalAPI

/** Interface for type-full singleton storage */
@InternalAPI
class Storage {
    private val myStorage = HashMap<Key<*>, Any>()

    class Key<T>

    operator fun <E : Any, K : Key<E>> set(key: K, value: E) {
        myStorage[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <E : Any, K : Key<E>> get(key: K): E? {
        return myStorage[key] as E?
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : Any, K : Key<E>> getOrPut(key: K, defaultValue: () -> E): E {
        return myStorage.getOrPut(key, defaultValue) as E
    }
}
