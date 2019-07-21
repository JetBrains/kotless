package io.kotless.gen

import io.kotless.hcl.HCLEntity

interface GenerationFactory<Input : Any, Output : Any> {
    data class GenerationResult<Output : Any>(val output: Output, val entities: Set<HCLEntity>) {
        constructor(output: Output, vararg entities: HCLEntity) : this(output, entities.toSet())
    }

    fun hasRan(entity: Input, context: GenerationContext): Boolean = context.check(entity, this)

    fun mayRun(entity: Input, context: GenerationContext): Boolean

    fun generate(entity: Input, context: GenerationContext): GenerationResult<Output>

    fun run(entity: Input, context: GenerationContext) = generate(entity, context).also {
        context.registerOutput(this, entity, it.output)
        context.registerEntities(it.entities)
    }
}

