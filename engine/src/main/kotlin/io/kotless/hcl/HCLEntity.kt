package io.kotless.hcl

import io.kotless.utils.forEachWithEnd
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

open class HCLEntity(val fields: LinkedHashSet<HCLField<*>> = LinkedHashSet(), open val owner: HCLNamed? = null) : HCLRender {
    override val renderable: Boolean = true
    private val renderableFields: Collection<HCLField<*>>
        get() = fields.filter { it.renderable }

    override fun render(indentNum: Int): String = buildString {
        renderableFields.forEachWithEnd { field, isEnd ->
            field.render(indentNum, this)
            if (!isEnd) append("\n")
        }
    }

    inner class FieldProvider<T : Any, F : HCLField<T>>(val name: String?, val inner: Boolean, val default: T,
                                                        val getField: (name: String, renderable: Boolean, entity: HCLEntity, value: T) -> F) {
        operator fun provideDelegate(thisRef: HCLEntity, prop: KProperty<*>): FieldDelegate<T, F> {
            val field = getField(name ?: prop.name, inner, thisRef, default)
            thisRef.fields.add(field)
            return FieldDelegate(field)
        }
    }


    inner class FieldDelegate<T : Any, F : HCLField<T>>(val field: F) : ReadWriteProperty<HCLEntity, T> {
        val hcl_ref: String by lazy { field.hcl_ref }

        override fun getValue(thisRef: HCLEntity, property: KProperty<*>): T = field.value

        override fun setValue(thisRef: HCLEntity, property: KProperty<*>, value: T) {
            field.value = value
        }
    }

    fun <T : HCLEntity> entity(name: String? = null, inner: Boolean = false, default: T) = FieldProvider(name, inner, default) { name, renderable, entity, value ->
        HCLEntityField(name, renderable, entity, value)
    }

    fun int(name: String? = null, inner: Boolean = false, default: Int = 0) = FieldProvider(name, inner, default) { name, renderable, entity, value ->
        HCLIntField(name, renderable, entity, value)
    }
    fun intArray(name: String? = null, inner: Boolean = false, default: Array<Int> = emptyArray()) = FieldProvider(name, inner, default) { name, renderable, entity, value ->
        HCLIntArrayField(name, renderable, entity, value)
    }

    fun bool(name: String? = null, inner: Boolean = false, default: Boolean = false) = FieldProvider(name, inner, default) { name, renderable, entity, value ->
        HCLBoolField(name, renderable, entity, value)
    }
    fun boolArray(name: String? = null, inner: Boolean = false, default: Array<Boolean> = emptyArray()) = FieldProvider(name, inner, default) { name, renderable, entity, value ->
        HCLBoolArrayField(name, renderable, entity, value)
    }

    fun text(name: String? = null, inner: Boolean = false, default: String = "")  = FieldProvider(name, inner, default) { name, renderable, entity, value ->
        HCLTextField(name, renderable, entity, value)
    }
    fun textArray(name: String? = null, inner: Boolean = false, default: Array<String> = emptyArray())  = FieldProvider(name, inner, default) { name, renderable, entity, value ->
        HCLTextArrayField(name, renderable, entity, value)
    }
}

val <T : Any> KProperty0<T>.ref: String
    get() {
        this.isAccessible = true
        return (getDelegate() as HCLEntity.FieldDelegate<*, *>).hcl_ref
    }
