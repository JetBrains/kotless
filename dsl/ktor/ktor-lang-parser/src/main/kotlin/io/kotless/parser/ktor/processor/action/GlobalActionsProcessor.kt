package io.kotless.parser.ktor.processor.action

import io.kotless.dsl.ktor.KotlessAWS
import io.kotless.dsl.ktor.KotlessAzure
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.SubTypesProcessor
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.psi.*
import io.kotless.permission.AWSPermission
import io.kotless.permission.Permission
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe


internal object GlobalActionsProcessor : SubTypesProcessor<GlobalActionsProcessor.Output>() {
    data class Output(val permissions: Set<Permission>)

    override val klasses = setOf(KotlessAWS::class, KotlessAzure::class)

    override fun mayRun(context: ProcessorContext) = context.output.check(EntrypointProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext): Output {
        val permissions = HashSet<Permission>()

        processClasses(files, binding) { klass, _ ->
            klass.visitNamedFunctions(filter = { func -> func.name == KotlessAzure::prepare.name }) { func ->
                func.visitCallExpressionsWithReferences(
                    filter = { it.getFqName(binding) == "io.ktor.application.ApplicationEvents.subscribe" }, binding = binding, visitOnce = true
                ) { element ->
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
