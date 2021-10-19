package io.kotless.gen.factory.azure.filescontent

import io.kotless.Application
import io.kotless.resource.Lambda
import io.terraformkt.azurerm.data.storage.StorageAccount
import io.terraformkt.azurerm.resource.storage.StorageContainer
import io.terraformkt.hcl.ref

object LambdaDescription {
    fun body(lambda: Lambda, methods: List<String> = listOf("GET", "POST")): String {
        return """
            {
              "scriptFile" : "../${lambda.file.name}",
              "entryPoint" : "${lambda.entrypoint.qualifiedName}",
              "bindings" : [ {
                "type" : "httpTrigger",
                "direction" : "in",
                "name" : "req",
                "methods" : [ ${methods.joinToString(", ") { "\"$it\"" }} ],
                "authLevel" : "ANONYMOUS"
              }, {
                "type" : "http",
                "direction" : "out",
                "name" : "${'$'}return"
              } ]
            }
        """.trimIndent().replace("\"", "\\\"").replace("\n", "")
    }

    fun timeBinding(lambda: Lambda, schedule: String): String {
        return """
            {
              "scriptFile" : "../${lambda.file.name}",
              "entryPoint" : "${lambda.entrypoint.qualifiedName.removeSuffix(".run")}.timer",
              "bindings" : [ {
                "schedule": "$schedule",
                "name": "timer",
                "type": "timerTrigger",
                "direction": "in"
              } ]
            }
        """.trimIndent().replace("\"", "\\\"").replace("\n", "")
    }

    fun proxy(lambdaPath: String, dynamicRoute: Application.API.DynamicRoute, functionAppName: String): String {
        return """
            "${dynamicRoute.path}_route": {
                "matchCondition": {
                    "route": "${dynamicRoute.path}"
                },
                "requestOverrides": {
                    "backend.request.headers.x-original-path": "/${dynamicRoute.path}"
                },
                "backendUri": "https://${functionAppName}.azurewebsites.net/${lambdaPath}"
            }
        """.trimIndent().replace("\"", "\\\"").replace("\n", "")
    }

    fun host() = """
            {
              "version": "2.0",
              "extensionBundle": {
                "id": "Microsoft.Azure.Functions.ExtensionBundle",
                "version": "[1.*, 2.0.0)"
              },
              "extensions": {
                "http": {
                  "routePrefix": ""
                }
              }
            }
        """.trimIndent().replace("\"", "\\\"").replace("\n", "")

    fun localSettings() = """
            {
              "IsEncrypted": false,
              "Values": {
                "AzureWebJobsStorage": "",
                "FUNCTIONS_WORKER_RUNTIME": "java"
              }
            }
        """.trimIndent().replace("\"", "\\\"").replace("\n", "")

    fun staticRoute(staticRoute: Application.API.StaticRoute, storageAccount: StorageAccount, storageContainer: StorageContainer, blobName: String) = """
            "${staticRoute.path}": {
                "matchCondition": {
                    "route": "${staticRoute.path}"
                },
                "backendUri": "https://${storageAccount::name.ref}.blob.core.windows.net/${storageContainer::name.ref}/${blobName}"
            }
        """.trimIndent().replace("\"", "\\\"").replace("\n", "")
}
