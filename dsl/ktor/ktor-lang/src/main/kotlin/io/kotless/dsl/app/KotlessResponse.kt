package io.kotless.dsl.app

import io.kotless.dsl.model.HttpResponse
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.*
import io.ktor.util.KtorExperimentalAPI

class KotlessResponse(override val call: ApplicationCall): ApplicationResponse {
    private var _status: HttpStatusCode? = null
    private val myHeaders = HashMap<String, String>()

    override val pipeline = ApplicationSendPipeline().apply {
        merge(call.application.sendPipeline)
    }

    //TODO-tanvd fix -- use only when https
    override val cookies: ResponseCookies get() = ResponseCookies(this, true)
    override val headers: ResponseHeaders = object : ResponseHeaders() {
        override fun engineAppendHeader(name: String, value: String) {
            myHeaders[name] = value
        }

        override fun getEngineHeaderNames() = myHeaders.keys.toList()
        override fun getEngineHeaderValues(name: String) = myHeaders[name]?.let { listOf(it) } ?: emptyList()
    }

    @KtorExperimentalAPI
    override fun push(builder: ResponsePushBuilder) {}

    override fun status() = _status
    override fun status(value: HttpStatusCode) {
        _status = value
    }
}
