package io.kotless.gen

import io.kotless.Schema
import io.kotless.hcl.HCLNamed

data class KotlessGenerationContext(val schema: Schema, val outputs: HashMap<Any, Any>, val entities: HashSet<HCLNamed>) {
    fun registerOutput(kotlessEntity: Any, factoryOutput: Any) {
        outputs[kotlessEntity] = factoryOutput
    }

    fun registerEntities(entities: Iterable<HCLNamed>) {
        this.entities.addAll(entities)
    }

    fun registerEntities(vararg entities: HCLNamed) {
        registerEntities(entities.toList())
    }

    inline fun <reified Input : Any, reified Output : Any,
            reified Factory : KotlessFactory<Input, Output>> get(input: Input, factory: Factory): Output {
        if (!outputs.containsKey(input)) factory.get(input, this)

        return outputs[input] as Output
    }
}

interface KotlessFactory<Input : Any, Output : Any> {
    fun get(entity: Input, context: KotlessGenerationContext)
}

