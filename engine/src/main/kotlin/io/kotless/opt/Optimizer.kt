package io.kotless.opt

import io.kotless.Schema

object Optimizer {
    private val optimizers = setOf(LambdaMergeOptimizer)

    fun optimize(schema: Schema): Schema {
        var optimized = schema
        val context = OptimizationContext()
        for (optimizer in optimizers) {
            optimized = optimizer.optimize(optimized, schema.config.optimization, context)
        }
        return optimized
    }
}
