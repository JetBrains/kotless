package io.kotless.parser.spring.processor.route

import io.kotless.*
import io.kotless.Webapp.ApiGateway
import io.kotless.Webapp.Events
import io.kotless.parser.processor.AnnotationProcessor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.psi.annotation.*
import io.kotless.parser.utils.psi.visitNamedFunctions
import io.kotless.utils.TypedStorage
import io.kotless.utils.everyNMinutes
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
import org.springframework.web.bind.annotation.*

internal object DynamicRoutesProcessor : AnnotationProcessor<Unit>() {
    override val annotations = setOf(RestController::class)

    private val methodAnnotations = setOf(GetMapping::class, PostMapping::class, PutMapping::class, PatchMapping::class, DeleteMapping::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        processClassesOrObjects(files, binding) { classOrObj, _, _ ->
            visitMethodMappings(classOrObj, binding, context)
        }
    }

    private fun visitMethodMappings(element: KtElement, binding: BindingContext, context: ProcessorContext) {
        element.visitNamedFunctions(filter = { el -> el.isAnnotatedWith(binding, methodAnnotations) }) { el ->
            val previous = el.parents.filterIsInstance(KtElement::class.java)
            for (klass in methodAnnotations) {
                el.getAnnotations(binding, klass).forEach { entry ->
                    val entrypoint = context.output.get(EntrypointProcessor).entrypoint
                    val routePermissions = PermissionsProcessor.process(el, binding)

                    val name = el.fqName!!.asString()

                    val key = TypedStorage.Key<Lambda>()
                    val function = Lambda(name, context.jar, entrypoint, context.lambda, routePermissions)

                    val (routeType, pathProperty) = when (klass) {
                        GetMapping::class -> HttpMethod.GET to GetMapping::value
                        PostMapping::class -> HttpMethod.POST to PostMapping::value
                        PutMapping::class -> HttpMethod.PUT to PutMapping::value
                        PatchMapping::class -> HttpMethod.PATCH to PatchMapping::value
                        DeleteMapping::class -> HttpMethod.DELETE to DeleteMapping::value
                        else -> error("Not supported class $entry")
                    }

                    val path = entry.getURIPaths(binding, pathProperty)?.singleOrNull()

                    if (path != null) {
                        context.resources.register(key, function)
                        context.routes.register(ApiGateway.DynamicRoute(routeType, URIPath(getRoutePath(previous, binding), path), key))

                        if (context.config.optimization.autowarm.enable) {
                            context.events.register(Events.Scheduled(name, everyNMinutes(context.config.optimization.autowarm.minutes),
                                ScheduledEventType.Autowarm, key))
                        }
                    }
                }
            }
        }
    }

    private fun getRoutePath(previous: Sequence<KtElement>, binding: BindingContext): URIPath {
        val routeCalls = previous.filter { it is KtClassOrObject && it.isAnnotatedWith<RequestMapping>(binding) }
        val path = routeCalls.mapNotNull {
            (it as KtClassOrObject).getAnnotation<RequestMapping>(binding).getURIPaths(binding, RequestMapping::value)?.singleOrNull()
        }
        return URIPath(path.joinToString(separator = "/"))
    }
}
