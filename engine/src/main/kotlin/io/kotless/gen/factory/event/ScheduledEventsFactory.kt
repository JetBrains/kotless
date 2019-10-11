package io.kotless.gen.factory.event

import io.kotless.Webapp
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.resource.dynamic.LambdaFactory
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.resource.cloudwatch.cloudwatch_event_rule
import io.kotless.terraform.provider.aws.resource.cloudwatch.cloudwatch_event_target
import io.kotless.terraform.provider.aws.resource.lambda.lambda_permission

object ScheduledEventsFactory : GenerationFactory<Webapp.Events.Scheduled, Unit> {
    override fun mayRun(entity: Webapp.Events.Scheduled, context: GenerationContext) = context.output.check(context.schema.lambdas[entity.lambda]!!, LambdaFactory)

    override fun generate(entity: Webapp.Events.Scheduled, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val lambda = context.output.get(context.schema.lambdas[entity.lambda]!!, LambdaFactory)

        val event_rule = cloudwatch_event_rule(context.names.tf(entity.fqId)) {
            name = context.names.aws(entity.fqId)
            schedule_expression = "cron(${entity.cron})"
        }

        val permission = lambda_permission(context.names.tf(entity.fqId)) {
            statement_id = context.names.aws(entity.fqId)
            action = "lambda:InvokeFunction"
            function_name = lambda.lambda_arn
            principal = "events.amazonaws.com"
            source_arn = event_rule::arn.ref
        }

        val target = cloudwatch_event_target(context.names.tf(entity.fqId)) {
            rule = event_rule::name.ref
            arn = lambda.lambda_arn
        }

        return GenerationFactory.GenerationResult(Unit, event_rule, target, permission)
    }
}
