package io.kotless.parser.processor.events

import io.kotless.Lambda
import io.kotless.ScheduledEventType
import io.kotless.Webapp.Events
import io.kotless.dsl.lang.event.Scheduled
import io.kotless.parser.processor.AnnotationProcessor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.action.GlobalActionsProcessor
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.errors.error
import io.kotless.parser.utils.errors.require
import io.kotless.parser.utils.psi.annotation.getValue
import io.kotless.utils.TypedStorage
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.math.absoluteValue

internal object ScheduledEventsProcessor : AnnotationProcessor<Unit>() {
    override val annotations = setOf(Scheduled::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(GlobalActionsProcessor) && context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        val permissions = context.output.get(GlobalActionsProcessor).permissions
        val entrypoint = context.output.get(EntrypointProcessor).entrypoint

        processStaticFunctions(files, binding) { func, entry, _ ->
            require(func, func.fqName != null) { "@Scheduled cannot be applied to anonymous function" }
            require(func, func.valueParameters.isEmpty()) { "@Scheduled cannot be applied to ${func.fqName!!.asString()} since it has parameters" }

            val routePermissions = PermissionsProcessor.process(func, binding) + permissions

            val id = (entry.getValue(binding, Scheduled::id) ?: "").ifBlank { func.fqName!!.asString().hashCode().absoluteValue.toString() }

            val key = TypedStorage.Key<Lambda>()
            val function = Lambda(id, context.jar, entrypoint, context.lambda, routePermissions)

            val cron = entry.getValue(binding, Scheduled::cron) ?: error(func, "@Scheduled annotation must have `cron` parameter set")

            context.resources.register(key, function)
            context.events.register(Events.Scheduled(id, cron, ScheduledEventType.General, key))
        }
    }
}
