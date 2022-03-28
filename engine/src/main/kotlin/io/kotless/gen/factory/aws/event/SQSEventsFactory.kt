package io.kotless.gen.factory.aws.event

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.aws.resource.dynamic.LambdaFactory
import io.kotless.gen.factory.aws.resource.static.SQSDataFactory
import io.terraformkt.aws.data.iam.iam_policy_document
import io.terraformkt.aws.resource.iam.iam_policy
import io.terraformkt.aws.resource.iam.iam_role_policy_attachment
import io.terraformkt.aws.resource.lambda.lambda_event_source_mapping
import io.terraformkt.hcl.ref

object SQSEventsFactory : GenerationFactory<Application.Events.SQS, Unit> {
    override fun mayRun(entity: Application.Events.SQS, context: GenerationContext): Boolean {
        return context.output.check(context.schema.lambdas[entity.lambda]!!, LambdaFactory) &&
            context.output.check(entity, SQSDataFactory)
    }

    override fun generate(entity: Application.Events.SQS, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val lambda = context.output.get(context.schema.lambdas[entity.lambda]!!, LambdaFactory)
        val sqsQueue = context.output.get(entity, SQSDataFactory).sqsQueue

        val policyDocument = iam_policy_document(context.names.tf(entity.fqId)) {
            statement {
                effect = "Allow"
                actions = arrayOf(
                    "sqs:DeleteMessage",
                    "sqs:GetQueueAttributes",
                    "sqs:ReceiveMessages",
                    "sqs:ReceiveMessage"
                )

                resources = arrayOf(
                    sqsQueue::arn.ref,
                    "${sqsQueue::arn.ref}/*"
                )
            }
        }

        val iamPolicy = iam_policy(context.names.tf(entity.fqId)) {
            name = "${sqsQueue::name.ref}-sqs-reader-policy"
            policy = policyDocument::json.ref
        }

        val iamPolicyAttachment = iam_role_policy_attachment(context.names.tf(entity.fqId)) {
            role = lambda.lambda_instance_role_name
            policy_arn = iamPolicy::arn.ref
        }

//        val permission = lambda_permission(context.names.tf(entity.fqId)) {
//            statement_id = "AllowExecutionFromSQS"
//            action = "lambda:InvokeFunction"
//            function_name = lambda.lambda_arn
//            principal = "s3.amazonaws.com"
//            source_arn = sqsQueue::arn.ref
//        }
        val eventSourceMapping = lambda_event_source_mapping(context.names.tf(entity.fqId)) {
            event_source_arn = sqsQueue::arn.ref
            function_name = lambda.lambda_name
            batch_size = 1
        }
        return GenerationFactory.GenerationResult(Unit, policyDocument, iamPolicy, iamPolicyAttachment, eventSourceMapping)
    }
}
