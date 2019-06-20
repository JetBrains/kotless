package io.kotless.terraform.provider.aws.data.acm

import io.kotless.terraform.TFData
import io.kotless.terraform.TFFile

/**
 * Terraform aws_acm_certificate data.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/d/acm_certificate.html">aws_acm_certificate</a>
 */
class ACMCertificate(id: String) : TFData(id, "aws_acm_certificate") {
    val arn by text(inner = true)

    var domain by text()
    var statuses by textArray()
}

fun acm_certificate(id: String, configure: ACMCertificate.() -> Unit) = ACMCertificate(id).apply(configure)

fun TFFile.acm_certificate(id: String, configure: ACMCertificate.() -> Unit) {
    add(ACMCertificate(id).apply(configure))
}
