package io.kotless.opt

import io.kotless.KotlessConfig
import io.kotless.Schema

interface SchemaOptimizer {
    fun optimize(schema: Schema, optimization: KotlessConfig.Optimization): Schema
}
