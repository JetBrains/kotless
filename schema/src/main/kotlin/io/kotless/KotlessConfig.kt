package io.kotless

import java.io.File

/**
 * Config of Kotless itself
 *
 * @param bucket name of bucket Kotless will use to store all files
 * @param resourcePrefix name with which will be prepended all Kotless created entities
 * @param workDirectory directory Kotless considers as root for a file resolving
 * @param genDirectory the local directory Kotless will use to store generated files
 * @param terraform terraform configuration used by Kotless
 * @param optimization optimizations considered during generation of code
 */
data class KotlessConfig(val bucket: String, val resourcePrefix: String, val workDirectory: File, val genDirectory: File, val terraform: Terraform,
                         val optimization: Optimization = Optimization()) : Visitable {

    /**
     * Terraform configuration used by Kotless
     *
     * @param version version of Terraform used
     */
    data class Terraform(val version: String, val backend: Backend, val aws: AWSProvider) : Visitable {

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
    data class Optimization(val mergeLambda: MergeLambda = MergeLambda.All) : Visitable {
        /**
         * Optimization defines, if different lambdas should be merged into one and when.
         *
         * Basically, lambda serving few endpoints is more likely to be warm.
         *
         * There are 3 levels of merge optimization:
         * * None -- lambdas will never be merged
         * * PerPermissions -- lambdas will be merged, if they have equal permissions
         * * All -- all lambdas in context are merged in one
         */
        enum class MergeLambda {
            None,
            PerPermissions,
            All
        }
    }

    override fun visit(visitor: (Any) -> Unit) {
        terraform.visit(visitor)
        optimization.visit(visitor)
        visitor(this)
    }
}
