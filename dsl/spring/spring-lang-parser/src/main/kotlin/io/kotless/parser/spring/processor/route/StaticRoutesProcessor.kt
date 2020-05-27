package io.kotless.parser.spring.processor.route

import io.kotless.MimeType
import io.kotless.StaticResource
import io.kotless.URIPath
import io.kotless.Webapp
import io.kotless.parser.processor.ProcessorContext
import io.kotless.utils.TypedStorage
import java.io.File
import java.nio.file.Path

object StaticRoutesProcessor {
    fun process(resources: Set<File>, context: ProcessorContext) {
        val paths = resources.map { it.toPath() }.toSet()

        val filtered = paths.filterInDirectory("static").takeIf { it.isNotEmpty() } ?: return
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
        require(mime != null) { "Unknown mime type for file $file at Spring `static` resources folder" }

        val resource = StaticResource(context.config.bucket, URIPath("static", path), file, mime)

        context.resources.register(key, resource)
        context.routes.register(Webapp.ApiGateway.StaticRoute(path, key))
    }

    private fun Set<Path>.filterInDirectory(directory: String) = filter { it.getParent(directory) != null  }.toSet()
}
