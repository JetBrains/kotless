package io.kotless.parser.ktor.processor.route

import io.kotless.*
import io.kotless.dsl.ktor.Kotless
import io.kotless.parser.ktor.utils.toMime
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.SubTypesProcessor
import io.kotless.parser.utils.errors.error
import io.kotless.parser.utils.errors.require
import io.kotless.parser.utils.psi.*
import io.kotless.parser.utils.psi.visitor.KtReferenceFollowingVisitor
import io.kotless.parser.utils.reversed
import io.kotless.utils.TypedStorage
import io.ktor.http.ContentType
import io.ktor.http.defaultForFile
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File

internal object StaticRoutesProcessor : SubTypesProcessor<Unit>() {
    private val functions = setOf(
        "io.ktor.http.content.file",
        "io.ktor.http.content.files",
        "io.ktor.http.content.default"
    )

    override val klasses = setOf(Kotless::class)

    override fun mayRun(context: ProcessorContext) = true

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        processClassesOrObjects(files, binding) { klass, _ ->
            klass.visitNamedFunctions(filter = { func -> func.name == Kotless::prepare.name }) { func ->
                func.visitCallExpressionsWithReferences(filter = { it.getFqName(binding) in functions }, binding = binding) { element ->
                    val outer = getStaticPath(element, binding)
                    val base = getStaticRootFolder(element, binding, context)

                    when (element.getFqName(binding)) {
                        "io.ktor.http.content.file" -> {
                            val remotePath = element.getArgument("remotePath", binding).asString(binding)
                            val localPath = element.getArgumentOrNull("localPath", binding)?.asString(binding) ?: remotePath

                            val file = File(base, localPath)
                            val path = URIPath(outer, remotePath)

                            createResource(file, path, context)
                        }
                        "io.ktor.http.content.default" -> {
                            val localPath = element.getArgument("localPath", binding).asString(binding)

                            val file = File(base, localPath)

                            createResource(file, outer, context)
                        }
                        "io.ktor.http.content.files" -> {
                            val folder = File(base, element.getArgument("folder", binding).asString(binding))

                            addStaticFolder(folder, outer, context)
                        }
                    }
                }
            }
        }
    }

    private fun addStaticFolder(folder: File, outer: URIPath, context: ProcessorContext) {
        val allFiles = folder.listFiles() ?: return

        for (file in allFiles) {
            when {
                file.isDirectory -> addStaticFolder(file, URIPath(outer, file.name), context)
                file.isFile -> {
                    val remotePath = file.toRelativeString(folder).toURIPath()
                    val path = URIPath(outer, remotePath)

                    createResource(file, path, context)
                }
            }
        }
    }

    private fun KtReferenceFollowingVisitor.getStaticPath(element: KtElement, binding: BindingContext): URIPath {
        val calls = element.parentsWithReferences(KtCallExpression::class) { it.getFqName(binding) == "io.ktor.http.content.static" }

        val path = calls.mapNotNull {
            it.getArgumentOrNull("remotePath", binding)?.asString(binding)
        }.reversed().toList()

        return URIPath(path)
    }

    private fun KtReferenceFollowingVisitor.getStaticRootFolder(element: KtElement, binding: BindingContext, context: ProcessorContext): File {
        val previous = element.parentsWithReferences(KtCallExpression::class) { it.getFqName(binding) == "io.ktor.http.content.static" }

        return previous.mapNotNull { static ->
            var folder: File? = null
            static.visitCallExpressionsWithReferences(filter = { el -> el.getFqName(binding) == "io.ktor.http.content.static" }, binding = binding) { call ->
                call.visitBinaryExpressions(filter = { (it.operationToken as? KtSingleValueToken)?.value == "=" }) { binary ->
                    if (binary.getChildAt<KtNameReferenceExpression>(0)?.getFqName(binding) != "io.ktor.http.content.staticRootFolder") {
                        return@visitBinaryExpressions
                    }

                    val right = binary.getChildAt<KtCallExpression>(2)
                        ?: error(binary, "staticRootFolder should be assigned with java.io.File(...) constructor")

                    require(binary, right.getFqName(binding) == "java.io.File.<init>") {
                        "staticRootFolder should be assigned with java.io.File(...) constructor"
                    }

                    right.getArgumentByIndexOrNull(0)?.asString(binding)?.let { value ->
                        folder = if (!value.startsWith("/")) {
                            File(context.config.dsl.workDirectory, value)
                        } else {
                            File(value)
                        }
                    }
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
