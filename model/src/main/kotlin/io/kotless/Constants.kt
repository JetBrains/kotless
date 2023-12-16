package io.kotless

import java.util.*

@InternalAPI
object Constants {
    object LocalStack {
        const val enabled = "LOCALSTACK_ENABLED"

        const val accessKey = "LOCALSTACK_ACCESSKEY"
        const val secretKey = "LOCALSTACK_SECRETKEY"

        fun url(resource: AwsResource) = "LOCALSTACK_${resource.prefix.uppercase(Locale.getDefault())}_URL"
        fun region(resource: AwsResource) = "LOCALSTACK_${resource.prefix.uppercase(Locale.getDefault())}_REGION"
    }

    object Local {
        const val serverPort = "SERVER_PORT"
        const val autowarmMinutes = "AUTOWARM_MINUTES"

        object Kotless {
            const val workingDir = "WORKING_DIR"
        }

        object KtorOrSpring {
            const val classToStart = "CLASS_TO_START"
        }
    }
}
