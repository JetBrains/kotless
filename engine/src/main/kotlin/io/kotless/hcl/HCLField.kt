package io.kotless.hcl

import io.kotless.terraform.functions.link
import io.kotless.utils.Text
import io.kotless.utils.indent

sealed class HCLField<T : Any>(override val hcl_name: String, inner: Boolean, private val entity: HCLEntity, var value: T) : HCLNamed, HCLRender {
    override val renderable: Boolean = !inner

    override val hcl_ref: String by lazy { link(entity.owner?.hcl_ref, hcl_name) }
}

class HCLEntityField<T : HCLEntity>(name: String, inner: Boolean, owner: HCLEntity, value: T) : HCLField<T>(name, inner, owner, value) {
    override fun render(indentNum: Int): String {
        return """
            |${indent(indentNum)}$hcl_name = {
            |${value.render(indentNum + Text.indent)}
            |${indent(indentNum)}}""".trimMargin()
    }
}

class HCLTextField(name: String, inner: Boolean, owner: HCLEntity, value: String) : HCLField<String>(name, inner, owner, value) {
    override fun render(indentNum: Int): String {
        return "${indent(indentNum)}$hcl_name = \"$value\""
    }
}

class HCLTextArrayField(name: String, inner: Boolean, owner: HCLEntity, value: Array<String>) : HCLField<Array<String>>(name, inner, owner, value) {
    override fun render(indentNum: Int): String {
        return "${indent(indentNum)}$hcl_name = ${value.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }}"
    }
}

class HCLBoolField(name: String, inner: Boolean, owner: HCLEntity, value: Boolean) : HCLField<Boolean>(name, inner, owner, value) {
    override fun render(indentNum: Int): String {
        return "${indent(indentNum)}$hcl_name = $value"
    }
}

class HCLBoolArrayField(name: String, inner: Boolean, owner: HCLEntity, value: Array<Boolean>) : HCLField<Array<Boolean>>(name, inner, owner, value) {
    override fun render(indentNum: Int): String {
        return "${indent(indentNum)}$hcl_name = ${value.joinToString(prefix = "[", postfix = "]") { "$it" }}"
    }
}

class HCLIntField(name: String, inner: Boolean, owner: HCLEntity, value: Int) : HCLField<Int>(name, inner, owner, value) {
    override fun render(indentNum: Int): String {
        return "${indent(indentNum)}$hcl_name = $value"
    }
}

class HCLIntArrayField(name: String, inner: Boolean, owner: HCLEntity, value: Array<Int>) : HCLField<Array<Int>>(name, inner, owner, value) {
    override fun render(indentNum: Int): String {
        return "${indent(indentNum)}$hcl_name = ${value.joinToString(prefix = "[", postfix = "]") { "$it" }}"
    }
}
