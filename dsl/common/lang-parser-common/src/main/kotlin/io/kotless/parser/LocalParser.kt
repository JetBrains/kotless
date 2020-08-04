package io.kotless.parser

import io.kotless.resource.Lambda
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.utils.psi.analysis.EnvironmentManager
import io.kotless.parser.utils.psi.analysis.ParseUtil
import io.kotless.parser.utils.psi.analysis.ResolveUtil
import java.io.File

/**
 * LocalParser parses Kotlin code with Kotlin embeddable compiler looking
 * for Kotless DSL constructs that needed for local execution.
 */
object LocalParser {
    data class Result(val entrypoint: Lambda.Entrypoint)

    fun parse(files: Set<File>, libs: Set<File>): Result {
        val environment = EnvironmentManager.create(libs)

        val ktFiles = ParseUtil.analyze(files, environment)
        val binding = ResolveUtil.analyze(ktFiles, environment).bindingContext

        val entrypoint = EntrypointProcessor.find(ktFiles, binding)

        return Result(entrypoint)
    }
}
