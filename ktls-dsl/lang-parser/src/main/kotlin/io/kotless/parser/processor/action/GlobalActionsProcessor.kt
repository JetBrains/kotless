package io.kotless.parser.processor.action

import io.kotless.*
import io.kotless.dsl.lang.LambdaInit
import io.kotless.dsl.lang.LambdaWarming
import io.kotless.dsl.lang.http.HttpRequestInterceptor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.SubTypesProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.buildSet
import io.kotless.parser.utils.psi.utils.gatherAllExpressions
import io.kotless.parser.utils.psi.utils.gatherNamedFunctions
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.reflect.KClass


internal object GlobalActionsProcessor : SubTypesProcessor<GlobalActionsProcessor.Output>() {
    data class Output(val permissions: Set<Permission>)

    override val klasses: Set<KClass<*>> = setOf(LambdaInit::class, LambdaWarming::class, HttpRequestInterceptor::class)

    private val functions = mapOf(
        LambdaInit::class to setOf(LambdaInit::init.name),
        LambdaWarming::class to setOf(LambdaWarming::warmup.name),
        HttpRequestInterceptor::class to setOf(HttpRequestInterceptor::intercept.name)
    )

    override fun mayRun(context: ProcessorContext) = true

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext): Output {
        val permissions = HashSet<Permission>()
        processObjects(files, binding) { obj, klass ->
            for (func in functions.getValue(klass)) {
                permissions.addAll(obj.gatherNamedFunctions { it.name == func }.flatMap { it.gatherPermissions(binding) })
            }
        }
        return Output(permissions)
    }

    private fun KtNamedFunction.gatherPermissions(context: BindingContext) = buildSet<Permission> {
        val annotatedExpressions = gatherAllExpressions(context, andSelf = true).filterIsInstance<KtAnnotated>()
        addAll(AwsResource.values().flatMap { PermissionsProcessor.process(context, annotatedExpressions) })
        add(Permission(AwsResource.CloudWatchLogs, PermissionLevel.ReadWrite, setOf("*")))
    }
}
