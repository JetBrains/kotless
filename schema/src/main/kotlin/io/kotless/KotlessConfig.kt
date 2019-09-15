package io.kotless

import java.io.File

/** Config of Kotless itself */
data class KotlessConfig(
    /** Name of bucket Kotless will use to store all files */
    val bucket: String,
    /** Name with which will be prepended all Kotless created entities */
    val resourcePrefix: String,
    /** Directory Kotless considers as root for a file resolving */
    val workDirectory: File,
    /** The local directory Kotless will use to store generated files */
    val genDirectory: File,
    /** Terraform configuration used by Kotless */
    val terraform: Terraform,
    /** Optimizations considered during generation of code */
    val optimization: Optimization = Optimization()) : Visitable {

    /** Terraform configuration used by Kotless */
    data class Terraform(
        /** Version of Terraform used */
        val version: String,
        val backend: Backend,
        val aws: AWSProvider) : Visitable {

        /** Configuration of Terraform backend */
        data class Backend(
            /** Name of bucket, that will be used as Terraform backend storage */
            val bucket: String,
            /** Path in a bucket to store Terraform state */
            val key: String,
            /** AWS profile from a local machine to use for Terraform state storing */
            val profile: String,
            /** AWS region where state bucket is located */
            val region: String)

        /** Configuration of Terraform AWS provider */
        data class AWSProvider(
            /** Version of AWS provider to use */
            val version: String,
            /** AWS profile from a local machine to use for Terraform operations authentication */
            val profile: String,
            /** AWS region in context of which all Terraform operations should be performed */
            val region: String) : Visitable

        override fun visit(visitor: (Any) -> Unit) {
            aws.visit(visitor)
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
