package io.kotless.gen.factory.route53

import io.kotless.Webapp
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.infra.TFProvidersFactory
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.data.acm.acm_certificate

object CertificateFactory : GenerationFactory<Webapp.Route53, CertificateFactory.Output> {
    data class Output(val cert_arn: String)

    override fun mayRun(entity: Webapp.Route53, context: GenerationContext) = context.output.check(context.schema.config.terraform, TFProvidersFactory)

    override fun generate(entity: Webapp.Route53, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val providers = context.output.get(context.schema.config.terraform, TFProvidersFactory)

        val cert = acm_certificate(context.names.tf(entity.certificate)) {
            providers.us_east_provider?.let {
                provider = it
            }

            domain = entity.certificate
            statuses = arrayOf("ISSUED")
        }

        return GenerationFactory.GenerationResult(Output(cert::arn.ref), cert)
    }
}
