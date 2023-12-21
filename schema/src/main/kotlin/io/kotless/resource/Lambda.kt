package io.kotless.resource

import io.kotless.InternalAPI
import io.kotless.permission.AWSPermission
import io.kotless.permission.Permission
import io.kotless.utils.Visitable
import java.io.File

/**
 * Serverless function (Lambda)
 *
 * It is an executable code with integrated Kotless dispatcher.
 *
 * Lambda is used as a worker serving requests
 * from different interfaces (for example, ApiGateway)
 *
 * @param name a unique name of lambda
 * @param file file with function code
 * @param entrypoint entrypoint of function
 * @param config config of function: defines memory and time limit, etc.
 * @param config permissions to access other resources granted to this lambda
 */
@OptIn(InternalAPI::class)
data class Lambda(val name: String, val file: File, val entrypoint: Entrypoint, val config: Config, val permissions: Set<Permission>) : Visitable {

    /**
     * Entrypoint function definition
     *
     * @param qualifiedName a qualified name of entrypoint function
     */
    data class Entrypoint(val qualifiedName: String)

    /**
     * Configuration of lambda deployment
     *
     * @param memoryMb memory in megabytes available for a lambda
     * @param timeoutSec limit of lambda execution in seconds
     * @param environment environment variables available for lambda
     */
    data class Config(val memoryMb: Int, val timeoutSec: Int, val runtime: Runtime, val environment: Map<String, String>) {
        enum class Runtime(val aws: String) {
            Java8("java8"),
            Java11("java11"),
            Java17("java17"),
            Java21("java21"),
            GraalVM("provided.al2023"),
            Provided("provided")
        }
    }
}
