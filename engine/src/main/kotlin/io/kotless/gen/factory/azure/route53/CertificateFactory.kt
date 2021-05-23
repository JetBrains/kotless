package io.kotless.gen.factory.azure.route53

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.azure.info.InfoFactory
import io.terraformkt.azurerm.data.app.AppServiceCertificate
import io.terraformkt.azurerm.data.app.app_service_certificate
import io.terraformkt.hcl.ref

object CertificateFactory : GenerationFactory<Application.DNS, CertificateFactory.Output> {

    data class Output(val certificate: AppServiceCertificate)

    override fun mayRun(entity: Application.DNS, context: GenerationContext) = context.output.check(context.webapp, InfoFactory)

    override fun generate(entity: Application.DNS, context: GenerationContext): GenerationFactory.GenerationResult<CertificateFactory.Output> {
        val resourceGroup = context.output.get(context.webapp, InfoFactory).resourceGroup
        val cert = app_service_certificate(context.names.tf(entity.certificate)) {
            name = entity.certificate
            resource_group_name = resourceGroup::name.ref
        }

        return GenerationFactory.GenerationResult(Output(cert), cert)
    }
}
