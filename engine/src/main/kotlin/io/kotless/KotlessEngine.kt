package io.kotless

import io.kotless.gen.Generator
import java.io.File

object KotlessEngine  {
    fun generate(schema: Schema) = Generator.generate(schema).map { tfFile ->
        schema.kotlessConfig.genDirectory.mkdir()
        val file = File(schema.kotlessConfig.genDirectory, tfFile.nameWithExt)
        tfFile.write(file)
        file
    }
}
