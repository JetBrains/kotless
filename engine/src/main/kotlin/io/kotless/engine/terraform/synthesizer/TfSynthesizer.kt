package io.kotless.engine.terraform.synthesizer

import io.kotless.engine.terraform.TfEntity
import io.kotless.engine.terraform.utils.poll
import io.kotless.engine.terraform.utils.toTfName
import java.util.*
import kotlin.collections.HashSet

/**
 * Synthesizer of terraform code.
 *
 * It takes entities from TfEntity (all created) and renders them into
 * files grouping by TfGroup of each resource and sorting entities
 * in topological order (using depends_on relation) per-file.
 */
object TfSynthesizer {
    data class TfFile(val name: String, val content: String)

    fun synthesize(): List<TfSynthesizer.TfFile> {
        val entitiesByGroup = TfEntity.instantiatedEntities.values.groupBy { it.group }

        val sortedEntitiesByGroup = entitiesByGroup.map { (group, entities) ->
            group to TfSynthesizer.sortTopEntities(group, entities)
        }

        return sortedEntitiesByGroup.map { TfSynthesizer.TfFile(it.first.name.toTfName() + ".tf", it.second.joinToString(separator = "\n\n") { it.render() }) }
    }

    /** Topological sorting of tf resources in a file. */
    private fun sortTopEntities(tfGroup: TfGroup, entities: List<TfEntity>): List<TfEntity> {
        val groups = TfSynthesizer.getConnectedGroups(entities)
        val sortedGroups = groups.map { TfSynthesizer.topSort(it) }.map { tfGroup.resort(it) }
        return sortedGroups.flatten()
    }

    private fun getConnectedGroups(entities: List<TfEntity>): List<List<TfEntity>> {
        var groupCounter = 0
        val seenEntity = entities.map { it to 0 }.toMap().toMutableMap()

        val aliasGroups = ArrayList<Int>()
        fun deepSearch(entity: TfEntity) {
            if (seenEntity[entity] == 0) {
                seenEntity[entity] = -1
                entity.uses.filter { it in entities }.map {
                    deepSearch(it)
                }
            } else if (seenEntity[entity] != -1) {
                aliasGroups.add(seenEntity[entity]!!)
            }
        }

        while (seenEntity.any { it.value == 0 }) {
            aliasGroups.clear()
            groupCounter++
            val start = seenEntity.filter { it.value == 0 }.entries.first().key
            deepSearch(start)
            for ((key, value) in seenEntity) {
                if (value in (aliasGroups + -1)) {
                    seenEntity[key] = groupCounter
                }
            }
        }

        return seenEntity.entries.groupBy { it.value }.map { it.value.map { it.key } }
    }

    @Suppress("ComplexMethod")
    private fun usedByTransClosure(entities: List<TfEntity>): MutableMap<TfEntity, MutableSet<TfEntity>> {
        val entitiesUsed = entities.map { it to mutableSetOf<TfEntity>() }.toMap().toMutableMap()
        entities.forEach { entityToFind ->
            fun innerUsedBy(entity: TfEntity, alreadySeen: MutableSet<TfEntity>): Boolean {
                if (entity !in alreadySeen) {
                    entity.uses.filter { it in entities }.forEach {
                        val res = innerUsedBy(it, (alreadySeen + entity).toMutableSet())
                        if (res) {
                            entitiesUsed[entityToFind]!!.add(it)
                        }
                    }
                    if (entityToFind in entity.uses) {
                        return true
                    }
                }
                return false
            }
            entities.forEach { entityStart ->
                if (innerUsedBy(entityStart, HashSet())) {
                    entitiesUsed[entityToFind]!!.add(entityStart)
                }
            }

        }
        return entitiesUsed.also { it.forEach { it.value.remove(it.key) } }
    }

    private fun topSort(entities: List<TfEntity>): List<TfEntity> {
        val resultList = ArrayList<TfEntity>()
        var zeroQueue: MutableList<TfEntity> = ArrayList()

        val usesEntities = entities.map { it to it.uses.filter { it in entities }.toMutableList() }.toMap().toMutableMap()
        val usedEntities = TfSynthesizer.usedByTransClosure(entities)

        usesEntities.filter { it.value.isEmpty() && !(zeroQueue.contains(it.key) || resultList.contains(it.key)) }.forEach {
            zeroQueue.add(it.key)
        }
        zeroQueue = zeroQueue.sortedBy { usedEntities[it]!!.size }.toMutableList()

        var currentEntity = zeroQueue.poll() ?: usesEntities.minBy { it.value.size }?.key
        while (currentEntity != null) {
            resultList += currentEntity

            usesEntities.remove(currentEntity)
            usesEntities.forEach { it.value.remove(currentEntity!!) }

            usedEntities.remove(currentEntity)
            usedEntities.forEach { it.value.remove(currentEntity!!) }

            usesEntities.filter { it.value.isEmpty() && !(zeroQueue.contains(it.key) || resultList.contains(it.key)) }.forEach {
                zeroQueue.add(it.key)
            }

            zeroQueue = zeroQueue.sortedBy { usedEntities[it]!!.size }.toMutableList()

            val min = usesEntities.values.map { it.size }.min()
            currentEntity = zeroQueue.poll() ?: usesEntities.filter { it.value.size == min }.maxBy { it.value.intersect(resultList).size }?.key
        }
        return resultList
    }
}
