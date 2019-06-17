package io.kotless.engine.terraform.aws.acm

import io.kotless.engine.terraform.TfData
import io.kotless.engine.terraform.synthesizer.TfGroup

/**
 * Terraform aws_acm_certificate data.
 * @see <a href="https://www.terraform.io/docs/providers/aws/d/acm_certificate.html">aws_acm_certificate</a>
 */
class TfAcmCertificateData(tfName: String, domain: String) : TfData("aws_acm_certificate", tfName) {
    val arn = "$tfFullName.arn"

    override val dataDef = """
        |    domain = "${domain.trim().trimEnd('.', ' ')}"
        |    statuses = ["ISSUED"]
        """

    override val group = TfGroup.ACM
}
