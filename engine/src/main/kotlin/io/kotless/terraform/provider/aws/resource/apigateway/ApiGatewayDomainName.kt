package io.kotless.terraform.provider.aws.resource.apigateway

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_api_gateway_domain_name resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_domain_name.html">aws_api_gateway_domain_name</a>
 */
class ApiGatewayDomainName(id: String) : TFResource(id, "aws_api_gateway_domain_name") {
    val cloudfront_domain_name by text(inner = true)
    val cloudfront_zone_id by text(inner = true)

    var domain_name by text()
    var certificate_arn by text()
}

fun api_gateway_domain_name(id: String, configure: ApiGatewayDomainName.() -> Unit) = ApiGatewayDomainName(id).apply(configure)

fun TFFile.api_gateway_domain_name(id: String, configure: ApiGatewayDomainName.() -> Unit) {
    add(ApiGatewayDomainName(id).apply(configure))
}
