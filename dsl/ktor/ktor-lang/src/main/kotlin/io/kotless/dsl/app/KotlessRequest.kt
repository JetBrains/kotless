package io.kotless.dsl.app

import io.kotless.dsl.model.HttpRequest
import io.ktor.application.ApplicationCall
import io.ktor.http.*
import io.ktor.request.ApplicationReceivePipeline
import io.ktor.request.RequestCookies
import io.ktor.server.engine.BaseApplicationRequest
import kotlinx.coroutines.io.ByteReadChannel

//TODO-tanvd verify all
class KotlessRequest(val query: HttpRequest, call: ApplicationCall) : BaseApplicationRequest(call) {
    override val pipeline = ApplicationReceivePipeline().apply {
        merge(call.application.receivePipeline)
    }

    override val cookies: RequestCookies = RequestCookies(this)

    override val headers: Headers = Headers.build {
        query.headers?.forEach { append(it.key, it.value) }
    }

    override val local: RequestConnectionPoint = object : RequestConnectionPoint {
        override val host: String = query.requestContext.domainName
        override val method: HttpMethod = HttpMethod.parse(query.httpMethod.toUpperCase())
        override val port: Int = 80
        override val remoteHost: String = query.requestContext.identity.sourceIp
        override val scheme: String = query.requestContext.protocol ?: ""
        override val uri: String = query.path
        override val version: String = query.requestContext.stage
    }

    override val queryParameters: Parameters = Parameters.build {
        query.allParams.forEach { append(it.key, it.value) }
    }

    override fun receiveChannel() = ByteReadChannel(query.body ?: "")
}
