package io.kotless.opt

import io.kotless.*
import io.kotless.KotlessConfig.Optimization
import io.kotless.utils.*

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
    fun merge(lambdas: TypedStorage<Lambda>, level: Optimization.MergeLambda, context: OptimizationContext): Map<TypedStorage.Key<Lambda>, Lambda> = when (level) {
        Optimization.MergeLambda.None -> lambdas.entries.map { it.key to it.value }.toMap()
        else -> {
            val grouped = when (level) {
                Optimization.MergeLambda.PerPermissions -> lambdas.entries.groupBy { (_, it) -> listOf(it.config, it.entrypoint, it.file, it.permissions) }
                Optimization.MergeLambda.All -> lambdas.entries.groupBy { (_, it) -> listOf(it.config, it.entrypoint, it.file) }
                Optimization.MergeLambda.None -> error("Merge mode could not be None, but was")
            }
            grouped.flatMap { (_, group) ->
                if (group.size > 1) {
                    val (_, fst) = group.first()
                    val merged = Lambda("merged-${context.getIndexAndIncrement()}", fst.file, fst.entrypoint, fst.config, fst.permissions)
                    group.map { it.key to merged }
                } else {
                    listOf(group.single().key to group.single().value)
                }
            }.toMap()
        }
    }

    override fun optimize(schema: Schema, optimization: Optimization, context: OptimizationContext): Schema {
        val mergedMap = merge(schema.lambdas, optimization.mergeLambda, context)
        val scheduled = if (optimization.autowarm.enable) {
            (schema.webapp.events.scheduled.filter { it.type != ScheduledEventType.Autowarm } +
                mergedMap.entries.distinctBy { it.value }.map { (key, lambda) ->
                    Webapp.Events.Scheduled(lambda.name, everyNMinutes(optimization.autowarm.minutes), ScheduledEventType.Autowarm, key)
                }).toSet()
        } else schema.webapp.events.scheduled

        return schema.copy(
            lambdas = TypedStorage(HashMap(mergedMap)),
            webapp = schema.webapp.copy(
                events = schema.webapp.events.copy(scheduled = scheduled)
            )
        )
    }
}
