package io.kotless.hcl

interface HCLRender {
    val renderable: Boolean

    fun render(indentNum: Int, appendable: Appendable) {
        if (renderable) appendable.append(render(indentNum))
    }

    fun render(indentNum: Int): String
}
