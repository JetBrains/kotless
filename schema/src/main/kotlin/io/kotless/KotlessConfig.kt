package io.kotless

import io.kotless.utils.Visitable
import java.io.File

/**
 * Config of Kotless itself
 *
 * @param bucket name of bucket Kotless will use to store all files
 * @param prefix name with which will be prepended all Kotless created entities
 * @param dsl configuration of DSL that will be used for Kotless application
 * @param terraform terraform configuration used by Kotless
 * @param optimization optimizations considered during generation of code
 */
data class KotlessConfig(
    val bucket: String,
    val prefix: String,
    val dsl: DSL,
    val terraform: Terraform,
    val optimization: Optimization = Optimization()
) : Visitable {

    /**
     * Configuration of DSL used for this application
     * @param type type of dsl that is used
     * @param staticsRoot directory Kotless considers as root for a file resolving
     */
    data class DSL(val type: DSLType, val staticsRoot: File) : Visitable

    /**
     * Terraform configuration used by Kotless
     *
     * @param version version of Terraform used
     */
    data class Terraform(val version: String, val backend: Backend, val aws: AWSProvider, val locals: Map<String, String>) : Visitable {

        /**
         * Configuration of Terraform backend
         *
         * @param bucket name of bucket, that will be used as Terraform backend storage
         * @param key path in a bucket to store Terraform state
         * @param profile AWS profile from a local machine to use for Terraform state storing
         * @param region AWS region where state bucket is located
         */
        data class Backend(val bucket: String, val key: String, val profile: String, val region: String) : Visitable

        /**
         * Configuration of Terraform AWS provider
         *
         * @param version version of AWS provider to use
         * @param profile AWS profile from a local machine to use for Terraform operations authentication
         * @param region AWS region in context of which all Terraform operations should be performed
         */
        data class AWSProvider(val version: String, val profile: String, val region: String) : Visitable

        override fun visit(visitor: (Any) -> Unit) {
            aws.visit(visitor)
            backend.visit(visitor)
            visitor(this)
        }
    }

    /** Configuration of optimizations considered during code generation */
    data class Optimization(val mergeLambda: MergeLambda = MergeLambda.All, val autowarm: Autowarm = Autowarm(enable = true, minutes = 5)) : Visitable {

        /**
         * Optimization defines, if lambdas should be autowarmed and with what schedule
         *
         * Lambdas cannot be autowarmed with interval more than hour, since it has no practical sense
         */
        data class Autowarm(val enable: Boolean, val minutes: Int) : Visitable

        /**
         * Optimization defines, if different lambdas should be merged into one and when.
         *
         * Basically, lambda serving few endpoints is more likely to be warm.
         *
         * There are 3 levels of merge optimization:
         * * None -- lambdas will never be merged
         * * PerPermissions -- lambdas will be merged, if they have equal permissions
         * * All -- all lambdas in context are merged in one.
         */
        enum class MergeLambda {
            None,
            PerPermissions,
            All
        }

        override fun visit(visitor: (Any) -> Unit) {
            autowarm.visit(visitor)
            visitor(this)
        }
    }

    override fun visit(visitor: (Any) -> Unit) {
        dsl.visit(visitor)
        terraform.visit(visitor)
        optimization.visit(visitor)
        visitor(this)
    }
}
