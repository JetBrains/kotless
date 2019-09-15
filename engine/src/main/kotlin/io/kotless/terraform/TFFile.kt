package io.kotless.terraform

import io.kotless.hcl.HCLEntity
import java.io.File

class TFFile(val name: String, private val entities: ArrayList<HCLEntity> = ArrayList()) {

    val nameWithExt = "$name.tf"

    fun write(file: File) {
        if (!file.exists()) file.createNewFile()
        file.writeText(buildString {
            for (entity in entities) {
                entity.render(0, this)
                append("\n\n")
            }
        })
    }

    fun add(entity: HCLEntity) {
        entities.add(entity)
    }
}

fun tf(name: String, configure: TFFile.() -> Unit) = TFFile(name).apply(configure)
