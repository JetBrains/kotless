package io.kotless.gen.factory.azure.info

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.azurerm.data.resource.ResourceGroup
import io.terraformkt.azurerm.data.resource.resource_group
import io.terraformkt.azurerm.data.storage.*

object InfoFactory : GenerationFactory<Application, InfoFactory.Output> {
    data class Output(val resourceGroup: ResourceGroup, val storageAccount: StorageAccount, val storageContainer: StorageContainer, val storageBlobName: String)

    override fun mayRun(entity: Application, context: GenerationContext) = true

    override fun generate(entity: Application, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val resourceGroup = resource_group(context.names.tf(context.schema.config.prefix, "resource_group")) {
            name = context.schema.config.resourceGroup!!
        }

        val storageAccount = storage_account(context.names.tf(context.schema.config.bucket, "storage_account")) {
            name = context.schema.config.storageAccountName!!
            resource_group_name = resourceGroup.name
        }

        val storageContainer = storage_container("storage_container") {
            name = context.schema.config.bucket
            storage_account_name = storageAccount.name
        }


        return GenerationFactory.GenerationResult(
            Output(resourceGroup, storageAccount, storageContainer, "azure-test-zip"),
            resourceGroup,
            storageAccount,
            storageContainer
        )
    }
}
