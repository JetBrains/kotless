package io.kotless.gen.factory.azure.infra

import io.kotless.KotlessConfig
import io.kotless.KotlessConfig.Cloud.Terraform
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.azurerm.provider.Provider
import io.terraformkt.azurerm.provider.provider
import io.terraformkt.terraform.TFConfig
import io.terraformkt.terraform.terraform

object TFConfigFactory : GenerationFactory<Terraform<Terraform.Backend.Azure, Terraform.Provider.Azure>, TFConfigFactory.Output> {
    class Output(val provider: Provider)

    override fun mayRun(entity: Terraform<Terraform.Backend.Azure, Terraform.Provider.Azure>, context: GenerationContext) = true

    override fun generate(
        entity: Terraform<Terraform.Backend.Azure, Terraform.Provider.Azure>,
        context: GenerationContext
    ): GenerationFactory.GenerationResult<Output> {
        val terraform = terraform {
            required_version = entity.version
            backend = TFConfig.Backend.AzureRM().apply {
                resource_group_name = entity.backend.resourceGroup
                storage_account_name = entity.backend.storageAccountName
                container_name = entity.backend.containerName
                key = entity.backend.key
            }
        }
        val provider = provider {
            version = entity.provider.version
            features { }
        }

        return GenerationFactory.GenerationResult(Output(provider), provider, terraform)
    }
}
