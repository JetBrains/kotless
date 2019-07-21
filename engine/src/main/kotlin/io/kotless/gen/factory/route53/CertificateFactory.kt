package io.kotless.gen.factory.route53

import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.terraform.provider.aws.data.acm.acm_certificate

object CertificateFactory : GenerationFactory<Webapp.Route53, CertificateFactory.CertificateOutput> {
    data class CertificateOutput(val cert_arn: String)

    override fun mayRun(entity: Webapp.Route53, context: GenerationContext) = true

    override fun generate(entity: Webapp.Route53, context: GenerationContext): GenerationFactory.GenerationResult<CertificateOutput> {
        val cert = acm_certificate(Names.tf(entity.certificate)) {
            domain = entity.certificate
            statuses = arrayOf("ISSUED")
        }

        return GenerationFactory.GenerationResult(CertificateOutput(cert.arn), cert)
    }
}
