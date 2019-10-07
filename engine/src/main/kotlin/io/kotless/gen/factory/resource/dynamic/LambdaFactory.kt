package io.kotless.gen.factory.resource.dynamic

import io.kotless.Lambda
import io.kotless.gen.*
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

    override fun mayRun(entity: Lambda, context: GenerationContext) = true

    override fun generate(entity: Lambda, context: GenerationContext): GenerationFactory.GenerationResult<LambdaOutput> {
        val obj = s3_object(Names.tf(entity.name)) {
            bucket = context.schema.kotlessConfig.bucket
            key = "kotless-lambdas/${Names.aws(entity.name)}.jar"
            source = path(entity.file)
            etag = eval(filemd5(entity.file))
        }

        val assume = iam_policy_document(Names.tf(entity.name, "assume")) {
            statement {
                principals {
                    type = "Service"
                    identifiers = arrayOf("lambda.amazonaws.com", "apigateway.amazonaws.com")
                }

                actions = arrayOf("sts:AssumeRole")
            }
        }

        val iam_role = iam_role(Names.tf(entity.name)) {
            name = Names.aws(entity.name)
            assume_role_policy = assume::json.ref
        }

        val policy_document = iam_policy_document(Names.tf(entity.name)) {
            statement {
                effect = "Allow"
                resources = entity.permissions.flatMap { it.cloudIds }.toTypedArray()
                actions = entity.permissions.flatMap { permission -> permission.actions.map { "${permission.resource.prefix}:$it" } }.toTypedArray()
            }
        }

        val role_policy = iam_role_policy(Names.tf(entity.name)) {
            role = iam_role::name.ref
            policy = policy_document::json.ref
        }

        val lambda = lambda_function(Names.tf(entity.name)) {
            function_name = Names.tf(entity.name)

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
