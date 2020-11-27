package io.kotless.terraform.functions

import io.terraformkt.hcl.HCLTextField
import java.io.File

//Escaping required for Windows
private fun escape(value: String) = value.replace("\\", "\\\\")

/** Get a canonical path of file */
fun path(file: File): String = escape(file.canonicalPath)

fun md5(field: String) = "md5($field)"
fun base64sha256(field: String) = "base64sha256($field)"

fun filemd5(file: File) = filemd5(path(file))
fun filemd5(file: String) = "filemd5(${HCLTextField.toText(file)})"

fun filesha256(file: String) = "filesha256(${HCLTextField.toText(file)})"

fun file(file: File) = file(path(file))
fun file(file: String) = "file(${HCLTextField.toText(file)})"


fun timestamp() = "timestamp()"

/**
 * Unlink is used to restyle old `"${ref}"` style refs to Terraform 12 style `ref`
 */
fun unlink(field: String): String {
    if (isLink(field)) return field.drop(2).dropLast(1)
    return field
}

fun isLink(field: String) = field.startsWith("\${") && field.endsWith("}")

fun link(field: String) = "\${$field}"
fun link(vararg parts: String?) = link(parts.filterNotNull().joinToString(separator = "."))

fun eval(func: String) = "\${$func}"
