package io.kotless.dsl.model

import kotlinx.serialization.Serializable

@Serializable
open class Response(val status: Int) {
}
