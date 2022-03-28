package io.kotless

import io.kotless.utils.Visitable
import java.io.File

/**
 * Configuration of Kotless itself -- its deployment and analysis
 *
 * @param cloud is a configuration defining deployment configuration and providing access to the cloud platform
 * @param dsl configuration of DSL that will be used for Kotless application
 * @param optimization optimizations considered during generation of code
 */
data class KotlessConfig(
    val cloud: Cloud<*, *>,
    val dsl: DSL,
    val optimization: Optimization = Optimization(),
) : Visitable {

    @InternalAPI
    val aws: Cloud.AWS
        get() = cloud as Cloud.AWS

    @InternalAPI
    val azure: Cloud.Azure
        get() = cloud as Cloud.Azure

    /**
     * Definition of the cloud platform and access to it
     *
     * @param prefix is a prefix with which all the created resources will be prepended
     * @param storage is a storage that should be used to store all Kotless-related data
     * @param terraform is a configuration of Terraform that should be used during the deployment
     * @param platform is a type of cloud platform to which the deployment will be performed
     */
    sealed class Cloud<T : Cloud.Terraform<*, *>, S : Cloud.Storage>(
        val prefix: String, val storage: S, val terraform: T, val platform: CloudPlatform
    ) : Visitable {

        /**
         * Definition of a cloud platform powered storage
         *
         * Storage may be used to store Terraform backend data or Kotless-related data
         */
        sealed class Storage : Visitable {
            /** S3-based cloud platform storage */
            class S3(val bucket: String, val region: String) : Storage()

            /** Azure Blob-based cloud platform storage */
            class AzureBlob(val container: String, val storageAccount: String) : Storage()
        }

        /** Microsoft Azure cloud platform Kotless configuration */
        class Azure(prefix: String, blob: Storage.AzureBlob, terraform: Terraform.Azure) :
            Cloud<Terraform.Azure, Storage.AzureBlob>(prefix, blob, terraform, CloudPlatform.Azure)

        /** AWS cloud platform Kotless configuration */
        class AWS(prefix: String, s3: Storage.S3, terraform: Terraform.AWS) : Cloud<Terraform.AWS, Storage.S3>(prefix, s3, terraform, CloudPlatform.AWS)


        /**
         * Terraform configuration used by Kotless
         *
         * @param version version of Terraform used
         * @param backend is a backend configuration used by Terraform
         * @param provider is a provider used by Terraform
         */
        sealed class Terraform<B : Terraform.Backend, P : Terraform.Provider>(val version: String, val backend: B, val provider: P) : Visitable {
            /** AWS-related Terraform configuration used by Kotless */
            class AWS(version: String, backend: Backend.AWS, provider: Provider.AWS) : Terraform<Backend.AWS, Provider.AWS>(version, backend, provider)

            /** Azure-related Terraform configuration used by Kotless */
            class Azure(version: String, backend: Backend.Azure, provider: Provider.Azure) :
                Terraform<Backend.Azure, Provider.Azure>(version, backend, provider)


            /**
             * Configuration of Terraform backend
             */
            sealed class Backend : Visitable {
                /**
                 * Configuration of AWS Terraform backend
                 *
                 * @param storage is a storage used to store Terraform backend
                 * @param key path in a bucket to store Terraform state
                 * @param profile AWS profile from a local machine to use for Terraform state storing
                 */
                class AWS(val storage: Storage.S3, val key: String, val profile: String) : Backend()


                /**
                 * Configuration of Azure Terraform backend
                 *
                 * @param storage is a storage used to store Terraform backend
                 * @param key path in a bucket to store Terraform state
                 * @param resourceGroup is a resource group to which the whole deployment should be performed
                 */
                class Azure(val storage: Storage.AzureBlob, val key: String, val resourceGroup: String) : Backend()
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
            storage.visit(visitor)
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
