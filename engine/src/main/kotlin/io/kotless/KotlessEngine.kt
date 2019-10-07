package io.kotless

import io.kotless.gen.Generator
import io.kotless.opt.Optimizer
import java.io.File

object KotlessEngine {
    fun generate(schema: Schema): List<File> {
        val optimized = Optimizer.optimize(schema)
        val files = Generator.generate(optimized)
        return files.map { tfFile ->
            schema.config.genDirectory.mkdir()

            File(schema.config.genDirectory, tfFile.nameWithExt).apply {
                tfFile.write(this)
            }
        }
    }
}
