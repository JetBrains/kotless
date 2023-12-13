package io.kotless.plugin.gradle.tasks.local

import io.kotless.*
import io.kotless.plugin.gradle.tasks.local.LocalStackRunner.Start
import io.kotless.plugin.gradle.tasks.local.LocalStackRunner.Stop
import io.kotless.plugin.gradle.utils.gradle.Groups
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.ResourceReaper
import java.util.*
import kotlin.collections.HashMap

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
            localstack.stop(logger)
        }
    }

    private val services = resources.map { it.toService() }

    private lateinit var container: LocalStackContainer

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

        container.start()

        myEnvironment[Constants.LocalStack.enabled] = "true"

        for (service in services) {
            val endpoint = container.getEndpointConfiguration(service)

            myEnvironment[Constants.LocalStack.url(service.toResource())] = endpoint.serviceEndpoint
            myEnvironment[Constants.LocalStack.region(service.toResource())] = endpoint.signingRegion

            myServiceMap[service.toResource()] = endpoint.serviceEndpoint
        }

        val credentials = container.defaultCredentialsProvider.credentials

        myEnvironment[Constants.LocalStack.accessKey] = credentials.awsAccessKeyId
        myEnvironment[Constants.LocalStack.secretKey] = credentials.awsSecretKey

        ResourceReaper.instance().registerContainerForCleanup(container.containerId, container.dockerImageName)
    }

    fun stop(logger: Logger) {
        logger.lifecycle("Stopping LocalStack...")
        ResourceReaper.instance().performCleanup()
        logger.lifecycle("LocalStack stopped...")
    }

    private fun AwsResource.toService() = LocalStackContainer.Service.valueOf(prefix.uppercase(Locale.getDefault()))
    private fun LocalStackContainer.Service.toResource() = AwsResource.values().find { it.prefix.uppercase(Locale.getDefault()) == name }!!
}
