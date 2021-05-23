package io.kotless.gen.factory.azure.infra

import io.kotless.KotlessConfig
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.azurerm.provider.Provider
import io.terraformkt.azurerm.provider.provider
import io.terraformkt.terraform.TFConfig
import io.terraformkt.terraform.terraform

object ProvidersFactory : GenerationFactory<KotlessConfig.Terraform, ProvidersFactory.Output> {
    class Output(val provider: Provider)

    override fun mayRun(entity: KotlessConfig.Terraform, context: GenerationContext) = true

    override fun generate(entity: KotlessConfig.Terraform, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val azureConfig = context.schema.config.cloud as KotlessConfig.Cloud.Azure
        val terraform = terraform {
            required_version = entity.version
            backend = TFConfig.Backend.AzureRM().apply {
                resource_group_name = azureConfig.resourceGroup
                storage_account_name = azureConfig.storageAccountName
                container_name = context.schema.config.storage
                key = entity.backend.key
            }
        }
        val provider = provider {
            features { }
        }

        return GenerationFactory.GenerationResult(Output(provider), provider, terraform)
    }
}
