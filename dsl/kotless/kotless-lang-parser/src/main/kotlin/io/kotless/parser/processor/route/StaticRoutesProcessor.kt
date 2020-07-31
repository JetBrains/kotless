package io.kotless.parser.processor.route

import io.kotless.StaticResource
import io.kotless.URIPath
import io.kotless.Webapp
import io.kotless.dsl.lang.http.StaticGet
import io.kotless.parser.processor.AnnotationProcessor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.utils.errors.error
import io.kotless.parser.utils.errors.require
import io.kotless.parser.utils.psi.annotation.getEnumValue
import io.kotless.parser.utils.psi.annotation.getURIPath
import io.kotless.parser.utils.psi.getTypeFqName
import io.kotless.utils.TypedStorage
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File

internal object StaticRoutesProcessor : AnnotationProcessor<Unit>() {
    override fun mayRun(context: ProcessorContext) = true

    override val annotations = setOf(StaticGet::class)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        processStaticVariables(files, binding) { variable, entry, _ ->
            val path = entry.getURIPath(binding, StaticGet::path)
                ?: error(variable, "For @StaticGet annotation `path` parameter is required")
            val mime = entry.getEnumValue(binding, StaticGet::mime)
                ?: error(variable, "For @StaticGet annotation `path` parameter is required")

            require(variable, variable.getTypeFqName(binding).toString() == File::class.qualifiedName) {
                "Variable annotated with @StaticGet should have type java.io.File"
            }
            require(variable, variable.initializer is KtCallExpression) {
                "Variable annotated with @StaticGet should be created via File(\"...\") constructor"
            }

            val arguments = (variable.initializer as KtCallExpression).valueArguments

            require(variable, arguments.size == 1) {
                "Variable annotated with @StaticGet should be created via File(\"...\") constructor with one argument"
            }

            val file = File(context.config.dsl.staticsRoot, arguments.single().text.trim('"'))

            val key = TypedStorage.Key<StaticResource>()
            val resource = StaticResource(URIPath("static", path), file, mime)

            context.resources.register(key, resource)
            context.routes.register(Webapp.ApiGateway.StaticRoute(path, key))
        }
    }
}
