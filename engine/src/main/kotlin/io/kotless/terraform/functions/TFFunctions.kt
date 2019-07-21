package io.kotless.terraform.functions

import java.io.File

/** Get a canonical path of file */
fun path(file: File) = file.canonicalPath

fun md5(field: String) = "md5($field)"
fun base64sha256(field: String) = "base64sha256($field)"

fun file(file: File) = "file(${file.absolutePath})"
fun file(file: String) = "file($file)"


fun timestamp() = "timestamp()"
