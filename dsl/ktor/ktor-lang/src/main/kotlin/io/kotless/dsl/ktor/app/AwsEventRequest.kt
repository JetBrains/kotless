package io.kotless.dsl.ktor.app

import io.kotless.dsl.model.AwsEvent
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.server.engine.*
import io.ktor.utils.io.*

/**
 * Ktor Request used by Kotless. It will be created from APIGateway request.
 */
class AwsEventRequest(val event: AwsEvent.Record, call: ApplicationCall) : BaseApplicationRequest(call) {
    override val pipeline = ApplicationReceivePipeline().apply {
        merge(call.application.receivePipeline)
    }

    override val cookies: RequestCookies = RequestCookies(this)

    override val headers: Headers = Headers.Empty

    override val local: RequestConnectionPoint = object : RequestConnectionPoint {
        override val host: String = event.eventSource
        override val method: HttpMethod = HttpMethod(event.eventSource)

        //Port is not applicable in case of Serverless execution
        override val port: Int = -1
        override val remoteHost: String = event.eventSource
        override val scheme: String = "https"
        override val uri: String = event.event.path.replace(":", "/").lowercase()
        override val version: String = "version"
    }

    override val queryParameters: Parameters = Parameters.build {
        event.event.parameters.forEach { (key, value) ->
            append(key, value)
        }
    }

    override fun receiveChannel() = ByteReadChannel(ByteArray(0))
}
