package io.kotless.parser.spring.processor.route

import io.kotless.MimeType
import io.kotless.StaticResource
import io.kotless.URIPath
import io.kotless.Webapp
import io.kotless.parser.processor.ProcessorContext
import io.kotless.utils.TypedStorage
import java.io.File

object StaticRoutesProcessor {
    fun process(resources: Set<File>, context: ProcessorContext) {
        val resourcesPaths = resources.map { it.toPath() }.toSet()
        val staticsRoot = context.config.dsl.staticsRoot.toPath().resolve("static")

        val filtered = resourcesPaths.filter { it.normalize().startsWith(staticsRoot) }

        for (file in filtered) {
            val path = staticsRoot.relativize(file)
            createResource(file.toFile(), URIPath(path.map { it.toString() }), context)
        }
    }


    private fun createResource(file: File, path: URIPath, context: ProcessorContext) {
        val key = TypedStorage.Key<StaticResource>()
        val mime = MimeType.forFile(file)
        require(mime != null) { "Unknown mime type for file $file at Spring `static` resources folder" }

        val resource = StaticResource(URIPath("static", path), file, mime)

        context.resources.register(key, resource)
        context.routes.register(Webapp.ApiGateway.StaticRoute(path, key))
    }
}
