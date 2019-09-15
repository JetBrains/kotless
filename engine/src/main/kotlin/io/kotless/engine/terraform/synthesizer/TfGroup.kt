package io.kotless.engine.terraform.synthesizer

import io.kotless.engine.terraform.*
import kotlin.reflect.KClass

/**
 * Groups of terraform resources by AWS services.
 *
 * Different groups are mapped to different files and used to
 * generate more human-readable terraform code.
 *
 * Also, groups may define sorting order in their file.
 */
enum class TfGroup {
    RestApi,
    Cloudwatch,
    Lambda,
    IAM,
    Route53,
    ACM,
    S3 {
        override fun resort(entities: List<TfEntity>): List<TfEntity> = entities.filter { it is TfResource } +
            entities.filter { it is TfData } + entities.filter { it !is TfResource && it !is TfData }
    },
    Info;

    /**
     * Sort entities after initial sort with order used by TfGroup
     *
     * Method makes possible to set custom order of entities in file
     * for specific TfGroup.
     */
    open fun resort(entities: List<TfEntity>): List<TfEntity> = entities

    fun <T : Any, Y : KClass<*>> sortInClassOrder(entities: List<T>, klasses: List<Y>): List<T> {
        val resultEntities = ArrayList<T>()
        for (klass in klasses) {
            resultEntities += entities.filter { it::class == klass }
        }
        resultEntities += entities.filterNot { resultEntities.contains(it) }
        return resultEntities
    }
}
