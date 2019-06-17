package io.kotless.engine.terraform.aws.infra

import io.kotless.KotlessConfig

/** Terraform backend and version configuration */
data class TfConfig(val version: String, val backend: KotlessConfig.Terraform.Backend) {
    fun render() = """
            |terraform {
            |    backend "s3" {
            |        bucket = "${backend.bucket}"
            |        key = "${backend.key}"
            |        profile = "${backend.profile}"
            |        region = "${backend.region}"
            |    }
            |    required_version = "$version"
            |}
        """.trimMargin()
}
