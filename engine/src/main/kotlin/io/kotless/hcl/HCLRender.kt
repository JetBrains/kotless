package io.kotless.hcl

interface HCLRender {
    val renderable: Boolean

    fun render(): String
}
