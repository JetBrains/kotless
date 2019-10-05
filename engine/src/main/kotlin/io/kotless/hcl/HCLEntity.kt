package io.kotless.hcl

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

open class HCLEntity(val fields: LinkedHashSet<HCLField<*>> = LinkedHashSet(), open val owner: HCLNamed? = null) : HCLRender {
    override val renderable: Boolean = true
    private val renderableFields: Collection<HCLField<*>>
        get() = fields.filter { it.renderable }

    override fun render(): String = renderableFields.joinToString(separator = "\n") {
        it.render()
    }

    inner class FieldProvider<T : Any, F : HCLField<T>>(val name: String?, val inner: Boolean, val default: T?,
                                                        val getField: (name: String, renderable: Boolean, entity: HCLEntity, value: T?) -> F) {
        operator fun provideDelegate(entity: HCLEntity, property: KProperty<*>): FieldDelegate<T, F> {
            val field = getField(name ?: property.name, inner, entity, default)
            entity.fields.add(field)
            return FieldDelegate(field)
        }
    }


    class FieldDelegate<T : Any, F : HCLField<T>>(val field: F) : ReadWriteProperty<HCLEntity, T> {
        val hcl_ref: String by lazy { field.hcl_ref }

        override fun getValue(thisRef: HCLEntity, property: KProperty<*>): T = field.value!!

        override fun setValue(thisRef: HCLEntity, property: KProperty<*>, value: T) {
            field.value = value
        }
    }

    fun <T : HCLEntity> entity(name: String? = null, inner: Boolean = false, default: T) = FieldProvider(name, inner, default) { field, renderable, entity, value ->
        HCLEntityField(field, renderable, entity, value)
    }

    fun int(name: String? = null, inner: Boolean = false, default: Int? = null) = FieldProvider(name, inner, default) { field, renderable, entity, value ->
        HCLIntField(field, renderable, entity, value)
    }

    fun intArray(name: String? = null, inner: Boolean = false, default: Array<Int>? = null) = FieldProvider(name, inner, default) { field, renderable, entity, value ->
        HCLIntArrayField(field, renderable, entity, value)
    }

    fun bool(name: String? = null, inner: Boolean = false, default: Boolean? = null) = FieldProvider(name, inner, default) { field, renderable, entity, value ->
        HCLBoolField(field, renderable, entity, value)
    }

    fun boolArray(name: String? = null, inner: Boolean = false, default: Array<Boolean>? = null) = FieldProvider(name, inner, default) { field, renderable, entity, value ->
        HCLBoolArrayField(field, renderable, entity, value)
    }

    fun text(name: String? = null, inner: Boolean = false, default: String? = null) = FieldProvider(name, inner, default) { field, renderable, entity, value ->
        HCLTextField(field, renderable, entity, value)
    }

    fun textArray(name: String? = null, inner: Boolean = false, default: Array<String>? = null) = FieldProvider(name, inner, default) { field, renderable, entity, value ->
        HCLTextArrayField(field, renderable, entity, value)
    }
}

val <T : Any> KProperty0<T>.ref: String
    get() {
        this.isAccessible = true
        return (getDelegate() as HCLEntity.FieldDelegate<*, *>).hcl_ref
    }
