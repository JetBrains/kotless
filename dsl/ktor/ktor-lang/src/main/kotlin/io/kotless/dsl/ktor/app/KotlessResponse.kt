package io.kotless.dsl.ktor.app

import io.kotless.MimeType
import io.kotless.dsl.model.HttpResponse
import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.response.ResponseHeaders
import io.ktor.server.engine.BaseApplicationResponse
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.close
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class KotlessResponse(call: ApplicationCall) : BaseApplicationResponse(call), CoroutineScope {
    override val coroutineContext: CoroutineContext = EmptyCoroutineContext
    private val output = ByteChannel(true)

    private val reader = async(Dispatchers.Unconfined) {
        output.readRemaining()
    }

    override val headers: ResponseHeaders = object : ResponseHeaders() {
        private val builder = HeadersBuilder()

        override fun engineAppendHeader(name: String, value: String) {
            builder.append(name, value)
        }

        override fun getEngineHeaderNames(): List<String> = builder.names().toList()
        override fun getEngineHeaderValues(name: String): List<String> = builder.getAll(name).orEmpty()
    }

    private var _status: HttpStatusCode? = null

    override suspend fun respondOutgoingContent(content: OutgoingContent) {
        try {
            super.respondOutgoingContent(content)
        } catch (e: CancellationException) {
            coroutineScope { cancel(e) }
            output.cancel(e)
            throw e
        } finally {
            output.close()
        }
    }

    override suspend fun respondUpgrade(upgrade: OutgoingContent.ProtocolUpgrade) = throw NotImplementedError()

    override suspend fun responseChannel() = output

    override fun setStatus(statusCode: HttpStatusCode) {
        _status = statusCode
    }

    suspend fun toHttp(): HttpResponse {
        val content = reader.await().readBytes()

        val isBinary = headers["Content-Type"]?.let {
            val type = ContentType.parse(it)
            MimeType.forDeclaration(type.contentType, type.contentSubtype)
        }?.isBinary ?: false

        val status = status()?.value ?: 500
        val myHeaders = headers.allValues().entries().map { it.key to it.value.single() }.toMap().let { HashMap(it) }

        return if (isBinary) {
            HttpResponse(status, myHeaders, content)
        } else {
            HttpResponse(status, myHeaders, content.toString(Charsets.UTF_8))
        }
    }
}
