package io.kotless.dsl.ktor.app

import io.kotless.dsl.model.HttpRequest
import io.ktor.http.Parameters
import io.ktor.server.application.Application
import io.ktor.server.engine.BaseApplicationCall

/**
 * Ktor Call used by Kotless. It is mapped from APIGateway request and to APIGateway response.
 */
class KotlessCall(application: Application, request: HttpRequest) : BaseApplicationCall(application) {
    override val request = KotlessRequest(request, this)
    override val response = KotlessResponse(this)

    override val parameters: Parameters by lazy { this.request.queryParameters }

    init {
        putResponseAttribute()
    }
}
