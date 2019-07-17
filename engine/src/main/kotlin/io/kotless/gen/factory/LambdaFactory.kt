package io.kotless.gen.factory

import io.kotless.Lambda
import io.kotless.gen.*
import io.kotless.hcl.HCLNamed
import io.kotless.terraform.provider.aws.resource.cloudwatch.cloudwatch_event_rule
import io.kotless.terraform.provider.aws.resource.cloudwatch.cloudwatch_event_target
import io.kotless.terraform.provider.aws.resource.lambda.lambda_function
import io.kotless.terraform.provider.aws.resource.s3.s3_object

object LambdaFactory : KotlessFactory<Lambda> {
    override fun get(entity: Lambda, context: KotlessGenerationContext): Set<HCLNamed> {
        val obj = s3_object(Names.tf(entity.name)) {
            bucket = context.schema.kotlessConfig.bucket
            key = "kotless-lambdas/${Names.aws(entity.name)}.jar"
            source = entity.file.absolutePath
        }

        val lambda = lambda_function(Names.tf(entity.name)) {
            function_name = Names.tf(entity.name)

            s3_bucket = obj.bucket
            s3_key = obj.key

            handler = entity.entrypoint.qualifiedName
            runtime = "java8"

            memory_size = entity.config.memoryMb
            timeout = entity.config.timeoutSec

            source_code_hash = "base64sha256(file(${obj.source}))"

            //add here role
        }

        val autowarm = if (entity.config.autowarm) {
            val eventRule = cloudwatch_event_rule(Names.tf(entity.name)) {
                name = Names.aws(entity.name)
                schedule_expression = "cron(0/${entity.config.autowarmMinutes} * * * ? *)"
            }

            val target = cloudwatch_event_target(Names.tf(entity.name)) {
                rule = eventRule.name
                arn = lambda.arn
            }

            setOf(eventRule, target)
        } else emptySet()

        return setOf(obj, lambda) + autowarm
    }
}
