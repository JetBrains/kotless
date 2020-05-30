package io.kotless.dsl.ktor.app

import io.kotless.dsl.model.HttpRequest
import io.ktor.application.ApplicationCall
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.RequestConnectionPoint
import io.ktor.request.ApplicationReceivePipeline
import io.ktor.request.RequestCookies
import io.ktor.server.engine.BaseApplicationRequest
import io.ktor.utils.io.ByteReadChannel

/**
 * Ktor Request used by Kotless. It will be created from APIGateway request.
 */
class KotlessRequest(val query: HttpRequest, call: ApplicationCall) : BaseApplicationRequest(call) {
    override val pipeline = ApplicationReceivePipeline().apply {
        merge(call.application.receivePipeline)
    }

    override val cookies: RequestCookies = RequestCookies(this)

    override val headers: Headers = Headers.build {
        query.headers?.forEach { appendAll(it.key, it.value) }
    }

    override val local: RequestConnectionPoint = object : RequestConnectionPoint {
        override val host: String = query.requestContext.domainName
        override val method: HttpMethod = HttpMethod.parse(query.method.name)

        //Port is not applicable in case of Serverless execution
        override val port: Int = -1
        override val remoteHost: String = query.requestContext.identity.sourceIp
        override val scheme: String = query.requestContext.protocol
        override val uri: String = query.path
        override val version: String = query.requestContext.protocol
    }

    override val queryParameters: Parameters = Parameters.build {
        query.params?.forEach { append(it.key, it.value) }
    }

    override fun receiveChannel() = ByteReadChannel(query.body ?: ByteArray(0))
}
