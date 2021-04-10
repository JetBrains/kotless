package io.kotless.gen.factory.azure.info

import io.kotless.Application
import io.kotless.KotlessConfig
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.azurerm.data.resource.ResourceGroup
import io.terraformkt.azurerm.data.resource.resource_group
import io.terraformkt.azurerm.data.storage.*
import io.terraformkt.azurerm.resource.storage.storage_container
import io.terraformkt.azurerm.resource.storage.StorageContainer

object InfoFactory : GenerationFactory<Application, InfoFactory.Output> {
    data class Output(val resourceGroup: ResourceGroup, val storageAccount: StorageAccount,
                      val staticStorageContainer: StorageContainer,
                      val storageBlobName: String)

    override fun mayRun(entity: Application, context: GenerationContext) = true

    override fun generate(entity: Application, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val azureConfig = context.schema.config.cloudConfig as KotlessConfig.AzureCloudConfig
        val prefix = context.schema.config.prefix
        val resourceGroup = resource_group(context.names.tf(context.schema.config.prefix, "resource_group")) {
            name = azureConfig.resourceGroup
        }

        val storageAccount = storage_account(context.names.tf(context.schema.config.bucket, "storage_account")) {
            name = azureConfig.storageAccountName
            resource_group_name = resourceGroup.name
        }

        val storageContainer = io.terraformkt.azurerm.data.storage.storage_container("storage_container") {
            name = context.schema.config.bucket
            storage_account_name = storageAccount.name
        }

        val staticStorageContainer = storage_container("static_storage_container") {
            name = "$prefix-storage-container"
            storage_account_name = storageAccount.name
            container_access_type = "blob"
        }


        return GenerationFactory.GenerationResult(
            Output(resourceGroup, storageAccount, staticStorageContainer, "azure-test-zip"),
            resourceGroup,
            storageAccount,
            storageContainer,
            staticStorageContainer
        )
    }
}
