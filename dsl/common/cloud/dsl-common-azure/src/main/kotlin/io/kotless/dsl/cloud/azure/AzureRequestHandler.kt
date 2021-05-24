package io.kotless.dsl.cloud.azure

import com.microsoft.azure.functions.*
import java.util.*

interface AzureRequestHandler {
    fun run(request: HttpRequestMessage<Optional<String>>, context: ExecutionContext): HttpResponseMessage
}
