package io.kotless.parser.processor.events

import io.kotless.Application.Events
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

internal object S3EventsProcessor : AnnotationProcessor<Unit>() {
    override val annotations = setOf(S3Event::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(GlobalActionsProcessor) && context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        val permissions = context.output.get(GlobalActionsProcessor).permissions
        val entrypoint = context.output.get(EntrypointProcessor).entrypoint
        val events = mutableListOf<Events.S3>()

        processStaticFunctions(files, binding) { func, entry, _ ->
            require(func, func.fqName != null) { "@S3Event cannot be applied to anonymous function" }
            require(func, func.valueParameters.size == 1) { "@S3Event cannot be applied to ${func.fqName!!.asString()}. It should have only one parameter" }

            val routePermissions = PermissionsProcessor.process(func, binding, context) + permissions

            val bucket = entry.getValue(binding, S3Event::bucket) ?: error(func, "@S3Event annotation must have `bucket` parameter set")
            val id = func.fqName!!.asString().hashCode().absoluteValue.toString()
            val key = TypedStorage.Key<Lambda>()
            val function = Lambda(id, context.jar, entrypoint, context.lambda, routePermissions)

            val type = entry.getValue(binding, S3Event::type) ?: error(func, "@S3Event annotation must have `type` parameter set")
            events.add(Events.S3(id, bucket, listOf(type), key))
            context.resources.register(key, function)
        }
        events.groupBy { it.bucket }.map { (_, value) -> Events.S3(value.first().id, value.first().bucket, value.flatMap { it.types }, value.first().lambda) }
            .forEach {
                context.events.register(it)
            }
    }

    fun KtProperty.getTypeFqName(context: BindingContext): FqName? {
        return (context.get(BindingContext.DECLARATION_TO_DESCRIPTOR, this) as PropertyDescriptor).type.constructor.declarationDescriptor?.fqNameSafe
    }


}
