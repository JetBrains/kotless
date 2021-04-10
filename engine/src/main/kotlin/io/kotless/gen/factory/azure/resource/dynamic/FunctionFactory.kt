package io.kotless.gen.factory.azure.resource.dynamic

import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.azure.ZipArchiveFactory
import io.kotless.gen.factory.azure.info.InfoFactory
import io.kotless.gen.factory.azure.storage.StorageFactory
import io.kotless.resource.Lambda
import io.terraformkt.azurerm.resource.app.app_service_plan
import io.terraformkt.azurerm.resource.application.application_insights
import io.terraformkt.azurerm.resource.function.FunctionApp
import io.terraformkt.azurerm.resource.function.function_app
import io.terraformkt.hcl.ref

object FunctionFactory : GenerationFactory<Lambda, FunctionFactory.Output> {
    data class Output(val function: FunctionApp)

    override fun mayRun(entity: Lambda, context: GenerationContext) = context.output.check(context.webapp, InfoFactory)
        && context.output.check(context.webapp, StorageFactory)
        && context.output.check(context.webapp, ZipArchiveFactory)

    override fun generate(entity: Lambda, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val resourceGroup = context.output.get(context.webapp, InfoFactory).resourceGroup
        val storageAccount = context.output.get(context.webapp, InfoFactory).storageAccount
        val storageContainer = context.output.get(context.webapp, InfoFactory).staticStorageContainer
        val storageBlob = context.output.get(context.webapp, StorageFactory).storageBlob
        val storageAccountSas = context.output.get(context.webapp, StorageFactory).storageAccountSas

        val appServicePlan = app_service_plan(context.names.tf("app", "service", "plan")) {
            name = context.names.azure(context.schema.config.prefix.replace("-", ""), "functions", "consumption", "asp")
            location = resourceGroup::location.ref
            resource_group_name = resourceGroup::name.ref
            sku {
                tier = "Dynamic"
                size = "Y1"
            }
        }


        val appInsight = application_insights("funcdeploy") {
            name = context.names.azure(entity.name, "app", "insight")
            location = resourceGroup::location.ref
            resource_group_name = resourceGroup::name.ref
            application_type = "java"
            tags(
                mapOf(
                    "hidden-link:${resourceGroup::id.ref}/providers/Microsoft.Web/sites/${context.names.azure(entity.name)}" to "Resource"
                )
            )
        }

        val functionApp = function_app("functionapp") {
            name = context.names.azure(entity.name)
            location = resourceGroup::location.ref
            resource_group_name = resourceGroup::name.ref
            app_service_plan_id = appServicePlan::id.ref
            storage_account_name = storageAccount::name.ref
            storage_account_access_key = storageAccount::primary_access_key.ref
            https_only = true
            version = "~3"

            appSettings(
                mapOf(
                    "FUNCTIONS_WORKER_RUNTIME" to "java",
                    "FUNCTION_APP_EDIT_MODE" to "readonly",
                    "APPLICATIONINSIGHTS_CONNECTION_STRING" to
                        "InstrumentationKey=${appInsight::instrumentation_key.ref};IngestionEndpoint=https://westeurope-1.in.applicationinsights.azure.com/",
                    "APPINSIGHTS_INSTRUMENTATIONKEY" to appInsight::instrumentation_key.ref,
                    "WEBSITE_RUN_FROM_PACKAGE" to
                        "https://${storageAccount.name}.blob.core.windows.net/${storageContainer::name.ref}/${storageBlob::name.ref}${storageAccountSas::sas.ref}"
                ) + entity.config.environment
            )
        }

        return GenerationFactory.GenerationResult(
            Output(functionApp), appServicePlan, storageAccount, storageContainer, appInsight, functionApp
        )

    }
}
