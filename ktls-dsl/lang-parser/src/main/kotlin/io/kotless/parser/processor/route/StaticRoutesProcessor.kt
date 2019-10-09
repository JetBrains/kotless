package io.kotless.parser.processor.route

import io.kotless.*
import io.kotless.dsl.lang.http.StaticGet
import io.kotless.parser.processor.AnnotationProcessor
import io.kotless.parser.ParserContext
import io.kotless.parser.utils.psi.annotation.getEnumValue
import io.kotless.parser.utils.psi.annotation.getUriPath
import org.jetbrains.kotlin.codegen.kotlinType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import java.io.File

internal object StaticRoutesProcessor : AnnotationProcessor() {
    override fun mayRun(context: ParserContext) = true

    override val annotations = setOf(StaticGet::class)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ParserContext) {
        processStaticVariables(files, binding) { variable, entry ->
            val path = entry.getUriPath(binding, StaticGet::path)!!
            val mime = entry.getEnumValue(binding, StaticGet::mime)!!

            require(variable.kotlinType(binding)!!.constructor.declarationDescriptor!!.fqNameSafe.toString() == File::class.qualifiedName) {
                "Variable ${variable.fqName.toString()} is @StaticGet, but is not created via File(\"...\")"
            }

            require(variable.initializer is KtCallExpression) { "Variable ${variable.fqName.toString()} is @StaticGet, but is not created via File(\"...\")" }
            val arguments = (variable.initializer as KtCallExpression).valueArguments
            require(arguments.size == 1) { "Variable ${variable.fqName.toString()} is @StaticGet, but is not created via File(\"...\")" }

            val file = File(context.workDirectory, arguments.single().text.trim('"'))
            val resource = StaticResource(context.bucket, URIPath("static", path), file, mime)

            context.resources.register(resource)
            context.routes.register(Webapp.ApiGateway.StaticRoute(path, resource))
        }
    }
}
