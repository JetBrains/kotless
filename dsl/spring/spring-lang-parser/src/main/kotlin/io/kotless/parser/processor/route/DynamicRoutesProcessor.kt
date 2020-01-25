package io.kotless.parser.processor.route

import io.kotless.*
import io.kotless.Webapp.ApiGateway
import io.kotless.Webapp.Events
import io.kotless.parser.processor.AnnotationProcessor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.psi.annotation.getURIPaths
import io.kotless.utils.TypedStorage
import io.kotless.utils.everyNMinutes
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.springframework.web.bind.annotation.*

internal object DynamicRoutesProcessor : AnnotationProcessor<Unit>() {
    override val annotations = setOf(GetMapping::class, PostMapping::class, PutMapping::class, PatchMapping::class, DeleteMapping::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        val entrypoint = context.output.get(EntrypointProcessor).entrypoint

        processFunctions(files, binding) { func, entry, klass ->
            val routePermissions = PermissionsProcessor.process(func, binding)

            val name = func.fqName!!.asString()

            val key = TypedStorage.Key<Lambda>()
            val function = Lambda(name, context.jar, entrypoint, context.lambda, routePermissions)

            val (routeType, pathProperty) = when (klass) {
                GetMapping::class -> HttpMethod.GET to GetMapping::path
                PostMapping::class -> HttpMethod.POST to PostMapping::path
                PutMapping::class -> HttpMethod.PUT to PutMapping::path
                PatchMapping::class -> HttpMethod.PATCH to PatchMapping::path
                DeleteMapping::class -> HttpMethod.DELETE to DeleteMapping::path
                else -> error("Not supported class $entry")
            }

            val paths = entry.getURIPaths(binding, pathProperty)!!

            for (path in paths) {
                context.resources.register(key, function)
                context.routes.register(ApiGateway.DynamicRoute(routeType, path, key))
            }

            if (context.config.optimization.autowarm.enable) {
                context.events.register(Events.Scheduled(name, everyNMinutes(context.config.optimization.autowarm.minutes), ScheduledEventType.Autowarm, key))
            }
        }
    }
}
