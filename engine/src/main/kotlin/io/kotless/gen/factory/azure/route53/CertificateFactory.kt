package io.kotless.gen.factory.azure.route53

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.azure.info.InfoFactory
import io.kotless.gen.factory.azure.utils.FilesCreationTf.app_service_certificate_binding
import io.kotless.gen.factory.azure.utils.FilesCreationTf.app_service_managed_cert
import io.terraformkt.hcl.ref

object CertificateFactory : GenerationFactory<Application.DNS, CertificateFactory.Output> {

    class Output()

    override fun mayRun(entity: Application.DNS, context: GenerationContext) = context.output.check(context.webapp, InfoFactory) &&
        context.output.check(entity, RecordFactory)

    override fun generate(entity: Application.DNS, context: GenerationContext): GenerationFactory.GenerationResult<CertificateFactory.Output> {
        val resourceGroup = context.output.get(context.webapp, InfoFactory).resourceGroup
        val hostnameBinding = context.output.get(entity, RecordFactory).hostnameBinding
        val cert = app_service_managed_cert(context.names.tf(entity.certificate), hostnameBinding::id.ref)

        val binding = app_service_certificate_binding(context.names.tf(entity.certificate), hostnameBinding::id.ref, cert.hcl_ref)

        return GenerationFactory.GenerationResult(Output(), cert, binding)
    }
}
