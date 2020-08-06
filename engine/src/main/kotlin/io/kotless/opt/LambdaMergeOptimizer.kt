package io.kotless.opt

import io.kotless.KotlessConfig.Optimization
import io.kotless.resource.Lambda
import io.kotless.ScheduledEventType
import io.kotless.Schema
import io.kotless.Application
import io.kotless.utils.Storage
import io.kotless.utils.TypedStorage
import io.kotless.utils.everyNMinutes

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
    fun merge(lambdas: TypedStorage<Lambda>, level: Optimization.MergeLambda, context: OptimizationContext): Map<TypedStorage.Key<Lambda>, Lambda> {
        return when (level) {
            Optimization.MergeLambda.None -> lambdas.entries.map { it.key to it.value }.toMap()
            Optimization.MergeLambda.All, Optimization.MergeLambda.PerPermissions -> {
                val grouped = when (level) {
                    Optimization.MergeLambda.PerPermissions -> lambdas.entries.groupBy { (_, it) -> listOf(it.config, it.entrypoint, it.file, it.permissions) }
                    Optimization.MergeLambda.All -> lambdas.entries.groupBy { (_, it) -> listOf(it.config, it.entrypoint, it.file) }
                    Optimization.MergeLambda.None -> error("Merge mode could not be None, but was")
                }
                grouped.flatMap { (_, group) ->
                    if (group.size > 1) {
                        val (_, fst) = group.first()
                        val permissions = group.flatMap { it.value.permissions }.toSet()

                        val prefix = commonPrefix(group.map { it.value.name })
                        val mergedName = if (prefix.isEmpty()) "merged" else prefix

                        val merged = Lambda("${mergedName}-${context.getIndexAndIncrement()}", fst.file, fst.entrypoint, fst.config, permissions)
                        group.map { it.key to merged }
                    } else {
                        listOf(group.single().key to group.single().value)
                    }
                }.toMap()
            }
        }
    }

    private fun commonPrefix(items: List<String>): String {
        return items.reduce { acc, s -> acc.commonPrefixWith(s) }.trimEnd('-')
    }

    override fun optimize(schema: Schema, optimization: Optimization, context: OptimizationContext): Schema {
        val mergedMap = merge(schema.lambdas, optimization.mergeLambda, context)
        val scheduled = if (optimization.autowarm.enable) {
            (schema.application.events.scheduled.filter { it.type != ScheduledEventType.Autowarm } +
                mergedMap.entries.distinctBy { it.value }.map { (key, lambda) ->
                    Application.Events.Scheduled(lambda.name, everyNMinutes(optimization.autowarm.minutes), ScheduledEventType.Autowarm, key)
                }).toSet()
        } else schema.application.events.scheduled

        return schema.copy(
            lambdas = TypedStorage(HashMap(mergedMap)),
            application = schema.application.copy(
                events = schema.application.events.copy(scheduled = scheduled)
            )
        )
    }
}
