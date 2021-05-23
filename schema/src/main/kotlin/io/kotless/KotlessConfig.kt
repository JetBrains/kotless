package io.kotless

import io.kotless.utils.Visitable
import java.io.File

/**
 * Config of Kotless itself
 *
 * @param storage name of bucket Kotless will use to store all files
 * @param prefix name with which will be prepended all Kotless created entities
 * @param dsl configuration of DSL that will be used for Kotless application
 * @param terraform terraform configuration used by Kotless
 * @param optimization optimizations considered during generation of code
 * @param cloud type of the cloud
 */
data class KotlessConfig(
    val storage: String,
    val prefix: String,
    val cloud: Cloud<*, *>,
    val dsl: DSL,
    val optimization: Optimization = Optimization(),
) : Visitable {


    sealed class Cloud<B : Cloud.Terraform.Backend, P : Cloud.Terraform.Provider>(val terraform: Terraform<B, P>, val platform: CloudPlatform) : Visitable {
        class Azure(terraform: Terraform<Terraform.Backend.Azure, Terraform.Provider.Azure>) :
            Cloud<Terraform.Backend.Azure, Terraform.Provider.Azure>(terraform, CloudPlatform.Azure)

        class AWS(terraform: Terraform<Terraform.Backend.AWS, Terraform.Provider.AWS>) :
            Cloud<Terraform.Backend.AWS, Terraform.Provider.AWS>(terraform, CloudPlatform.AWS)


        /**
         * Terraform configuration used by Kotless
         *
         * @param version version of Terraform used
         */
        data class Terraform<B : Terraform.Backend, P : Terraform.Provider>(val version: String, val backend: B, val provider: P) : Visitable {

            /**
             * Configuration of Terraform backend
             */
            sealed class Backend : Visitable {
                /**
                 * Configuration of AWS Terraform backend
                 *
                 * @param bucket name of bucket, that will be used as Terraform backend storage
                 * @param key path in a bucket to store Terraform state
                 * @param profile AWS profile from a local machine to use for Terraform state storing
                 * @param region AWS region where state bucket is located
                 */
                class AWS(val bucket: String, val key: String, val profile: String, val region: String) : Backend()


                /**
                 * Configuration of Azure Terraform backend
                 *
                 */
                class Azure(val containerName: String, val key: String, val resourceGroup: String, val storageAccountName: String) : Backend()
            }


            sealed class Provider(val version: String) : Visitable {
                /**
                 * Configuration of Terraform AWS provider
                 *
                 * @param version version of AWS provider to use
                 * @param profile AWS profile from a local machine to use for Terraform operations authentication
                 * @param region AWS region in context of which all Terraform operations should be performed
                 */
                class AWS(version: String, val profile: String, val region: String) : Provider(version)

                /**
                 * Configuration of Terraform Azure provider
                 *
                 * @param version version of Azure provider to use
                 */
                class Azure(version: String) : Provider(version)
            }

            override fun visit(visitor: (Any) -> Unit) {
                provider.visit(visitor)
                backend.visit(visitor)
                visitor(this)
            }
        }

        override fun visit(visitor: (Any) -> Unit) {
            terraform.visit(visitor)
            visitor(this)
        }
    }


    /**
     * Configuration of DSL used for this application
     * @param type type of dsl that is used
     * @param staticsRoot directory Kotless considers as root for a file resolving
     */
    data class DSL(val type: DSLType, val staticsRoot: File) : Visitable

    /** Configuration of optimizations considered during code generation */
    data class Optimization(val mergeLambda: MergeLambda = MergeLambda.All, val autoWarm: AutoWarm = AutoWarm(enable = true, minutes = 5)) : Visitable {

        /**
         * Optimization defines, if lambdas should be autowarmed and with what schedule
         *
         * Lambdas cannot be autowarmed with interval more than hour, since it has no practical sense
         */
        data class AutoWarm(val enable: Boolean, val minutes: Int) : Visitable

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
            autoWarm.visit(visitor)
            visitor(this)
        }
    }

    override fun visit(visitor: (Any) -> Unit) {
        dsl.visit(visitor)
        cloud.visit(visitor)
        optimization.visit(visitor)
        visitor(this)
    }
}
