package io.kotless.gen.factory.event

import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.gen.factory.resource.dynamic.LambdaFactory
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.resource.cloudwatch.cloudwatch_event_rule
import io.kotless.terraform.provider.aws.resource.cloudwatch.cloudwatch_event_target
import io.kotless.terraform.provider.aws.resource.lambda.lambda_permission

object ScheduledEventsFactory : GenerationFactory<Webapp.Events.Scheduled, Unit> {
    override fun mayRun(entity: Webapp.Events.Scheduled, context: GenerationContext) = context.output.check(context.schema.lambdas[entity.lambda]!!, LambdaFactory)

    override fun generate(entity: Webapp.Events.Scheduled, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val lambda = context.output.get(context.schema.lambdas[entity.lambda]!!, LambdaFactory)

        val event_rule = cloudwatch_event_rule(Names.tf(entity.id)) {
            name = Names.aws(entity.id)
            schedule_expression = "cron(${entity.cron})"
        }

        val permission = lambda_permission(Names.tf(entity.id)) {
            statement_id = Names.aws(entity.id)
            action = "lambda:InvokeFunction"
            function_name = lambda.lambda_arn
            principal = "events.amazonaws.com"
            source_arn = event_rule::arn.ref
        }

        val target = cloudwatch_event_target(Names.tf(entity.id)) {
            rule = event_rule::name.ref
            arn = lambda.lambda_arn
        }

        return GenerationFactory.GenerationResult(Unit, event_rule, target, permission)
    }
}
