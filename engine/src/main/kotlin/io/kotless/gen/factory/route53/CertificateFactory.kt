package io.kotless.gen.factory.route53

import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.gen.factory.infra.TFProvidersFactory
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.data.acm.acm_certificate

object CertificateFactory : GenerationFactory<Webapp.Route53, CertificateFactory.CertificateOutput> {
    data class CertificateOutput(val cert_arn: String)

    override fun mayRun(entity: Webapp.Route53, context: GenerationContext) = context.output.check(context.schema.config.terraform, TFProvidersFactory)

    override fun generate(entity: Webapp.Route53, context: GenerationContext): GenerationFactory.GenerationResult<CertificateOutput> {
        val providers = context.output.get(context.schema.config.terraform, TFProvidersFactory)

        val cert = acm_certificate(Names.tf(entity.certificate)) {
            providers.us_east_provider?.let {
                provider = it
            }

            domain = entity.certificate
            statuses = arrayOf("ISSUED")
        }

        return GenerationFactory.GenerationResult(CertificateOutput(cert::arn.ref), cert)
    }
}
