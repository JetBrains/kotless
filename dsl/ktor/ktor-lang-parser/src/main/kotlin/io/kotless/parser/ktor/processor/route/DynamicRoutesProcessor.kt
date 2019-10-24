package io.kotless.parser.ktor.processor.route

import io.kotless.*
import io.kotless.dsl.ktor.Kotless
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.SubTypesProcessor
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.psi.*
import io.kotless.utils.TypedStorage
import io.kotless.utils.everyNMinutes
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

internal object DynamicRoutesProcessor : SubTypesProcessor<Unit>() {
    override val klasses = setOf(Kotless::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        val entrypoint = context.output.get(EntrypointProcessor).entrypoint

        processClasses(files, binding) { klass, _ ->
            klass.gatherNamedFunctions { func -> func.name == Kotless::prepare.name }.forEach { func ->
                func.visit(binding) { element, previous ->
                    if (element is KtCallExpression) {
                        val outer = getRoutePath(previous, binding)

                        val res = when (element.getFqName(binding)) {
                            "io.ktor.routing.get" -> {
                                HttpMethod.GET to URIPath(outer, element.getArgument("path", binding).asPath(binding))
                            }
                            "io.ktor.routing.post" -> {
                                HttpMethod.POST to URIPath(outer, element.getArgument("path", binding).asPath(binding))
                            }
                            else -> null
                        }

                        if (res != null) {
                            val permissions = PermissionsProcessor.process(element, binding)

                            val (method, path) = res
                            val name = "${path.parts.joinToString(separator = "_")}_${method.name}"

                            val key = TypedStorage.Key<Lambda>()
                            val function = Lambda(name, context.jar, entrypoint, context.lambda, permissions)

                            context.resources.register(key, function)
                            context.routes.register(Webapp.ApiGateway.DynamicRoute(method, path, key))
                            if (context.config.optimization.autowarm.enable) {
                                context.events.register(Webapp.Events.Scheduled(name, everyNMinutes(context.config.optimization.autowarm.minutes), ScheduledEventType.Autowarm, key))
                            }
                        }
                    }
                    true
                }
            }
        }
    }

    private fun getRoutePath(previous: List<KtElement>, binding: BindingContext): URIPath {
        val routeCalls = previous.filter { it is KtCallExpression && it.getFqName(binding) == "io.ktor.routing.route" }
        val path = routeCalls.map {
            (it as KtCallExpression).getArgumentOrNull("path", binding)?.asString(binding) ?: ""
        }
        return URIPath(path.joinToString(separator = "/"))
    }
}
