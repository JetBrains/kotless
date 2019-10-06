package io.kotless.terraform

import io.kotless.hcl.HCLEntity
import java.io.File

/** Representation of file with Terraform code */
class TFFile(val name: String, private val entities: MutableList<HCLEntity> = ArrayList()) {
    val nameWithExt = "$name.tf"

    fun write(file: File) {
        if (!file.exists()) file.createNewFile()
        file.writeText(buildString {
            for (entity in entities) {
                append(entity.render())
                append("\n\n")
            }
        })
    }

    fun add(entity: HCLEntity) {
        entities.add(entity)
    }
}

fun tf(name: String, configure: TFFile.() -> Unit) = TFFile(name).apply(configure)
