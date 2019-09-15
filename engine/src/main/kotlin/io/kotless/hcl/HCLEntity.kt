package io.kotless.hcl

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

open class HCLEntity(val fields: LinkedHashSet<HCLField<*>> = LinkedHashSet(), val owner: HCLNamed? = null) : HCLRender {
    override val renderable: Boolean = true

    override fun render(indentNum: Int): String = buildString {
        for ((ind, field) in fields.filter { it.renderable }.withIndex()) {
            field.render(indentNum, this)
            if (ind != fields.size - 1) {
                append("\n")
            }
        }
    }

    abstract inner class FieldDelegate<T : Any, F : HCLField<T>>(val name: String?,
                                                                 private val inner: Boolean,
                                                                 private val default: T) : ReadWriteProperty<HCLEntity, T> {
        var field: F? = null
        protected abstract fun getField(name: String, renderable: Boolean, entity: HCLEntity, value: T): F
        protected fun init(property: KProperty<*>) {
            if (field == null) {
                field = getField(name ?: property.name, inner, this@HCLEntity, default)
                fields += field!!
            }
        }

        override fun getValue(thisRef: HCLEntity, property: KProperty<*>): T {
            init(property)
            return field!!.value
        }

        override fun setValue(thisRef: HCLEntity, property: KProperty<*>, value: T) {
            init(property)
            field!!.value = value
        }
    }

    inner class IntFieldDelegate(name: String?, inner: Boolean, default: Int) : FieldDelegate<Int, HCLIntField>(name, inner, default) {
        override fun getField(name: String, renderable: Boolean, entity: HCLEntity, value: Int) = HCLIntField(name, renderable, entity, value)
    }

    inner class IntArrayFieldDelegate(name: String?, inner: Boolean, default: Array<Int>) : FieldDelegate<Array<Int>, HCLIntArrayField>(name, inner, default) {
        override fun getField(name: String, renderable: Boolean, entity: HCLEntity, value: Array<Int>) = HCLIntArrayField(name, renderable, entity, value)
    }

    inner class TextFieldDelegate(name: String?, inner: Boolean, default: String) : FieldDelegate<String, HCLTextField>(name, inner, default) {
        override fun getField(name: String, renderable: Boolean, entity: HCLEntity, value: String) = HCLTextField(name, renderable, entity, value)
    }

    inner class TextArrayFieldDelegate(name: String?, inner: Boolean, default: Array<String>) : FieldDelegate<Array<String>, HCLTextArrayField>(name, inner, default) {
        override fun getField(name: String, renderable: Boolean, entity: HCLEntity, value: Array<String>) = HCLTextArrayField(name, renderable, entity, value)
    }

    inner class BoolFieldDelegate(name: String?, inner: Boolean, default: Boolean) : FieldDelegate<Boolean, HCLBoolField>(name, inner, default) {
        override fun getField(name: String, renderable: Boolean, entity: HCLEntity, value: Boolean) = HCLBoolField(name, renderable, entity, value)
    }

    inner class BoolArrayFieldDelegate(name: String?, inner: Boolean, default: Array<Boolean>) : FieldDelegate<Array<Boolean>, HCLBoolArrayField>(name, inner, default) {
        override fun getField(name: String, renderable: Boolean, entity: HCLEntity, value: Array<Boolean>) = HCLBoolArrayField(name, renderable, entity, value)
    }

    inner class EntityFieldDelegate<T : HCLEntity>(name: String?, inner: Boolean, default: T) : FieldDelegate<T, HCLEntityField<T>>(name, inner, default) {
        override fun getField(name: String, renderable: Boolean, entity: HCLEntity, value: T) = HCLEntityField(name, renderable, entity, value)
    }

    fun <T : HCLEntity> entity(name: String? = null, inner: Boolean = false, default: T) = EntityFieldDelegate(name, inner, default)

    fun int(name: String? = null, inner: Boolean = false, default: Int = 0) = IntFieldDelegate(name, inner, default)
    fun intArray(name: String? = null, inner: Boolean = false, default: Array<Int> = emptyArray()) = IntArrayFieldDelegate(name, inner, default)

    fun bool(name: String? = null, inner: Boolean = false, default: Boolean = false) = BoolFieldDelegate(name, inner, default)
    fun boolArray(name: String? = null, inner: Boolean = false, default: Array<Boolean> = emptyArray()) = BoolArrayFieldDelegate(name, inner, default)

    fun text(name: String? = null, inner: Boolean = false, default: String = "") = TextFieldDelegate(name, inner, default)
    fun textArray(name: String? = null, inner: Boolean = false, default: Array<String> = emptyArray()) = TextArrayFieldDelegate(name, inner, default)
}

val <T : Any> KProperty0<T>.ref: String
    get() = (this.getDelegate() as HCLEntity.FieldDelegate<*, *>).field!!.ref
