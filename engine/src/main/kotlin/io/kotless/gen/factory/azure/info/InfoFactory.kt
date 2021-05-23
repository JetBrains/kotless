package io.kotless.gen.factory.azure.info

import io.kotless.Application
import io.kotless.KotlessConfig
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.azurerm.data.resource.ResourceGroup
import io.terraformkt.azurerm.data.resource.resource_group
import io.terraformkt.azurerm.data.storage.StorageAccount
import io.terraformkt.azurerm.data.storage.storage_account
import io.terraformkt.azurerm.resource.storage.StorageContainer
import io.terraformkt.azurerm.resource.storage.storage_container

object InfoFactory : GenerationFactory<Application, InfoFactory.Output> {
    data class Output(
        val resourceGroup: ResourceGroup, val storageAccount: StorageAccount,
        val staticStorageContainer: StorageContainer,
        val storageBlobName: String
    )

    override fun mayRun(entity: Application, context: GenerationContext) = true

    override fun generate(entity: Application, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val azure = context.schema.config.cloud as KotlessConfig.Cloud.Azure
        val prefix = context.schema.config.prefix
        val resourceGroup = resource_group(context.names.tf(context.schema.config.prefix, "resource_group")) {
            name = azure.terraform.backend.resourceGroup
        }

        val storageAccount = storage_account(context.names.tf(context.schema.config.storage, "storage_account")) {
            name = azure.terraform.backend.storageAccountName
            resource_group_name = resourceGroup.name
        }

        val storageContainer = io.terraformkt.azurerm.data.storage.storage_container("storage_container") {
            name = context.schema.config.storage
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
