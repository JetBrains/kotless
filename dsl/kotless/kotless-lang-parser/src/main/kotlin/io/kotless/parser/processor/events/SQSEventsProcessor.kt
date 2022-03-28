package io.kotless.parser.processor.events

import io.kotless.Application.Events
import io.kotless.dsl.lang.event.S3Event
import io.kotless.dsl.lang.event.SQSEvent
import io.kotless.parser.processor.AnnotationProcessor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.action.GlobalActionsProcessor
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.errors.error
import io.kotless.parser.utils.errors.require
import io.kotless.parser.utils.psi.annotation.getValue
import io.kotless.resource.Lambda
import io.kotless.utils.TypedStorage
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import kotlin.math.absoluteValue

internal object SQSEventsProcessor : AnnotationProcessor<Unit>() {
    override val annotations = setOf(SQSEvent::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(GlobalActionsProcessor) && context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        val permissions = context.output.get(GlobalActionsProcessor).permissions
        val entrypoint = context.output.get(EntrypointProcessor).entrypoint

        processStaticFunctions(files, binding) { func, entry, _ ->
            require(func, func.fqName != null) { "@SQSEvent cannot be applied to anonymous function" }
            require(func, func.valueParameters.size == 1) { "@SQSEvent cannot be applied to ${func.fqName!!.asString()} since it has parameters" }

            val routePermissions = PermissionsProcessor.process(func, binding, context) + permissions

            val queueArn = entry.getValue(binding, SQSEvent::queueArn) ?: error(func, "@SQSEvent annotation must have `queueArn` parameter set")
            val id = func.fqName!!.asString().hashCode().absoluteValue.toString()
            val key = TypedStorage.Key<Lambda>()
            val function = Lambda(id, context.jar, entrypoint, context.lambda, routePermissions)

            context.events.register(Events.SQS(id, queueArn, key))
            context.resources.register(key, function)
        }
    }

}
