package io.kotless.parser.processor.route

import io.kotless.Application.API
import io.kotless.Application.Events
import io.kotless.HttpMethod
import io.kotless.ScheduledEventType
import io.kotless.dsl.config.KotlessAppConfig
import io.kotless.dsl.lang.http.*
import io.kotless.parser.processor.AnnotationProcessor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.action.GlobalActionsProcessor
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.errors.error
import io.kotless.parser.utils.psi.annotation.getURIPath
import io.kotless.resource.Lambda
import io.kotless.utils.TypedStorage
import io.kotless.utils.everyNMinutes
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext

internal object DynamicRoutesProcessor : AnnotationProcessor<Unit>() {
    private val methodAnnotations = mapOf(
        Get::class to HttpMethod.GET,
        Post::class to HttpMethod.POST,
        Put::class to HttpMethod.PUT,
        Patch::class to HttpMethod.PATCH,
        Delete::class to HttpMethod.DELETE,
        Head::class to HttpMethod.HEAD,
        Options::class to HttpMethod.OPTIONS
    )

    override val annotations = setOf(Get::class, Post::class, Put::class, Patch::class, Delete::class, Head::class, Options::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(GlobalActionsProcessor) && context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        val globalPermissions = context.output.get(GlobalActionsProcessor).permissions
        val entrypoint = context.output.get(EntrypointProcessor).entrypoint

        processStaticFunctions(files, binding) { func, entry, klass ->
            val permissions = PermissionsProcessor.process(func, binding) + globalPermissions

            val name = prepareFunctionName(func, KotlessAppConfig.packages(context.lambda.environment.getValue(KotlessAppConfig.PACKAGE_ENV_NAME)))

            val key = TypedStorage.Key<Lambda>()
            val function = Lambda(name, context.jar, entrypoint, context.lambda, permissions)

            val path = entry.getURIPath(binding, "path") ?: error(func, "For Kotless HTTP annotation `path` property is required")
            val method = methodAnnotations[klass] ?: error(func, "Unknown Kotless HTTP annotation")

            context.resources.register(key, function)
            context.routes.register(API.DynamicRoute(method, path, key))

            if (context.config.optimization.autoWarm.enable) {
                context.events.register(Events.Scheduled(name, everyNMinutes(context.config.optimization.autoWarm.minutes), ScheduledEventType.Autowarm, key))
            }
        }
    }

    private fun prepareFunctionName(route: KtNamedFunction, packages: Set<String>): String {
        val fqName = route.fqName?.asString() ?: error(route, "Kotless HTTP annotation cannot be used on anonymous function")

        val pkg = packages.find { fqName.startsWith("$it.") }
        return if (pkg != null) {
            fqName.drop("$pkg.".length)
        } else {
            fqName
        }
    }
}
