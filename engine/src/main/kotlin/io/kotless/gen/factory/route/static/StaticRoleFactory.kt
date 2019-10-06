package io.kotless.gen.factory.route.static

import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.gen.factory.info.InfoFactory
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.data.iam.iam_policy_document
import io.kotless.terraform.provider.aws.resource.iam.iam_role
import io.kotless.terraform.provider.aws.resource.iam.iam_role_policy

object StaticRoleFactory : GenerationFactory<Webapp, StaticRoleFactory.StaticRoleOutput> {
    data class StaticRoleOutput(val role_arn: String, val role_name: String)

    override fun mayRun(entity: Webapp, context: GenerationContext) = context.output.check(context.webapp, InfoFactory)

    override fun generate(entity: Webapp, context: GenerationContext): GenerationFactory.GenerationResult<StaticRoleOutput> {
        val info = context.output.get(context.webapp, InfoFactory)


        val assume = iam_policy_document(Names.tf("kotless", "static", "assume")) {
            statement {
                principals {
                    type = "Service"
                    identifiers = arrayOf("apigateway.amazonaws.com")
                }

                actions = arrayOf("sts:AssumeRole")
            }
        }


        val iam_role = iam_role(Names.tf("kotless", "static", "role")) {
            name = Names.aws("kotless", "static", "role")
            assume_role_policy = assume::json.ref
        }

        val policy_document = iam_policy_document(Names.tf("kotless", "static", "policy")) {
            statement {
                effect = "Allow"
                resources = arrayOf("${info.kotless_bucket_arn}/*")
                actions = arrayOf("s3:GetObject")
            }
        }

        val role_policy = iam_role_policy(Names.tf("kotless", "static", "policy")) {
            role = iam_role::name.ref
            policy = policy_document::json.ref
        }

        return GenerationFactory.GenerationResult(StaticRoleOutput(iam_role::arn.ref, iam_role::name.ref), assume, iam_role, policy_document, role_policy)
    }
}

