package io.kotless.gen.factory.aws.route.static

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.aws.info.InfoFactory
import io.terraformkt.aws.data.iam.iam_policy_document
import io.terraformkt.aws.resource.iam.iam_role
import io.terraformkt.aws.resource.iam.iam_role_policy
import io.terraformkt.hcl.ref


object StaticRoleFactory : GenerationFactory<Application, StaticRoleFactory.Output> {
    data class Output(val role_arn: String, val role_name: String)

    override fun mayRun(entity: Application, context: GenerationContext) = context.output.check(context.webapp, InfoFactory)

    override fun generate(entity: Application, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val info = context.output.get(context.webapp, InfoFactory)


        val assume = iam_policy_document(context.names.tf("kotless", "static", "assume")) {
            statement {
                principals {
                    type = "Service"
                    identifiers = arrayOf("apigateway.amazonaws.com")
                }

                actions = arrayOf("sts:AssumeRole")
            }
        }


        val iam_role = iam_role(context.names.tf("kotless", "static", "role")) {
            name = context.names.aws("kotless", "static", "role")
            assume_role_policy = assume::json.ref
        }

        val policy_document = iam_policy_document(context.names.tf("kotless", "static", "policy")) {
            statement {
                effect = "Allow"
                resources = arrayOf("${info.kotless_bucket_arn}/*")
                actions = arrayOf("s3:GetObject")
            }
        }

        val role_policy = iam_role_policy(context.names.tf("kotless", "static", "policy")) {
            role = iam_role::name.ref
            policy = policy_document::json.ref
        }

        return GenerationFactory.GenerationResult(Output(iam_role::arn.ref, iam_role::name.ref), assume, iam_role, policy_document, role_policy)
    }
}

