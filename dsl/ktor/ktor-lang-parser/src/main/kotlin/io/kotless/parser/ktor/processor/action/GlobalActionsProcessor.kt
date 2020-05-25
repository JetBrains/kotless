package io.kotless.parser.ktor.processor.action

import io.kotless.Permission
import io.kotless.dsl.ktor.Kotless
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.SubTypesProcessor
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.psi.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe


internal object GlobalActionsProcessor : SubTypesProcessor<GlobalActionsProcessor.Output>() {
    data class Output(val permissions: Set<Permission>)

    override val klasses = setOf(Kotless::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext): Output {
        val permissions = HashSet<Permission>()

        processClasses(files, binding) { klass, _ ->
            klass.visitNamedFunctions(filter = { func -> func.name == Kotless::prepare.name }) { func ->
                func.visitCallExpressions(filter = { it.getFqName(binding) == "io.ktor.application.ApplicationEvents.subscribe" }) { element ->
                    val event = element.getArgument("definition", binding)
                    if (event.asReferencedDescriptorOrNull(binding)?.fqNameSafe?.asString() == "io.kotless.dsl.ktor.lang.LambdaWarming") {
                        permissions += PermissionsProcessor.process(element, binding)
                    }
                }
            }
        }

        return Output(permissions)
    }
}
