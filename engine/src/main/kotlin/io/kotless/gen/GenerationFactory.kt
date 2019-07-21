package io.kotless.gen

import io.kotless.Webapp
import io.kotless.hcl.HCLNamed

interface GenerationFactory<Input : Any, Output : Any> {
    data class GenerationResult<Output: Any>(val output: Output, val entities: Set<HCLNamed>) {
        constructor(output: Output, vararg entities: HCLNamed): this(output, entities.toSet())
    }

    fun mayRun(entity: Input, context: GenerationContext): Boolean

    fun generate(entity: Input, context: GenerationContext): GenerationResult<Output>

    fun run(entity: Input, context: GenerationContext) = generate(entity, context).also {
        context.registerOutput(this, entity, it.output)
        context.registerEntities(it.entities)
    }
}

