package io.kotless.parser.ktor.processor.route

import io.kotless.*
import io.kotless.Application.Events
import io.kotless.dsl.ktor.KotlessAWS
import io.kotless.dsl.ktor.KotlessAzure
import io.kotless.parser.ktor.processor.action.GlobalActionsProcessor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.SubTypesProcessor
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.errors.error
import io.kotless.parser.utils.psi.*
import io.kotless.parser.utils.psi.visitor.KtReferenceFollowingVisitor
import io.kotless.parser.utils.reversed
import io.kotless.resource.Lambda
import io.kotless.utils.TypedStorage
import io.kotless.utils.everyNMinutes
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.math.absoluteValue

internal object DynamicRoutesProcessor : SubTypesProcessor<Unit>() {
    private val functions = mapOf(
        "io.ktor.routing.get" to HttpMethod.GET,
        "io.ktor.routing.post" to HttpMethod.POST,
        "io.ktor.routing.put" to HttpMethod.PUT,
        "io.ktor.routing.patch" to HttpMethod.PATCH,
        "io.ktor.routing.delete" to HttpMethod.DELETE,
        "io.ktor.routing.head" to HttpMethod.HEAD,
        "io.ktor.routing.options" to HttpMethod.OPTIONS
    )

    private val events = mapOf(
        "io.kotless.dsl.ktor.KotlessAWS.Companion.s3" to AwsResource.S3
    )

    override val klasses = setOf(KotlessAWS::class, KotlessAzure::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(GlobalActionsProcessor) && context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        val globalPermissions = context.output.get(GlobalActionsProcessor).permissions
        val entrypoint = context.output.get(EntrypointProcessor).entrypoint
        assert(KotlessAWS::prepare.name == KotlessAzure::prepare.name) {
            "Internal Kotless error: Ktor entrypoint classes should have prepare function"
        }

        processClasses(files, binding) { klass, _ ->
            klass.visitNamedFunctions(filter = { func -> func.name == KotlessAWS::prepare.name }) { func ->
                func.visitCallExpressionsWithReferences(binding = binding, filter = { it.getFqName(binding) in functions.keys }) { element ->
                    val outer = getDynamicPath(element, binding)

                    val method = functions[element.getFqName(binding)] ?: error(element, "Unknown Ktor HTTP handler definition")

                    val permissions = PermissionsProcessor.process(element, binding, context) + globalPermissions

                    val path = URIPath(outer, element.getArgument("path", binding).asPath(binding))
                    val name = "${path.parts.joinToString(separator = "_")}_${method.name}"

                    val key = TypedStorage.Key<Lambda>()
                    val function = Lambda(name, context.jar, entrypoint, context.lambda, permissions)

                    context.resources.register(key, function)
                    context.routes.register(Application.API.DynamicRoute(method, path, key))

                    if (context.config.optimization.autoWarm.enable) {
                        context.events.register(
                            Events.Scheduled(name, everyNMinutes(context.config.optimization.autoWarm.minutes), ScheduledEventType.Autowarm, key)
                        )
                    }
                }
                func.visitCallExpressionsWithReferences(binding = binding, filter = { it.getFqName(binding) in events.keys }) { element ->
                    val permissions = PermissionsProcessor.process(element, binding, context) + globalPermissions

                    val name = func.fqName!!.asString().hashCode().absoluteValue.toString()

                    val key = TypedStorage.Key<Lambda>()
                    val function = Lambda(name, context.jar, entrypoint, context.lambda, permissions)

                    context.resources.register(key, function)

                    val bucket = element.getArgument("bucket", binding).asString(binding)
                    val eventType = element.getArgument("event", binding).asString(binding)

                    context.events.register(
                        Events.S3(
                            func.fqName!!.asString().hashCode().absoluteValue.toString(),
                            bucket, listOf(eventType),
                            key
                        )
                    )

                    if (context.config.optimization.autoWarm.enable) {
                        context.events.register(
                            Events.Scheduled(name, everyNMinutes(context.config.optimization.autoWarm.minutes), ScheduledEventType.Autowarm, key)
                        )
                    }
                }
            }
        }
    }

    private fun KtReferenceFollowingVisitor.getDynamicPath(element: KtElement, binding: BindingContext): URIPath {
        val calls = element.parentsWithReferences(KtCallExpression::class) { it.getFqName(binding) == "io.ktor.routing.route" }

        val path = calls.mapNotNull {
            it.getArgumentOrNull("path", binding)?.asString(binding)
        }.reversed().toList()

        return URIPath(path)
    }
}
