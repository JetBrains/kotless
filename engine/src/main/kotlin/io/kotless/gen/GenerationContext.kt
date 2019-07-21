package io.kotless.gen

import io.kotless.Schema
import io.kotless.Webapp
import io.kotless.hcl.HCLNamed


data class GenerationContext(val schema: Schema, val webapp: Webapp,
                             private val outputs: HashMap<Pair<GenerationFactory<*, *>, Any>, Any> = HashMap(),
                             val entities: HashSet<HCLNamed> = HashSet()) {
    fun <Input : Any, Output : Any, Factory : GenerationFactory<Input, Output>> registerOutput(factory: Factory, input: Input, output: Output) {
        outputs[factory to input] = output
    }

    fun registerEntities(entities: Iterable<HCLNamed>) {
        this.entities.addAll(entities)
    }

    fun registerEntities(vararg entities: HCLNamed) {
        registerEntities(entities.toList())
    }

    fun <Input : Any, Output : Any, Factory : GenerationFactory<Input, Output>> check(input: Input, factory: Factory): Boolean {
        return outputs.containsKey(factory to input)
    }

    fun <Input : Any, Output : Any, Factory : GenerationFactory<Input, Output>> get(input: Input, factory: Factory): Output {
        if (!outputs.containsKey(factory to input)) factory.generate(input, this)

        @Suppress("UNCHECKED_CAST")
        return outputs[factory to input] as Output
    }

    inline fun <reified T : HCLNamed> find(filter: (T) -> Boolean) = entities.mapNotNull { it as? T }.filter(filter)
}
