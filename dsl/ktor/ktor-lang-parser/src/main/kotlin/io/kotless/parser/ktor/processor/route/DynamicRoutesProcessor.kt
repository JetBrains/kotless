package io.kotless.parser.ktor.processor.route

import io.kotless.*
import io.kotless.dsl.ktor.Kotless
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.SubTypesProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.psi.utils.*
import io.kotless.utils.TypedStorage
import io.kotless.utils.everyNMinutes
import org.jetbrains.kotlin.js.descriptorUtils.nameIfStandardType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.constants.TypedCompileTimeConstant
import org.jetbrains.kotlin.resolve.constants.evaluate.ConstantExpressionEvaluator
import org.slf4j.LoggerFactory

internal object DynamicRoutesProcessor : SubTypesProcessor<Unit>() {
    private val logger = LoggerFactory.getLogger(DynamicRoutesProcessor::class.java)

    override val klasses = setOf(Kotless::class)

    override fun mayRun(context: ProcessorContext) = true

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        logger.info("Processing")
        processClasses(files, binding) { klass, _ ->
            klass.gatherNamedFunctions { func -> func.name == Kotless::prepare.name }.forEach {
                for (expr in it.gatherAllExpressions(binding).filterIsInstance<KtCallExpression>()) {
                    val res = when (expr.getFqName(binding)) {
                        "io.ktor.routing.get" -> {
                            HttpMethod.GET to getPath(expr, binding, it, klass)
                        }
                        "io.ktor.routing.post" -> {
                            HttpMethod.POST to getPath(expr, binding, it, klass)
                        }
                        else -> null
                    } ?: continue

                    val permissions = PermissionsProcessor.process(expr, binding)

                    val (method, path) = res
                    val name = "${path.parts.joinToString(separator = "_")}_${method.name}"

                    val key = TypedStorage.Key<Lambda>()
                    //TODO-tanvd fix
                    val function = Lambda(name, context.jar, Lambda.Entrypoint("io.kotless.examples.page.Main", emptySet()), context.lambda, permissions)

                    context.resources.register(key, function)
                    context.routes.register(Webapp.ApiGateway.DynamicRoute(method, path, key))
                    if (context.config.optimization.autowarm.enable) {
                        context.events.register(Webapp.Events.Scheduled(name, everyNMinutes(context.config.optimization.autowarm.minutes), ScheduledEventType.Autowarm, key))
                    }
                }
            }
        }
    }

    private fun getPath(expr: KtCallExpression, binding: BindingContext, func: KtNamedFunction, klass: KtClass): URIPath {
        require(expr.valueArguments.size == 2) {
            "Error in function ${func.fqName}, class ${klass.fqName}: all routing functions should have path argument"
        }
        val arg = expr.valueArguments.first().getArgumentExpression()
        require(arg != null) {
            "Error in function ${func.fqName}, class ${klass.fqName}: routing path should be compile-time constant string"
        }
        val value = ConstantExpressionEvaluator.getConstant(arg, binding)
        require(value is TypedCompileTimeConstant && value.type.nameIfStandardType?.identifier == "String") {
            "Error in function ${func.fqName}, class ${klass.fqName}: routing path should be compile-time constant string"
        }
        val path = value.constantValue.value as String

        return URIPath(path.split("/"))
    }
}
