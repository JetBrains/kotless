package io.kotless.opt

import io.kotless.KotlessConfig
import io.kotless.Schema

/** Represents implementation of some Kotless Schema optimization login */
interface SchemaOptimizer {
    fun optimize(schema: Schema, optimization: KotlessConfig.Optimization, context: OptimizationContext): Schema
}
