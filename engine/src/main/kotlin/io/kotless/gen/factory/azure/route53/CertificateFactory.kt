package io.kotless.gen.factory.azure.route53

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.azure.info.InfoFactory
import io.terraformkt.azurerm.resource.app.app_service_certificate_binding
import io.terraformkt.azurerm.resource.app.app_service_managed_certificate
import io.terraformkt.hcl.ref

object CertificateFactory : GenerationFactory<Application.DNS, CertificateFactory.Output> {

    class Output()

    override fun mayRun(entity: Application.DNS, context: GenerationContext) = context.output.check(context.webapp, InfoFactory) &&
        context.output.check(entity, RecordFactory)

    override fun generate(entity: Application.DNS, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val hostnameBinding = context.output.get(entity, RecordFactory).hostnameBinding
        val cert = app_service_managed_certificate(context.names.tf(entity.certificate)) {
            custom_hostname_binding_id = hostnameBinding::id.ref
        }

        val binding = app_service_certificate_binding(context.names.tf(entity.certificate)) {
            hostname_binding_id = hostnameBinding::id.ref
            certificate_id = cert::id.ref
            ssl_state = "SniEnabled"
        }

        return GenerationFactory.GenerationResult(Output(), cert, binding)
    }
}
