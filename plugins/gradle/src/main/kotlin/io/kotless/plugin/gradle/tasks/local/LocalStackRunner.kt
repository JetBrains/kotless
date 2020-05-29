package io.kotless.plugin.gradle.tasks.local

import io.kotless.AwsResource
import io.kotless.Constants
import io.kotless.InternalAPI
import io.kotless.plugin.gradle.tasks.local.LocalStackRunner.Start
import io.kotless.plugin.gradle.tasks.local.LocalStackRunner.Stop
import io.kotless.plugin.gradle.utils.Groups
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.testcontainers.containers.localstack.LocalStackContainer

/**
 * Class composing tasks to work with LocalStack.
 *
 * Includes [Start] task that starts LocalStack and
 * [Stop] task that stops LocalStack
 */
internal class LocalStackRunner(private val isEnabled: Boolean, resources: Set<AwsResource>) {
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

    private val myEnvironment = HashMap<String, String>()
    val environment: Map<String, String>
        get() = myEnvironment

    private val myServiceMap = HashMap<AwsResource, String>()
    val serviceMap: Map<AwsResource, String>
        get() = myServiceMap

    @OptIn(InternalAPI::class)
    fun start() {
        if (!isEnabled) return

        container = LocalStackContainer().withServices(*services.toTypedArray())

        container!!.start()

        myEnvironment[Constants.LocalStack.enabled] = "true"

        for (service in services) {
            val endpoint = container!!.getEndpointConfiguration(service)

            myEnvironment[Constants.LocalStack.url(service.toResource())] = endpoint.serviceEndpoint
            myEnvironment[Constants.LocalStack.region(service.toResource())] = endpoint.signingRegion

            myServiceMap[service.toResource()] = endpoint.serviceEndpoint
        }

        val credentials = container!!.defaultCredentialsProvider.credentials

        myEnvironment[Constants.LocalStack.accessKey] = credentials.awsAccessKeyId
        myEnvironment[Constants.LocalStack.secretKey] = credentials.awsSecretKey
    }

    fun stop() {
        container?.stop()
    }

    private fun AwsResource.toService() = LocalStackContainer.Service.valueOf(prefix.toUpperCase())
    private fun LocalStackContainer.Service.toResource() = AwsResource.values().find { it.prefix.toUpperCase() == name }!!
}
