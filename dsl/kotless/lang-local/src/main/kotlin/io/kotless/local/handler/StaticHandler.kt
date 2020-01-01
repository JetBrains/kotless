package io.kotless.local.handler

import io.kotless.dsl.lang.http.StaticGet
import io.kotless.dsl.reflection.ReflectionScanner
import io.kotless.toURIPath
import kotlinx.serialization.toUtf8Bytes
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class StaticHandler : AbstractHandler() {
    private val workingDir by lazy { File(System.getenv("WORKING_DIR")) }

    private val fields by lazy { ReflectionScanner.fieldsWithAnnotation<StaticGet, File>().mapValues { File(workingDir, it.value.path) } }

    override fun handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
        val path = request.requestURI.toURIPath()
        val route = fields.entries.find { it.key.path.toURIPath() == path }
        if (route != null) {
            val (ann, file) = route
            response.status = 200
            response.setHeader("Content-Type", ann.mime.mimeText)
            if (ann.mime.isBinary) {
                response.outputStream.write(file.readBytes())
            } else {
                response.outputStream.write(file.readText().toUtf8Bytes())
            }
            baseRequest.isHandled = true
        }
    }
}
