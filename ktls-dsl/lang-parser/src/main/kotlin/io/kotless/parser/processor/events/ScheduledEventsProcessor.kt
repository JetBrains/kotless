package io.kotless.parser.processor.events

import io.kotless.*
import io.kotless.dsl.kotlessLambdaEntrypoint
import io.kotless.dsl.lang.event.Scheduled
import io.kotless.parser.processor.AnnotationProcessor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.action.GlobalActionsProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.buildSet
import io.kotless.parser.utils.psi.annotation.getValue
import io.kotless.parser.utils.psi.utils.gatherAllExpressions
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.math.absoluteValue

internal object ScheduledEventsProcessor : AnnotationProcessor<Unit>() {
    override val annotations = setOf(Scheduled::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(GlobalActionsProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        val permissions = context.output.get(GlobalActionsProcessor).permissions

        processFunctions(files, binding) { func, entry, _ ->
            val routePermissions = gatherPermissionsForRoute(binding, func) + permissions

            val id = "scheduled-${func.name.hashCode().absoluteValue}"

            //TODO-tanvd Fix -- should use ID from fully qualified name
            val function = Lambda(id, context.jar, Lambda.Entrypoint(kotlessLambdaEntrypoint, emptySet()), context.lambda, routePermissions)

            val cron = entry.getValue(binding, Scheduled::cron)!!

            context.resources.register(function)
            context.events.register(Webapp.Events.ScheduledEvent(id, cron, function))
        }
    }

    private fun gatherPermissionsForRoute(context: BindingContext, function: KtNamedFunction) = buildSet<Permission> {
        val annotatedExpressions = function.gatherAllExpressions(context, andSelf = true).filterIsInstance<KtAnnotated>()
        addAll(AwsResource.values().flatMap { PermissionsProcessor.process(context, annotatedExpressions) })
        add(Permission(AwsResource.CloudWatchLogs, PermissionLevel.ReadWrite, setOf("*")))
    }
}
