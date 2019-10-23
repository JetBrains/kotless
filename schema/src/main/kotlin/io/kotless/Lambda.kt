package io.kotless

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
 * @param config сonfig of function: defines memory and time limit, etc.
 * @param config permissions to access other resources granted to this lambda
 */
data class Lambda(val name: String, val file: File, val entrypoint: Entrypoint, val config: Config, val permissions: Set<Permission>) : Visitable {

    /**
     * Entrypoint function definition
     *
     * @param qualifiedName a qualified name of entrypoint function
     * @param params params of entrypoint function
     */
    data class Entrypoint(val qualifiedName: String, val params: Set<Param>) {
        data class Param(val name: String, val type: String)
    }

    /**
     * Configuration of lambda deployment
     *
     * @param memoryMb memory in megabytes available for a lambda
     * @param timeoutSec limit of lambda execution in seconds
     * @param environment environment variables available for lambda
     */
    data class Config(val memoryMb: Int, val timeoutSec: Int, val environment: Map<String, String>)
}
