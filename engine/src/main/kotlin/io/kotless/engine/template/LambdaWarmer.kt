package io.kotless.engine.template

import io.kotless.engine.terraform.aws.cloudwatch.TfCloudwatchEventRule
import io.kotless.engine.terraform.aws.cloudwatch.TfCloudwatchEventTarget
import io.kotless.engine.terraform.aws.lambda.TfLambda
import io.kotless.engine.terraform.utils.toAwsName
import io.kotless.engine.terraform.utils.toTfName

/**
 * CloudWatch rule and target to warm up lambda
 *
 * Created CloudWatch rule will send CloudWatch event to
 * lambda. Kotless dispatcher will trigger warm up sequence
 * when got such an event.
 */
data class LambdaWarmer(val tfLambda: TfLambda, val minutes: Int) {
    val eventRule = TfCloudwatchEventRule(tfLambda.tfFullName.toTfName("warmer", "rule"),
            tfLambda.awsName.toAwsName("warmer"), "cron(0/$minutes * * * ? *)")
    val eventTarget = TfCloudwatchEventTarget(tfLambda.tfFullName.toTfName("warmer", "target"), tfLambda, eventRule)
}
