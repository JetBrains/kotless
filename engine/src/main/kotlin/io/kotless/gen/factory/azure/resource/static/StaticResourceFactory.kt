package io.kotless.gen.factory.azure.resource.static

import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.azure.info.InfoFactory
import io.kotless.resource.StaticResource
import io.kotless.terraform.functions.path
import io.terraformkt.azurerm.resource.storage.storage_blob
import io.terraformkt.hcl.ref

object StaticResourceFactory : GenerationFactory<StaticResource, StaticResourceFactory.Output> {
    data class Output(val blobName: String)

    override fun mayRun(entity: StaticResource, context: GenerationContext) = context.output.check(context.webapp, InfoFactory)

    override fun generate(entity: StaticResource, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val resourceName = "copy_${entity.file.path.replace(".", "_").replace("/", "_")}"
        val storageAccount = context.output.get(context.webapp, InfoFactory).storageAccount
        val storageContainer = context.output.get(context.webapp, InfoFactory).staticStorageContainer
        val storageBlob = storage_blob(context.names.tf(context.schema.config.azure.storage.container, entity.path.parts)) {
            name = resourceName
            storage_account_name = storageAccount::name.ref
            storage_container_name = storageContainer::name.ref
            type = "Block"
            access_tier = "Hot"
            content_type = entity.mime.mimeText
            source = path(entity.file)
        }

        return GenerationFactory.GenerationResult(Output(resourceName), storageBlob)
    }
}
