package io.kotless.gen.factory.azure.storage

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.azure.ZipArchiveFactory
import io.kotless.gen.factory.azure.info.InfoFactory
import io.terraformkt.azurerm.data.storage.StorageAccountSas
import io.terraformkt.azurerm.data.storage.storage_account_sas
import io.terraformkt.azurerm.resource.storage.StorageBlob
import io.terraformkt.azurerm.resource.storage.storage_blob
import io.terraformkt.hcl.ref

object StorageFactory : GenerationFactory<Application, StorageFactory.Output> {
    data class Output(val storageBlob: StorageBlob, val storageAccountSas: StorageAccountSas)

    override fun mayRun(entity: Application, context: GenerationContext): Boolean = context.output.check(context.webapp, InfoFactory)
        && context.output.check(context.webapp, ZipArchiveFactory)

    override fun generate(entity: Application, context: GenerationContext): GenerationFactory.GenerationResult<StorageFactory.Output> {
        val storageAccount = context.output.get(context.webapp, InfoFactory).storageAccount
        val storageContainer = context.output.get(context.webapp, InfoFactory).storageContainer
        val zipArchiveRef = context.output.get(context.webapp, ZipArchiveFactory).artifactCompleteRef

        val storageAccountSas = storage_account_sas("storage_account") {
            connection_string = storageAccount::primary_connection_string.ref
            https_only = true

            expiry = "\${timeadd(timestamp(), \"17520h\")}"

            resourceTypes {
                service = true
                container = true
                `object` = true
            }

            services {
                blob = true
                queue = false
                table = false
                file = false
            }

            start = "\${timestamp()}"
            permissions {
                read = true
                write = true
                delete = true
                list = true
                add = true
                create = true
                update = false
                process = false
            }
        }

        val lambdas = context.schema.lambdas.all
        val directory = lambdas.first().file.parentFile

        val storageBlob = storage_blob("storage_blob") {
            name = "azure-test-zip"
            storage_account_name = storageAccount::name.ref
            storage_container_name = storageContainer::name.ref
            type = "Block"
            source = "${directory.parent}/result.zip"
            depends_on = arrayOf(zipArchiveRef)
        }
        return GenerationFactory.GenerationResult(Output(storageBlob, storageAccountSas), storageAccountSas, storageBlob)
    }

}
