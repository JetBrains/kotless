package io.kotless.dsl

import com.microsoft.azure.functions.*
import java.util.*

interface AzureRequestHandler {
    fun handleRequest(request: HttpRequestMessage<Optional<String>>, context: ExecutionContext): HttpResponseMessage
}
