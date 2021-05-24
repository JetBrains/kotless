package io.kotless.parser.processor.config

import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import io.kotless.*
import io.kotless.dsl.HandlerAWS
import io.kotless.dsl.HandlerAzure
import io.kotless.dsl.cloud.azure.AzureRequestHandler
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.SubTypesProcessor
import io.kotless.parser.utils.errors.require
import io.kotless.parser.utils.psi.isSubtypeOf
import io.kotless.resource.Lambda
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

object EntrypointProcessor : SubTypesProcessor<EntrypointProcessor.Output>() {
    data class Output(val entrypoint: Lambda.Entrypoint)

    override val klasses = setOf(RequestStreamHandler::class, AzureRequestHandler::class)

    override fun mayRun(context: ProcessorContext) = true

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext): Output {
        if (context.config.dsl.type == DSLType.Kotless) {
            return when (context.config.cloud.platform) {
                CloudPlatform.Azure -> Output(Lambda.Entrypoint("${HandlerAzure::class.qualifiedName}.run"))
                CloudPlatform.AWS -> Output(Lambda.Entrypoint("${HandlerAWS::class.qualifiedName}::${HandlerAWS::handleRequest.name}"))
            }
        }

        return Output(find(files, binding))
    }

    fun find(files: Set<KtFile>, binding: BindingContext): Lambda.Entrypoint {
        val entrypoint = ArrayList<Lambda.Entrypoint>()
        processClassesOrObjects(files, binding) { klass, _ ->
            entrypoint.add(klass.makeLambdaEntrypoint(binding))
        }

        require(entrypoint.size != 0) { "There should be a class or object inherited from ${RequestStreamHandler::class} or KotlessAWS in your app" }
        require(entrypoint.size == 1) { "There should be only one class or object inherited from ${RequestStreamHandler::class} or KotlessAWS in your app" }

        return entrypoint.first()
    }

    private fun KtClassOrObject.makeLambdaEntrypoint(binding: BindingContext): Lambda.Entrypoint {
        require(this, fqName != null) { "Anonymous class cannot be inherited from RequestStreamHandler or Kotless class" }
        if (this.isSubtypeOf(RequestStreamHandler::class, binding)) {
            return Lambda.Entrypoint("${fqName!!.asString()}::${RequestStreamHandler::handleRequest.name}")
        }
        if (this.isSubtypeOf(AzureRequestHandler::class, binding)) {
            return Lambda.Entrypoint("${fqName!!.asString()}.run")
        }
        error("Entry point should be inherited from ${RequestStreamHandler::class} or ${AzureRequestHandler::class}")
    }
}
