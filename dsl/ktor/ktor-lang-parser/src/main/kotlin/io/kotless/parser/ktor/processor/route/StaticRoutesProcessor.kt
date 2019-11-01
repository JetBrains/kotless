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
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File

internal object StaticRoutesProcessor : SubTypesProcessor<Unit>() {
    private val funcs = setOf("io.ktor.http.content.file", "io.ktor.http.content.files")

    override val klasses = setOf(Kotless::class)

    override fun mayRun(context: ProcessorContext) = true

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        processClasses(files, binding) { klass, _ ->
            klass.gatherNamedFunctions { func -> func.name == Kotless::prepare.name }.forEach { func ->
                func.visit(binding) { element, previous ->
                    if (element is KtCallExpression && element.getFqName(binding) in funcs) {
                        val outer = getStaticPath(previous, binding)
                        val base = getBaseFolder(previous, binding, context)

                        when (element.getFqName(binding)) {
                            "io.ktor.http.content.file" -> {
                                val remotePath = element.getArgument("remotePath", binding).asString(binding)
                                val localPath = element.getArgumentOrNull("localPath", binding)?.asString(binding) ?: remotePath

                                val file = File(base, localPath)
                                val path = URIPath(outer, remotePath)

                                createResource(file, path, context)
                            }
                            "io.ktor.http.content.files" -> {
                                val folder = File(base, element.getArgument("folder", binding).asString(binding))

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

    private fun getBaseFolder(previous: List<KtElement>, binding: BindingContext, context: ProcessorContext): File {
        return previous.asSequence().filter { it is KtCallExpression && it.getFqName(binding) == "io.ktor.http.content.static" }.mapNotNull {
            var folder: File? = null
            it.visit(binding) { el, _ ->
                if (el is KtCallExpression && el.getFqName(binding) == "io.ktor.http.content.static" && el != it) {
                    false
                } else {
                    if (el is KtBinaryExpression && (el.operationToken as? KtSingleValueToken)?.value == "=") {
                        if (el.getChildAt<KtNameReferenceExpression>(0)?.getFqName(binding) == "io.ktor.http.content.staticRootFolder") {
                            val right = el.getChildAt<KtCallExpression>(2)

                            require(right?.getFqName(binding) == "java.io.File.<init>") {
                                el.withExceptionHeader("staticRootFolder should be assigned with java.io.File(...) constructor")
                            }

                            right!!.getArgumentByIndexOrNull(0)?.asString(binding)?.let { value ->
                                folder = if (!value.startsWith("/")) {
                                    File(context.config.dsl.workDirectory, value)
                                } else {
                                    File(value)
                                }
                            }
                        }
                    }
                    true
                }
            }
            folder
        }.firstOrNull() ?: context.config.dsl.workDirectory
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
