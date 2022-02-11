package io.kotless.parser.processor.events

import io.kotless.Application.Events
import io.kotless.dsl.lang.event.CustomAwsEvent
import io.kotless.dsl.lang.event.S3Event
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

internal object CustomAwsEventsProcessor : AnnotationProcessor<Unit>() {
    override val annotations = setOf(CustomAwsEvent::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(GlobalActionsProcessor) && context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        val permissions = context.output.get(GlobalActionsProcessor).permissions
        val entrypoint = context.output.get(EntrypointProcessor).entrypoint

        processStaticFunctions(files, binding) { func, entry, _ ->
            require(func, func.fqName != null) { "@CustomAwsEvent cannot be applied to anonymous function" }
            require(func, func.valueParameters.size == 1) { "@CustomAwsEvent cannot be applied to ${func.fqName!!.asString()} since it has unknown parameters" }

            val routePermissions = PermissionsProcessor.process(func, binding, context) + permissions

            val path = entry.getValue(binding, CustomAwsEvent::path) ?: error(func, "@CustomAwsEvent annotation must have `path` parameter set")
            val id = func.fqName!!.asString().hashCode().absoluteValue.toString()
            val key = TypedStorage.Key<Lambda>()
            val function = Lambda(id, context.jar, entrypoint, context.lambda, routePermissions)

            val event = Events.CustomAwsEvent(id, path, key)
            context.resources.register(key, function)
            context.events.register(event)
        }

    }

}
