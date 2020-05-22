package io.kotless.plugin.gradle.tasks.local

import io.kotless.*
import io.kotless.plugin.gradle.utils.Groups
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.testcontainers.containers.localstack.LocalStackContainer

class LocalStackRunner(val isEnabled: Boolean, resources: Set<AwsResource>) {
    open class Start : DefaultTask() {
        init {
            group = Groups.`kotless setup`
        }

        @get:Internal
        lateinit var localstack: LocalStackRunner

        @TaskAction
        fun act() {
            localstack.start()
        }
    }

    open class Stop : DefaultTask() {
        init {
            group = Groups.`kotless setup`
        }

        @get:Internal
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

    @UseExperimental(InternalAPI::class)
    fun start() {
        if (!isEnabled) return

        container = LocalStackContainer().withServices(*services.toTypedArray())

        container!!.start()

        myEnvMap[Constants.LocalStack.enabled] = "true"

        for (service in services) {
            val endpoint = container!!.getEndpointConfiguration(service)

            myEnvMap[Constants.LocalStack.url(service.toResource())] = endpoint.serviceEndpoint
            myEnvMap[Constants.LocalStack.region(service.toResource())] = endpoint.signingRegion

            myServiceMap[service.toResource()] = endpoint.serviceEndpoint
        }

        val credentials = container!!.defaultCredentialsProvider.credentials

        myEnvMap[Constants.LocalStack.accessKey] = credentials.awsAccessKeyId
        myEnvMap[Constants.LocalStack.secretKey] = credentials.awsSecretKey
    }

    fun stop() {
        container?.stop()
    }

    private fun AwsResource.toService() = LocalStackContainer.Service.valueOf(prefix.toUpperCase())
    private fun LocalStackContainer.Service.toResource() = AwsResource.values().find { it.prefix.toUpperCase() == name }!!
}
