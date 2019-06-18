package io.kotless.engine.optimization

import io.kotless.KotlessConfig.Optimization
import io.kotless.Lambda

/**
 * Optimization defines, if different lambdas should be merged into one and when.
 *
 * Basically, lambda serving few endpoints is more likely to be warm.
 *
 * There are 3 levels of merge optimization:
 * * None -- lambdas will never be merged
 * * PerPermissions -- lambdas will be merged, if they have equal permissions
 * * All -- all lambdas in context are merged in one
 */
object LambdaMergeOptimization {
    private var globalMergeIndex = 0

    /** Result of LambdaMergeOptimization */
    data class MergedLambda(val merged: Lambda, val level: Optimization.MergeLambda, val from: LinkedHashSet<Lambda>) {
        /** Comment with information on what lambdas were merged and with which level */
        val comment = if (from.size > 1) {
            """ |// Due to lambda merge optimization mode ${level.name} this lambda
                |// was merged from:
                ${from.joinToString(separator = "\n") { "|//     * ${it.name}" }}
            """
        } else {
            null
        }
    }

    /**
     * Merges the set of lambdas in accordance with `level`
     *
     * @param lambdas -- lambdas to be merged
     * @param level -- level of merge optimization
     * @return list of MergedLambda
     */
    fun merge(lambdas: LinkedHashSet<Lambda>, level: Optimization.MergeLambda): List<MergedLambda> = when (level) {
        Optimization.MergeLambda.None -> lambdas.map { MergedLambda(it, level, linkedSetOf(it)) }
        Optimization.MergeLambda.PerPermissions -> {
            lambdas.groupBy { listOf(it.config, it.entrypoint, it.file, it.permissions) }.map {
                if (it.value.size > 1) {
                    val fst = it.value.first()
                    val merged = Lambda("merged_${globalMergeIndex++}", fst.file, fst.entrypoint, fst.config, fst.permissions)
                    MergedLambda(merged, level, LinkedHashSet(it.value))
                } else {
                    MergedLambda(it.value.single(), level, linkedSetOf(it.value.single()))
                }
            }
        }
        Optimization.MergeLambda.All -> {
            lambdas.groupBy { listOf(it.config, it.entrypoint, it.file) }.map {
                if (it.value.size > 1) {
                    val fst = it.value.first()
                    val mergedPermissions = it.value.flatMap { it.permissions }.toSet()
                    val merged = Lambda("merged_${globalMergeIndex++}", fst.file, fst.entrypoint, fst.config, mergedPermissions)
                    MergedLambda(merged, level, LinkedHashSet(it.value))
                } else {
                    MergedLambda(it.value.single(), level, linkedSetOf(it.value.single()))
                }
            }
        }
    }

    fun cleanup() {
        globalMergeIndex = 0
    }
}
