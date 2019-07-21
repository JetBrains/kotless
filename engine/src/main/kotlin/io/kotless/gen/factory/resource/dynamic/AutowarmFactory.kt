package io.kotless.gen.factory.resource.dynamic

import io.kotless.Lambda
import io.kotless.gen.*
import io.kotless.terraform.provider.aws.resource.cloudwatch.cloudwatch_event_rule
import io.kotless.terraform.provider.aws.resource.cloudwatch.cloudwatch_event_target

object AutowarmFactory : GenerationFactory<Lambda, Unit> {
    override fun mayRun(entity: Lambda, context: GenerationContext) = context.check(entity, LambdaFactory)

    override fun generate(entity: Lambda, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        if (!entity.config.autowarm) return GenerationFactory.GenerationResult(Unit)

        val lambda = context.get(entity, LambdaFactory)

        val eventRule = cloudwatch_event_rule(Names.tf(entity.name)) {
            name = Names.aws(entity.name)
            schedule_expression = "cron(0/${entity.config.autowarmMinutes} * * * ? *)"
        }

        val target = cloudwatch_event_target(Names.tf(entity.name)) {
            rule = eventRule.name
            arn = lambda.lambda_arn
        }

        return GenerationFactory.GenerationResult(Unit, eventRule, target)
    }
}
