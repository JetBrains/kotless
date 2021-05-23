package io.kotless.gen.factory.aws.resource.dynamic

import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.aws.info.InfoFactory
import io.kotless.resource.Lambda
import io.kotless.terraform.functions.*
import io.terraformkt.aws.data.iam.iam_policy_document
import io.terraformkt.aws.resource.iam.iam_role
import io.terraformkt.aws.resource.iam.iam_role_policy
import io.terraformkt.aws.resource.lambda.lambda_function
import io.terraformkt.aws.resource.s3.s3_bucket_object
import io.terraformkt.hcl.ref


object LambdaFactory : GenerationFactory<Lambda, LambdaFactory.Output> {
    data class Output(val lambda_arn: String, val lambda_name: String)

    override fun mayRun(entity: Lambda, context: GenerationContext) = context.output.check(context.webapp, InfoFactory)

    override fun generate(entity: Lambda, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val info = context.output.get(context.webapp, InfoFactory)

        val obj = s3_bucket_object(context.names.tf(entity.name)) {
            bucket = context.schema.config.storage
            key = "kotless-lambdas/${context.names.aws(entity.name)}.jar"
            source = path(entity.file)
            etag = eval(filemd5(entity.file))
        }

        val assume = iam_policy_document(context.names.tf(entity.name, "assume")) {
            statement {
                principals {
                    type = "Service"
                    identifiers = arrayOf("lambda.amazonaws.com", "apigateway.amazonaws.com")
                }

                actions = arrayOf("sts:AssumeRole")
            }
        }

        val iam_role = iam_role(context.names.tf(entity.name)) {
            name = context.names.aws(entity.name)
            assume_role_policy = assume::json.ref
        }

        val policy_document = iam_policy_document(context.names.tf(entity.name)) {
            for (permission in entity.permissions.sortedBy { it.resource }) {
                statement {
                    effect = "Allow"
                    resources = permission.cloudIds(info.region_name, info.account_id).toTypedArray()
                    actions = permission.actions.map { "${permission.resource.prefix}:$it" }.toTypedArray()
                }
            }
        }

        val role_policy = iam_role_policy(context.names.tf(entity.name)) {
            role = iam_role::name.ref
            policy = policy_document::json.ref
        }

        val lambda = lambda_function(context.names.tf(entity.name)) {
            function_name = context.names.aws(entity.name)

            s3_bucket = obj.bucket
            s3_key = obj.key

            handler = entity.entrypoint.qualifiedName
            runtime = entity.config.runtime.aws

            memory_size = entity.config.memoryMb
            timeout = entity.config.timeoutSec

            source_code_hash = eval(filesha256(obj::source.ref))

            role = iam_role::arn.ref

            if (entity.config.environment.isNotEmpty()) {
                environment {
                    variables(entity.config.environment)
                }
            }
        }

        return GenerationFactory.GenerationResult(
            Output(lambda::arn.ref, lambda::function_name.ref), obj, lambda, assume, iam_role, policy_document, role_policy
        )
    }
}
