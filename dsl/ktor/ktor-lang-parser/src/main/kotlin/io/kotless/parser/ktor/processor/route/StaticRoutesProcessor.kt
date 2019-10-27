package io.kotless.parser.ktor.processor.route

import io.kotless.*
import io.kotless.dsl.ktor.Kotless
import io.kotless.parser.ktor.utils.toMime
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.SubTypesProcessor
import io.kotless.parser.utils.psi.*
import io.kotless.utils.TypedStorage
import io.ktor.http.ContentType
import io.ktor.http.defaultForFile
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File

internal object StaticRoutesProcessor : SubTypesProcessor<Unit>() {
    override val klasses = setOf(Kotless::class)

    override fun mayRun(context: ProcessorContext) = true

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        processClasses(files, binding) { klass, _ ->
            klass.gatherNamedFunctions { func -> func.name == Kotless::prepare.name }.forEach { func ->
                func.visit(binding) { element, previous ->
                    if (element is KtCallExpression) {
                        val outer = getStaticPath(previous, binding)

                        when (element.getFqName(binding)) {
                            "io.ktor.http.content.file" -> {
                                val remotePath = element.getArgument("remotePath", binding).asString(binding)
                                val localPath = element.getArgumentOrNull("localPath", binding)?.asString(binding) ?: remotePath

                                val file = File(context.config.dsl.workDirectory, localPath)
                                val path = URIPath(outer, remotePath)

                                createResource(file, path, context)
                            }
                            "io.ktor.http.content.files" -> {
                                val folder = File(context.config.dsl.workDirectory, element.getArgument("folder", binding).asString(binding))

                                val allFiles = folder.listFiles() ?: emptyArray()

                                for (file in allFiles) {
                                    val remotePath = file.toRelativeString(folder).toURIPath()
                                    val path = URIPath(outer, remotePath)

                                    createResource(file, path, context)
                                }
                            }
                        }
                    }
                    true
                }
            }
        }
    }

    private fun getStaticPath(previous: List<KtElement>, binding: BindingContext): URIPath {
        val staticCalls = previous.filter { it is KtCallExpression && it.getFqName(binding) == "io.ktor.http.content.static" }
        val path = staticCalls.map {
            (it as KtCallExpression).getArgumentOrNull("remotePath", binding)?.asString(binding) ?: ""
        }
        return URIPath(path.joinToString(separator = "/"))
    }

    private fun createResource(file: File, path: URIPath, context: ProcessorContext) {
        val key = TypedStorage.Key<StaticResource>()
        val mime = MimeType.forFile(file) ?: ContentType.defaultForFile(file).toMime()
        require(mime != null) { "Unknown mime type for file $file" }

        val resource = StaticResource(context.config.bucket, URIPath("static", path), file, mime)

        context.resources.register(key, resource)
        context.routes.register(Webapp.ApiGateway.StaticRoute(path, key))
    }
}
