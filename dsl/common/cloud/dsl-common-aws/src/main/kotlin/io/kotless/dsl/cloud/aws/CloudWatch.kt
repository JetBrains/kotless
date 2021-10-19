package io.kotless.dsl.cloud.aws

import io.kotless.InternalAPI
import kotlinx.serialization.Serializable

/** AWS CloudWatch event representation */
@InternalAPI
@Serializable
data class CloudWatch(val source: String, val `detail-type`: String, val resources: Set<String>)
