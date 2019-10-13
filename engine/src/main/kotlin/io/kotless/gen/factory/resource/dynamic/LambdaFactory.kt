package io.kotless.gen.factory.resource.dynamic

import io.kotless.Lambda
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.info.InfoFactory
import io.kotless.hcl.HCLEntity
import io.kotless.hcl.ref
import io.kotless.terraform.functions.*
import io.kotless.terraform.provider.aws.data.iam.iam_policy_document
import io.kotless.terraform.provider.aws.resource.iam.iam_role
import io.kotless.terraform.provider.aws.resource.iam.iam_role_policy
import io.kotless.terraform.provider.aws.resource.lambda.lambda_function
import io.kotless.terraform.provider.aws.resource.s3.s3_object


object LambdaFactory : GenerationFactory<Lambda, LambdaFactory.LambdaOutput> {
    data class LambdaOutput(val lambda_arn: String, val lambda_name: String)

    override fun mayRun(entity: Lambda, context: GenerationContext) = context.output.check(context.webapp, InfoFactory)

    override fun generate(entity: Lambda, context: GenerationContext): GenerationFactory.GenerationResult<LambdaOutput> {
        val info = context.output.get(context.webapp, InfoFactory)

        val obj = s3_object(context.names.tf(entity.name)) {
            bucket = context.schema.config.bucket
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
            for (permission in entity.permissions) {
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
            runtime = "java8"

            memory_size = entity.config.memoryMb
            timeout = entity.config.timeoutSec

            source_code_hash = eval(base64sha256(file(obj::source.ref)))

            role = iam_role::arn.ref

            environment {
                variables = object : HCLEntity() {
                    val packages by text(name = "KOTLESS_PACKAGES", default = entity.config.packages.joinToString())
                }
            }
        }

        return GenerationFactory.GenerationResult(LambdaOutput(lambda::arn.ref, lambda::function_name.ref), obj, lambda, assume, iam_role, policy_document, role_policy)
    }
}
