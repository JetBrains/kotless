package io.kotless.terraform.provider.aws.resource.apigateway

import io.kotless.hcl.HCLEntity
import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_api_gateway_deployment resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_deployment.html">aws_api_gateway_deployment</a>
 */
class ApiGatewayDeployment(id: String) : TFResource(id, "aws_api_gateway_deployment") {
    val id by text(inner = true)
    val invoke_url by text(inner = true)

    var rest_api_id by text()
    var stage_name by text()

    var variables by entity<HCLEntity>(default = HCLEntity())

    class Lifecycle(configure: Lifecycle.() -> Unit = {}) : HCLEntity() {
        init {
            configure()
        }

        val create_before_destroy by bool(default = true)
    }

    val lifecycle by entity<Lifecycle>(default = Lifecycle())
}

fun api_gateway_deployment(id: String, configure: ApiGatewayDeployment.() -> Unit) = ApiGatewayDeployment(id).apply(configure)

fun TFFile.api_gateway_deployment(id: String, configure: ApiGatewayDeployment.() -> Unit) {
    add(ApiGatewayDeployment(id).apply(configure))
}
