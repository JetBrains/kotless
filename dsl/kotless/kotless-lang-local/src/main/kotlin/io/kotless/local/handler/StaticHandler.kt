package io.kotless.local.handler

import io.kotless.dsl.lang.http.StaticGet
import io.kotless.dsl.reflection.ReflectionScanner
import io.kotless.local.Environment
import io.kotless.toURIPath
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class StaticHandler : AbstractHandler() {
    private val fields by lazy { ReflectionScanner.fieldsWithAnnotation<StaticGet, File>().mapValues { File(Environment.workingDir, it.value.path) } }

    override fun handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
        val path = request.requestURI.toURIPath()
        val route = fields.entries.find { it.key.path.toURIPath() == path }
        if (route != null) {
            val (ann, file) = route

            response.apply {
                status = 200
                setHeader("Content-Type", ann.mime.mimeText)
                if (ann.mime.isBinary) {
                    outputStream.write(file.readBytes())
                } else {
                    outputStream.write(file.readText().toByteArray(Charsets.UTF_8))
                }
            }

            baseRequest.isHandled = true
        }
    }
}
