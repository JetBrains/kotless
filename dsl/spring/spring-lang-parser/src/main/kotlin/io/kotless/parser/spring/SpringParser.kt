package io.kotless.parser.spring

import io.kotless.MimeType
import io.kotless.StaticResource
import io.kotless.URIPath
import io.kotless.Webapp
import io.kotless.parser.Parser
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.spring.processor.route.DynamicRoutesProcessor
import io.kotless.utils.TypedStorage
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * SpringParser parses Kotlin code with Kotlin embeddable compiler looking
 * for Kotless DSL constructs.
 *
 * The result of parsing is a number of Lambdas and StaticResources and associated
 * with them Dynamic and Static routes
 */
object SpringParser : Parser(setOf(EntrypointProcessor, DynamicRoutesProcessor)) {
    override fun processResources(resources: Set<File>, context: ProcessorContext) {
        val filtered = resources.filter { it.toPath().contains(Paths.get("static")) }.map { it.toPath() }.takeIf { it.isNotEmpty() } ?: return
        val static = filtered.first().getParent("static") ?: return

        for (file in filtered) {
            val path = static.relativize(file)
            createResource(file.toFile(), URIPath(path.map { it.toString() }), context)
        }
    }

    private fun Path.getParent(name: String): Path? {
        var result = this.parent
        while (!result.endsWith(name) && result.toList().isNotEmpty()) {
            result = result.parent
        }

        return result.takeIf { it.toList().isNotEmpty() }
    }


    private fun createResource(file: File, path: URIPath, context: ProcessorContext) {
        val key = TypedStorage.Key<StaticResource>()
        val mime = MimeType.forFile(file)
        require(mime != null) { "Unknown mime type for file $file" }

        val resource = StaticResource(context.config.bucket, URIPath("static", path), file, mime)

        context.resources.register(key, resource)
        context.routes.register(Webapp.ApiGateway.StaticRoute(path, key))
    }
}
