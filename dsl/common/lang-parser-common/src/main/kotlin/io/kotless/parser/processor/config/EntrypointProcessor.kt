package io.kotless.parser.processor.config

import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import io.kotless.DSLType
import io.kotless.Lambda
import io.kotless.dsl.LambdaHandler
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.SubTypesProcessor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

object EntrypointProcessor : SubTypesProcessor<EntrypointProcessor.Output>() {
    data class Output(val entrypoint: Lambda.Entrypoint)

    override val klasses = setOf(RequestStreamHandler::class)

    override fun mayRun(context: ProcessorContext) = true

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext): Output {
        if (context.config.dsl.type == DSLType.Kotless) {
            return Output(Lambda.Entrypoint("${LambdaHandler::class.qualifiedName}::${LambdaHandler::handleRequest.name}", emptySet()))
        }

        return Output(find(files, binding))
    }

    fun find(files: Set<KtFile>, binding: BindingContext): Lambda.Entrypoint {
        val entrypoint = ArrayList<Lambda.Entrypoint>()
        processClasses(files, binding) { klass, _ ->
            entrypoint.add(Lambda.Entrypoint("${klass.fqName!!.asString()}::${RequestStreamHandler::handleRequest.name}", emptySet()))
        }

        require(entrypoint.size != 0) { "There should be a class inherited from ${RequestStreamHandler::class} in your app" }
        require(entrypoint.size == 1) { "There should be only one class inherited from ${RequestStreamHandler::class} in your app" }

        return entrypoint.single()
    }
}
