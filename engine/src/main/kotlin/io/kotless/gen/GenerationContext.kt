package io.kotless.gen

import io.kotless.Application
import io.kotless.Schema
import io.kotless.utils.*
import io.terraformkt.hcl.HCLEntity

/**
 * Context of current Terraform generation
 *
 * It is populated by outputs of [GenerationFactory] and resources created by them as well
 */
class GenerationContext(val schema: Schema, val webapp: Application) {

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
        data class Entity(val name: String, val hclEntity: HCLEntity.Named)
        private val all: HashSet<Entity> = HashSet()

        fun register(entities: Iterable<Entity>) {
            this.all.addAll(entities)
        }

        fun register(vararg entities: Entity) {
            register(entities.toList())
        }

        fun all() = all.toSet()
    }

    inner class Names {
        fun tf(vararg name: String) = tf(name.toList())
        fun tf(part: String, parts: Iterable<String>) = tf(part.plusIterable(parts))
        fun tf(parts: Iterable<String>, part: String) = tf(parts.plus(part))
        fun tf(name: Iterable<String>) =
            name.flatMap { Text.deall(it) }.joinToString(separator = "_") { it.toLowerCase() }.replace(Regex("[*:.{}]"), "_")

        fun aws(vararg name: String) = aws(name.toList())
        fun azure(vararg name: String) = azure(name.toList())
        fun aws(part: String, parts: Iterable<String>) = aws(part.plusIterable(parts))
        fun aws(parts: Iterable<String>, part: String) = aws(parts.plus(part))
        fun aws(name: Iterable<String>): String {
            return (schema.config.cloud.prefix.plusIterable(name)).flatMap { Text.deall(it) }.joinToString(separator = "-") { it.toLowerCase() }
                .replace(Regex("[*:.{}]"), "_").takeLast(64)
        }

        fun azure(name: Iterable<String>): String {
            return (schema.config.cloud.prefix.plusIterable(name)).flatMap { Text.deall(it) }.joinToString(separator = "-") { it.toLowerCase() }
        }
    }

    val names = Names()

    val storage = Storage()
}
