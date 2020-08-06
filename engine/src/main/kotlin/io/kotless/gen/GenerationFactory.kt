package io.kotless.gen

import io.kotless.hcl.HCLEntity

/**
 * Interface of factory generating Terraform from schema
 *
 * Factories are called in cycle and provided with updated generation context
 */
interface GenerationFactory<Input : Any, Output : Any> {
    data class GenerationResult<Output : Any>(val output: Output, val entities: Set<HCLEntity.Named>) {
        constructor(output: Output, vararg entities: HCLEntity.Named) : this(output, entities.toSet())
    }

    fun hasRan(entity: Input, context: GenerationContext): Boolean = context.output.check(entity, this)

    fun mayRun(entity: Input, context: GenerationContext): Boolean

    fun generate(entity: Input, context: GenerationContext): GenerationResult<Output>

    fun run(entity: Input, context: GenerationContext) = generate(entity, context).also {
        context.output.register(this, entity, it.output)
        context.entities.register(it.entities)
    }
}

