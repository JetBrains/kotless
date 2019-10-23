package io.kotless.parser.processor.config

import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import io.kotless.Lambda
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.SubTypesProcessor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

object EntrypointProcessor : SubTypesProcessor<EntrypointProcessor.Output>() {
    data class Output(val entrypoint: Lambda.Entrypoint)

    override val klasses = setOf(RequestStreamHandler::class)

    override fun mayRun(context: ProcessorContext) = true

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext): Output {
        val entrypoint = ArrayList<Lambda.Entrypoint>()
        processClasses(files, binding) { klass, _ ->
            entrypoint.add(Lambda.Entrypoint("${klass.fqName!!.asString()}::${RequestStreamHandler::handleRequest.name}", emptySet()))
        }

        require(entrypoint.size == 1) { "There should be only one class inherited from ${RequestStreamHandler::class} in your app" }

        return Output(entrypoint.single())
    }
}
