package io.kotless.gen.factory.resource.dynamic

import io.kotless.Lambda
import io.kotless.gen.*
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.resource.cloudwatch.cloudwatch_event_rule
import io.kotless.terraform.provider.aws.resource.cloudwatch.cloudwatch_event_target
import io.kotless.terraform.provider.aws.resource.lambda.lambda_permission

object AutowarmFactory : GenerationFactory<Lambda, Unit> {
    override fun mayRun(entity: Lambda, context: GenerationContext) = context.output.check(entity, LambdaFactory)

    override fun generate(entity: Lambda, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        if (!entity.config.autowarm) return GenerationFactory.GenerationResult(Unit)

        val lambda = context.output.get(entity, LambdaFactory)

        val event_rule = cloudwatch_event_rule(Names.tf(entity.name)) {
            name = Names.aws(entity.name)
            schedule_expression = "cron(0/${entity.config.autowarmMinutes} * * * ? *)"
        }

        val permission = lambda_permission(Names.tf(entity.name)) {
            statement_id = Names.aws(entity.name)
            action = "lambda:InvokeFunction"
            function_name = lambda.lambda_arn
            principal = "events.amazonaws.com"
            source_arn = event_rule::arn.ref
        }

        val target = cloudwatch_event_target(Names.tf(entity.name)) {
            rule = event_rule::name.ref
            arn = lambda.lambda_arn
        }

        return GenerationFactory.GenerationResult(Unit, event_rule, target, permission)
    }
}
