package io.kotless.hcl

/** Element of HCL that can be refered */
interface HCLNamed {
    val hcl_name: String
    val hcl_ref: String
}
