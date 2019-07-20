package io.kotless.gen

interface KotlessFactory<Input : Any, Output : Any> {
    fun mayRun(context: KotlessGenerationContext): Boolean = true

    fun get(entity: Input, context: KotlessGenerationContext)
}

