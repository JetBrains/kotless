package io.kotless.gen

import io.kotless.Schema
import io.kotless.terraform.TFFile
import java.io.File

object KotlessGen {
    fun gen(schema: Schema) {

    }

    private fun write(toDir: File, files: Set<TFFile>) {
        toDir.mkdirs()
        for (file in files) {
            file.write(File(toDir, file.nameWithExt))
        }
    }
}
