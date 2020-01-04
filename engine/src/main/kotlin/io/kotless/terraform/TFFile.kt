package io.kotless.terraform

import io.kotless.hcl.HCLEntity
import java.io.File

/** Representation of file with Terraform code */
class TFFile(val name: String, private val entities: MutableList<HCLEntity> = ArrayList()) {
    val nameWithExt = "$name.tf"

    fun writeToDirectory(directory: File): File {
        require(directory.exists().not() || directory.isDirectory) { "TFFile can be written only to directory" }

        directory.mkdirs()

        val file = File(directory, nameWithExt)
        if (!file.exists()) file.createNewFile()

        file.writeText(buildString {
            for (entity in entities) {
                append(entity.render())
                append("\n\n")
            }
        })

        return file
    }

    fun add(entity: HCLEntity) {
        entities.add(entity)
    }
}

fun tf(name: String, configure: TFFile.() -> Unit) = TFFile(name).apply(configure)
