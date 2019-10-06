package io.kotless.hcl

/** Element of HCL that can be presented as text */
interface HCLRender {
    val renderable: Boolean

    fun render(): String
}
