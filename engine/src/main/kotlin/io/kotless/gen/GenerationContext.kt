package io.kotless.gen

import io.kotless.*
import io.kotless.hcl.HCLEntity

/**
 * Context of current Terraform generation
 *
 * It is populated by outputs of [GenerationFactory] and resources created by them as well
 */
class GenerationContext(val schema: Schema, val webapp: Webapp) {

    val output = Output()

    inner class Output {
        private val outputs: HashMap<Pair<GenerationFactory<*, *>, *>, Any> = HashMap()

        fun <Input : Any, Output : Any, Factory : GenerationFactory<Input, Output>> register(factory: Factory, input: Input, output: Output) {
            outputs[factory to input] = output
        }

        fun <Input : Any, Output : Any, Factory : GenerationFactory<Input, Output>> check(input: Input, factory: Factory): Boolean {
            return (factory to input) in outputs
        }

        fun <Input : Any, Output : Any, Factory : GenerationFactory<Input, Output>> get(input: Input, factory: Factory): Output {
            if ((factory to input) !in outputs) factory.generate(input, this@GenerationContext)

            @Suppress("UNCHECKED_CAST")
            return outputs[factory to input] as Output
        }
    }

    val entities = Entities()

    class Entities {
        private val entities: HashSet<HCLEntity> = HashSet()

        fun register(entities: Iterable<HCLEntity>) {
            this.entities.addAll(entities)
        }

        fun register(vararg entities: HCLEntity) {
            register(entities.toList())
        }

        fun all() = entities.toSet()
    }

    class Storage {
        private val storage = HashMap<Key<*>, Any>()

        class Key<T>()

        fun <E: Any, K: Key<E>> register(key: K, value: E) {
            storage[key] = value
        }

        @Suppress("UNCHECKED_CAST")
        operator fun <E: Any, K: Key<E>> get(key: K): E? {
            return storage[key] as E?
        }

        @Suppress("UNCHECKED_CAST")
        fun <E: Any, K: Key<E>> getOrPut(key: K, defaultValue: () -> E): E {
            return storage.getOrPut(key, defaultValue) as E
        }
    }

    val storage = Storage()
}
