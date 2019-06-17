package io.kotless.engine.terraform.aws.data

import io.kotless.engine.terraform.TfData
import io.kotless.engine.terraform.synthesizer.TfGroup

/**
 * Terraform aws_region data.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/d/region.html">aws_region</a>
 */
class TfAwsRegion(tfName: String) : TfData("aws_region", tfName) {
    val name = "$tfFullName.name"

    override val dataDef = ""

    override val group = TfGroup.Info
}

/**
 * Terraform aws_caller_identity data.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/d/caller_identity.html">aws_caller_identity</a>
 */
class TfAwsCallerIdentity(tfName: String) : TfData("aws_caller_identity", tfName) {
    val account_id = "$tfFullName.account_id"

    override val dataDef = ""

    override val group = TfGroup.Info
}

/** Object providing singleton data sources with information on a region and account */
object AwsInformation {
    @Suppress("ObjectPropertyName")
    private var _tfRegion: TfAwsRegion? = null
    val tfRegion: TfAwsRegion
        get() = _tfRegion ?: run {
            _tfRegion = TfAwsRegion("current")
            _tfRegion!!
        }

    @Suppress("ObjectPropertyName")
    private var _tfAccount: TfAwsCallerIdentity? = null
    val tfAccount: TfAwsCallerIdentity
        get() = AwsInformation._tfAccount ?: run {
            AwsInformation._tfAccount = TfAwsCallerIdentity("current")
            AwsInformation._tfAccount!!
        }

    fun cleanup() {
        AwsInformation._tfAccount = null
        _tfRegion = null
    }
}
