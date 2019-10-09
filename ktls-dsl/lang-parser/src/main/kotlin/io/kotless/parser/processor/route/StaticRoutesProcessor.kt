package io.kotless.parser.processor.route

import io.kotless.*
import io.kotless.dsl.lang.http.StaticGet
import io.kotless.parser.processor.AnnotationProcessor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.utils.psi.annotation.getEnumValue
import io.kotless.parser.utils.psi.annotation.getURIPath
import io.kotless.parser.utils.psi.utils.getTypeFqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File

internal object StaticRoutesProcessor : AnnotationProcessor<Unit>() {
    override fun mayRun(context: ProcessorContext) = true

    override val annotations = setOf(StaticGet::class)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        processStaticVariables(files, binding) { variable, entry, _ ->
            val path = entry.getURIPath(binding, StaticGet::path)!!
            val mime = entry.getEnumValue(binding, StaticGet::mime)!!

            require(variable.getTypeFqName(binding).toString() == File::class.qualifiedName) {
                "Variable ${variable.fqName.toString()} is @StaticGet, but its type is not java.io.File"
            }
            require(variable.initializer is KtCallExpression) {
                "Variable ${variable.fqName.toString()} is @StaticGet, but is not created via File(\"...\")"
            }

            val arguments = (variable.initializer as KtCallExpression).valueArguments
            require(arguments.size == 1) { "Variable ${variable.fqName.toString()} is @StaticGet, but is not created via File(\"...\")" }

            val file = File(context.config.workDirectory, arguments.single().text.trim('"'))
            val resource = StaticResource(context.config.bucket, URIPath("static", path), file, mime)

            context.resources.register(resource)
            context.routes.register(Webapp.ApiGateway.StaticRoute(path, resource))
        }
    }
}
