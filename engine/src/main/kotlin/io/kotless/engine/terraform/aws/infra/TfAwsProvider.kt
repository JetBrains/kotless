package io.kotless.engine.terraform.aws.infra

import io.kotless.KotlessConfig

/** Terraform AWS provider configuration*/
data class TfAwsProvider(val provider: KotlessConfig.Terraform.AWSProvider) {
    fun render() = """
            |provider "aws" {
            |    region  = "${provider.region}"
            |    profile = "${provider.profile}"
            |    version = "${provider.version}"
            |}
        """.trimMargin()
}
