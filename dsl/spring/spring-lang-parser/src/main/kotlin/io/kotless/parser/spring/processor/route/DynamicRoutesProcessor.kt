package io.kotless.parser.spring.processor.route

import io.kotless.Application.API
import io.kotless.Application.Events
import io.kotless.InternalAPI
import io.kotless.ScheduledEventType
import io.kotless.parser.processor.AnnotationProcessor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.psi.visitNamedFunctions
import io.kotless.resource.Lambda
import io.kotless.utils.TypedStorage
import io.kotless.utils.everyNMinutes
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.springframework.web.bind.annotation.RestController

@OptIn(InternalAPI::class)
internal object DynamicRoutesProcessor : AnnotationProcessor<Unit>() {
    override val annotations = setOf(RestController::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        processClassesOrObjects(files, binding) { classOrObj, _, _ ->
            classOrObj.visitNamedFunctions(filter = { SpringAnnotationUtils.isHTTPHandler(binding, it) }) { el ->
                val entrypoint = context.output.get(EntrypointProcessor).entrypoint

                for (method in SpringAnnotationUtils.getMethods(binding, el)) {
                    val path = SpringAnnotationUtils.getRoutePath(binding, el)
                    val permissions = PermissionsProcessor.process(el, binding, context)
                    val name = el.fqName!!.asString() + "_" + method.name

                    val key = TypedStorage.Key<Lambda>()
                    val function = Lambda(name, context.jar, entrypoint, context.lambda, permissions)

                    context.resources.register(key, function)
                    context.routes.register(API.DynamicRoute(method, path, key))

                    if (context.config.optimization.autoWarm.enable) {
                        context.events.register(
                            Events.Scheduled(name, everyNMinutes(context.config.optimization.autoWarm.minutes), ScheduledEventType.Autowarm, key)
                        )
                    }
                }
            }
        }
    }
}
