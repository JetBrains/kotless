package io.kotless

import java.io.File

/**
 * Serverless function (Lambda)
 *
 * It is an executable code with integrated Kotless dispatcher.
 *
 * Lambda is used as a worker serving requests
 * from different interfaces (for example, ApiGateway)
 */
data class Lambda(
        /** A unique name of lambda */
        val name: String,
        /** File with function code */
        val file: File,
        /** Entrypoint of function */
        val entrypoint: Entrypoint,
        /** Config of function: defines memory and time limit, etc. */
        val config: Config,
        /** Permissions to access other resources granted to this lambda */
        val permissions: Set<Permission>): Visitable {

    /** Entrypoint function definition */
    data class Entrypoint(
            /** The qualified name of entrypoint function */
            val qualifiedName: String,
            /** Params of entrypoint function */
            val params: Set<Param>) {
        data class Param(
                /** Name of a function parameter */
                val name: String,
                /** A fully qualified name of type of parameter */
                val type: String)
    }

    /** Configuration of lambda deployment */
    data class Config(
            /** Memory in megabytes available for a lambda */
            val memoryMb: Int,
            /** Limit of lambda execution in seconds */
            val timeoutSec: Int,
            /** Should this lambda be autowarmed, or not */
            val autowarm: Boolean,
            /** Period in minutes between warm invocations */
            val autowarmMinutes: Int,
            /** Packages that lambda dispatcher should scan for annotated classes */
            val packages: Set<String>)
}
