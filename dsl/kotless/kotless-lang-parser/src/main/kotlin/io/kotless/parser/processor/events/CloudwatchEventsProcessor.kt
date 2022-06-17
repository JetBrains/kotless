package io.kotless.parser.processor.events

import io.kotless.Application.Events
import io.kotless.CloudwatchEventType
import io.kotless.dsl.lang.event.Cloudwatch
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
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.math.absoluteValue

internal object CloudwatchEventsProcessor : AnnotationProcessor<Unit>() {
    override val annotations = setOf(Cloudwatch::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(GlobalActionsProcessor) && context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        val permissions = context.output.get(GlobalActionsProcessor).permissions
        val entrypoint = context.output.get(EntrypointProcessor).entrypoint

        processStaticFunctions(files, binding) { func, entry, _ ->
            require(func, func.fqName != null) { "@Cloudwatch cannot be applied to anonymous function" }
            require(func, func.valueParameters.size == 1) { "@Cloudwatch cannot be applied to ${func.fqName!!.asString()}. It should have only one parameter" }

            val routePermissions = PermissionsProcessor.process(func, binding, context) + permissions

            val id = (entry.getValue(binding, Cloudwatch::id) ?: "").ifBlank { func.fqName!!.asString().hashCode().absoluteValue.toString() }

            val key = TypedStorage.Key<Lambda>()
            val function = Lambda(id, context.jar, entrypoint, context.lambda, routePermissions)

            val cron = entry.getValue(binding, Cloudwatch::cron) ?: error(func, "@Cloudwatch annotation must have `cron` parameter set")

            context.resources.register(key, function)
            context.events.register(Events.Scheduled(id, cron, CloudwatchEventType.General, key))
        }
    }
}
