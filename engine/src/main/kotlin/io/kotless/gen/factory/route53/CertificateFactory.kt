package io.kotless.gen.factory.route53

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.infra.TFProvidersFactory
import io.terraformkt.hcl.ref
import io.kotless.terraform.functions.link
import io.terraformkt.aws.data.acm.acm_certificate

object CertificateFactory : GenerationFactory<Application.Route53, CertificateFactory.Output> {
    data class Output(val cert_arn: String)

    override fun mayRun(entity: Application.Route53, context: GenerationContext) = context.output.check(context.schema.config.terraform, TFProvidersFactory)

    override fun generate(entity: Application.Route53, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val providers = context.output.get(context.schema.config.terraform, TFProvidersFactory)

        val cert = acm_certificate(context.names.tf(entity.certificate)) {
            providers.us_east_provider?.let {
                provider = link(it)
            }

            domain = entity.certificate
            statuses = arrayOf("ISSUED")
        }

        return GenerationFactory.GenerationResult(Output(cert::arn.ref), cert)
    }
}
