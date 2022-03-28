package io.kotless.gen

import io.terraformkt.hcl.HCLEntity

/**
 * Interface of factory generating Terraform from schema
 *
 * Factories are called in cycle and provided with updated generation context
 */
interface GenerationFactory<Input : Any, Output : Any> {
    data class GenerationResult<Output : Any>(val output: Output, val entities: Set<HCLEntity.Named>) {
        constructor(output: Output, vararg entities: HCLEntity.Named) : this(output, entities.toSet())
    }

    fun destination(entity: Input, context: GenerationContext): String = "environment"

    fun hasRan(entity: Input, context: GenerationContext): Boolean = context.output.check(entity, this)

    fun mayRun(entity: Input, context: GenerationContext): Boolean

    fun generate(entity: Input, context: GenerationContext): GenerationResult<Output>

    fun run(entity: Input, context: GenerationContext) = (destination(entity, context) to generate(entity, context)).also {
        context.output.register(this, entity, it.second.output)
        context.entities.register(it.second.entities.map { entity -> GenerationContext.Entities.Entity(it.first, entity) })
    }
}

