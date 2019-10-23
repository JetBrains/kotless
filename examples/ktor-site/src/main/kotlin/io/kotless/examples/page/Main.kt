package io.kotless.examples.page

import io.kotless.dsl.Kotless
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

class Main : Kotless() {
    override fun prepare(app: Application) {
        app.routing {
            get("/") {
                call.respondText { "Hello World!" }
            }
            get("/text") {
                call.respondText { "Hello My World!" }
            }
        }
    }
}

//fun main() {
//    val request = HttpRequest(resource = "api", path = "/texty", httpMethod = "get", headers = emptyMap(),
//        queryStringParameters = emptyMap(), pathParameters = emptyMap(),
//        requestContext = HttpRequest.RequestContext(
//            identity = HttpRequest.RequestContext.RequestIdentity(sourceIp = "1.1.1.1", userAgent = "my_user_agent"),
//            stage = "1", path = "/", protocol = "https", requestTimeEpoch = 1, domainName = "localhost"
//        ),
//        body = ""
//    )
//
//    val result = ByteArrayOutputStream()
//    Main().handleRequest(Json.bytes(HttpRequest.serializer(), request).inputStream(), result)
//    println(result.toByteArray().toString(Charsets.UTF_8))
//}
