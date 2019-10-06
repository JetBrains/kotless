package io.kotless

import io.kotless.gen.Generator
import io.kotless.opt.Optimizer
import java.io.File

object KotlessEngine {
    fun generate(schema: Schema): List<File> {
        val optimized = Optimizer.optimize(schema)
        val files = Generator.generate(optimized)
        return files.map { tfFile ->
            schema.kotlessConfig.genDirectory.mkdir()

            File(schema.kotlessConfig.genDirectory, tfFile.nameWithExt).apply {
                tfFile.write(this)
            }
        }
    }
}
