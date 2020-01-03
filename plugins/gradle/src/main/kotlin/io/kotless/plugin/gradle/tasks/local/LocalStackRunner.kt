package io.kotless.plugin.gradle.tasks.local

import io.kotless.AwsResource
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.testcontainers.containers.localstack.LocalStackContainer

class LocalStackRunner(val isEnabled: Boolean, resources: Set<AwsResource>) {
    open class Start : DefaultTask() {
        lateinit var localstack: LocalStackRunner

        @TaskAction
        fun act() {
            localstack.start()
        }
    }

    open class Stop : DefaultTask() {
        lateinit var localstack: LocalStackRunner

        @TaskAction
        fun act() {
            localstack.stop()
        }
    }

    private val services = resources.map { it.toService() }

    private var container: LocalStackContainer? = null

    private val myEnvMap = HashMap<String, String>()
    val envMap: Map<String, String>
        get() = myEnvMap

    private val myServiceMap = HashMap<AwsResource, String>()
    val serviceMap: Map<AwsResource, String>
        get() = myServiceMap

    fun start() {
        if (!isEnabled) return

        container = LocalStackContainer().withServices(*services.toTypedArray())

        container!!.start()

        myEnvMap["LOCALSTACK_ENABLED"] = "true"

        for (service in services) {
            val endpoint = container!!.getEndpointConfiguration(service)
            myEnvMap["LOCALSTACK_${service.localStackName.toUpperCase()}_URL"] = endpoint.serviceEndpoint
            myEnvMap["LOCALSTACK_${service.localStackName.toUpperCase()}_REGION"] = endpoint.signingRegion

            myServiceMap[service.toResource()] = endpoint.serviceEndpoint
        }

        val credentials = container!!.defaultCredentialsProvider.credentials

        myEnvMap["LOCALSTACK_ACCESSKEY"] = credentials.awsAccessKeyId
        myEnvMap["LOCALSTACK_SECRETKEY"] = credentials.awsSecretKey
    }

    fun stop() {
        container?.stop()
    }

    private fun AwsResource.toService() = LocalStackContainer.Service.valueOf(prefix.toUpperCase())
    private fun LocalStackContainer.Service.toResource() = AwsResource.values().find { it.prefix.toUpperCase() == name }!!
}
