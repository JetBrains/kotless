package io.kotless.parser.processor.action

import io.kotless.dsl.lang.LambdaInit
import io.kotless.dsl.lang.LambdaWarming
import io.kotless.dsl.lang.http.HttpRequestInterceptor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.SubTypesProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.psi.visitNamedFunctions
import io.kotless.permission.Permission
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.reflect.KClass


internal object GlobalActionsProcessor : SubTypesProcessor<GlobalActionsProcessor.Output>() {
    data class Output(val permissions: Set<Permission>)

    private val functions = mapOf(
        LambdaInit::class to LambdaInit::init.name,
        LambdaWarming::class to LambdaWarming::warmup.name,
        HttpRequestInterceptor::class to HttpRequestInterceptor::intercept.name
    )

    override val klasses: Set<KClass<*>> = functions.keys

    override fun mayRun(context: ProcessorContext) = true

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext): Output {
        val permissions = HashSet<Permission>()

        processObjects(files, binding) { obj, klass ->
            obj.visitNamedFunctions(filter = { it.name == functions.getValue(klass) }) {
                permissions += PermissionsProcessor.process(it, binding)
            }
        }

        return Output(permissions)
    }
}
