package io.kotless.opt

import io.kotless.*
import io.kotless.KotlessConfig.Optimization

object LambdaMergeOptimizer : SchemaOptimizer {
    private val key = Storage.Key<Int>()
    private fun OptimizationContext.getIndexAndIncrement(): Int {
        val value = storage.getOrPut(key) { 0 }
        storage[key] = value + 1
        return value
    }

    /**
     * Merges the set of lambdas in accordance with `level`
     *
     * @param lambdas -- lambdas to be merged
     * @param level -- level of merge optimization
     * @return list of MergedLambda
     */
    fun merge(lambdas: Set<Lambda>, level: Optimization.MergeLambda, context: OptimizationContext): Map<Lambda, Lambda> = when (level) {
        Optimization.MergeLambda.None -> lambdas.map { it to it }.toMap()
        else -> {
            val grouped = when (level) {
                Optimization.MergeLambda.PerPermissions -> lambdas.groupBy { listOf(it.config, it.entrypoint, it.file, it.permissions) }
                Optimization.MergeLambda.All -> lambdas.groupBy { listOf(it.config, it.entrypoint, it.file) }
                Optimization.MergeLambda.None -> error("Merge mode could not be None, but was")
            }
            grouped.flatMap { (_, group) ->
                if (group.size > 1) {
                    val fst = group.first()
                    val merged = Lambda("merged_${context.getIndexAndIncrement()}", fst.file, fst.entrypoint, fst.config, fst.permissions)
                    group.map { it to merged }
                } else {
                    listOf(group.single() to group.single())
                }
            }.toMap()
        }
    }

    override fun optimize(schema: Schema, optimization: Optimization, context: OptimizationContext): Schema {
        val mergedMap = merge(schema.lambdas, optimization.mergeLambda, context)
        val lambdas = schema.lambdas.map { mergedMap.getValue(it) }.toSet()
        val dynamics = schema.webapps.map { webapp -> webapp to webapp.api.dynamics.map { dynamic -> dynamic.copy(lambda = mergedMap.getValue(dynamic.lambda)) }.toSet() }.toMap()
        val scheduled = schema.webapps.map { webapp -> webapp to webapp.events.scheduled.map { event -> event.copy(lambda = mergedMap.getValue(event.lambda)) }.toSet() }.toMap()
        val webapps = schema.webapps.map { webapp ->
            webapp.copy(
                api = webapp.api.copy(dynamics = dynamics.getValue(webapp)),
                events = webapp.events.copy(scheduled = scheduled.getValue(webapp))
            )
        }.toSet()

        return schema.copy(lambdas = lambdas).copy(webapps = webapps)
    }
}
