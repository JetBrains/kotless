package io.kotless.dsl.app

import io.kotless.dsl.model.HttpResponse
import io.ktor.application.ApplicationCall
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.response.ResponseHeaders
import io.ktor.server.engine.BaseApplicationResponse
import io.ktor.util.toByteArray
import kotlinx.coroutines.io.ByteChannel
import kotlinx.coroutines.io.ByteWriteChannel
import sun.reflect.generics.reflectiveObjects.NotImplementedException

class KotlessResponse(call: ApplicationCall) : BaseApplicationResponse(call) {
    val output = ByteChannel(true)

    override val headers: ResponseHeaders = object : ResponseHeaders() {
        private val builder = HeadersBuilder()

        override fun engineAppendHeader(name: String, value: String) {
            builder.append(name, value)
        }

        override fun getEngineHeaderNames(): List<String> = builder.names().toList()
        override fun getEngineHeaderValues(name: String): List<String> = builder.getAll(name).orEmpty()
    }

    private var _status: HttpStatusCode? = null

    override suspend fun respondUpgrade(upgrade: OutgoingContent.ProtocolUpgrade) {
        throw NotImplementedException()
    }

    override suspend fun responseChannel(): ByteWriteChannel = output

    override fun setStatus(statusCode: HttpStatusCode) {
        _status = statusCode
    }

    suspend fun toHttp(): HttpResponse {
        val status = status()?.value ?: 500
        val myHeaders = headers.allValues().entries().map { it.key to it.value.single() }.toMap().let { HashMap(it) }
        val text = output.toByteArray().toString(Charsets.UTF_8)
        return HttpResponse(status, myHeaders, text, false)
    }
}
