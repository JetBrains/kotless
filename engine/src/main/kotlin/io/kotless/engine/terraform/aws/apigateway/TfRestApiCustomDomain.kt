package io.kotless.engine.terraform.aws.apigateway

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.aws.acm.TfAcmCertificateData
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.tf


/**
 * Terraform aws_api_gateway_domain_name resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_domain_name.html">aws_api_gateway_domain_name</a>
 */
class TfRestApiDomainName(tfName: String, domainName: String, certificate: TfAcmCertificateData) : TfResource("aws_api_gateway_domain_name", tfName) {
    val cloudfront_domain_name = "$tfFullName.cloudfront_domain_name"
    val cloudfront_zone_id = "$tfFullName.cloudfront_zone_id"

    val domain_name = "$tfFullName.domain_name"

    override val resourceDef: String = """
        |    domain_name = "$domainName"
        |    certificate_arn = ${tf(certificate.arn)}
        """
    override val group: TfGroup = TfGroup.RestApi
}

/**
 * Terraform aws_api_gateway_base_path_mapping resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_base_path_mapping.html">aws_api_gateway_base_path_mapping</a>
 */
class TfRestApiBasePathMapping(tfName: String, restApi: TfRestApi, domainName: TfRestApiDomainName,
                               deployment: TfRestApiDeployment? = null) : TfResource("aws_api_gateway_base_path_mapping", tfName) {
    override val resourceDef: String = """
        |    api_id = ${tf(restApi.id)}
        |    ${deployment?.let { "stage_name = ${tf(deployment.stage_name)}" } ?: ""}
        |    domain_name = ${tf(domainName.domain_name)}
        """
    override val group: TfGroup = TfGroup.RestApi
}
