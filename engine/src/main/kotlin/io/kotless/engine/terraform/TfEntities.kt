package io.kotless.engine.terraform

import io.kotless.engine.terraform.utils.filterBlankLines

/**
 * Abstract representation of terraform entity.
 *
 * Each instantiated terraform entity is added into `instantiatedEntities`
 * static val and will be rendered by `TfSynthesizer`.
 */
abstract class TfEntity(type: EntityType, tfType: String, tfName: String) {
    val descriptor = TfDescriptor(type, tfType, tfName)

    /** Name with type -- (data.)?aws_*.name */
    val tfFullName = descriptor.fullName

    /** Full type -- (data.)?aws_* */
    val tfFullType = descriptor.fullType

    @Suppress("EnumEntryName")
    enum class EntityType {
        data,
        resource
    }


    data class TfDescriptor(val type: EntityType, val tfType: String, val tfName: String) {
        val fullType = when (type) {
            EntityType.data -> "data.$tfType"
            EntityType.resource -> tfType
        }

        val fullName = "$fullType.$tfName"

        companion object {
            operator fun invoke(fullName: String): TfDescriptor {
                return if (fullName.startsWith("data")) {
                    TfDescriptor(EntityType.data, fullName.split(".")[1], fullName.split(".")[2])
                } else {
                    TfDescriptor(EntityType.resource, fullName.split(".")[0], fullName.split(".")[1])
                }
            }
        }
    }

    companion object {
        var resourcePrefix = ""

        val instantiatedEntities = HashMap<TfDescriptor, TfEntity>()

        operator fun get(descriptor: TfDescriptor) = TfEntity.instantiatedEntities[descriptor]

        fun cleanup() {
            TfEntity.instantiatedEntities.clear()
            TfEntity.resourcePrefix = ""
        }
    }

    init {
        TfEntity.instantiatedEntities[this.descriptor] = this
    }

    /** Optional comment for this entity */
    var comment: String? = null

    /** Body of this entity */
    abstract val body: String

    /**
     * [uses] saves information about relations
     * between tf entities to use it when generating terraform code.
     *
     * Mostly, it will be filled automatically when you use `tf`
     *
     * @see tf
     */
    val uses = HashSet<TfEntity>()
    /** Group of this TfEntity */
    abstract val group: io.kotless.engine.terraform.synthesizer.TfGroup

    /** Render entity with body and possible comment */
    fun render() = """
        ${comment ?: ""}
        |${descriptor.type.name} "${descriptor.tfType}" "${descriptor.tfName}" {
        $body
        |}
        """.trimMargin().filterBlankLines()

    override fun equals(other: Any?) = descriptor == (other as? TfEntity)?.descriptor
    override fun hashCode() = descriptor.hashCode()
}

/** Representation of terraform resource. Based on TfEntity */
abstract class TfResource(tfType: String, tfName: String) : TfEntity(EntityType.resource, tfType, tfName) {
    val dependsOn = HashSet<TfResource>()

    abstract val resourceDef: String

    override val body: String
        get() = """
            |    ${if (dependsOn.isNotEmpty()) "depends_on = [${dependsOn.joinToString { "\"${it.tfFullName}\"" }}]" else ""}
            $resourceDef
        """.trimMargin()
}

/** Representation of terraform data. Based on TfEntity */
abstract class TfData(tfType: String, tfName: String) : TfEntity(EntityType.data, tfType, tfName) {
    abstract val dataDef: String

    override val body: String
        get() = dataDef
}
