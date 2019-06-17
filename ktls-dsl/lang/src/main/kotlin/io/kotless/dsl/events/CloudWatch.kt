package io.kotless.dsl.events

import kotlinx.serialization.Serializable

/** AWS CloudWatch event representation */
@Suppress("ConstructorParameterNaming")
@Serializable
internal data class CloudWatch(val source: String, val `detail-type`: String)
