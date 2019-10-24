package io.kotless.dsl.ktor.app

import io.kotless.dsl.model.HttpRequest
import io.ktor.application.Application
import io.ktor.http.Parameters
import io.ktor.server.engine.BaseApplicationCall
import io.ktor.server.engine.EngineAPI

@EngineAPI
class KotlessCall(application: Application, request: HttpRequest) : BaseApplicationCall(application) {
    override val request = KotlessRequest(request, this)
    override val response = KotlessResponse(this)

    override val parameters: Parameters by lazy { this.request.queryParameters }

    init {
        putResponseAttribute()
    }
}
