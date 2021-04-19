package io.kotless.gen.factory.aws.event

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.aws.resource.dynamic.LambdaFactory
import io.terraformkt.aws.resource.cloudwatch.cloudwatch_event_rule
import io.terraformkt.aws.resource.cloudwatch.cloudwatch_event_target
import io.terraformkt.aws.resource.lambda.lambda_permission
import io.terraformkt.hcl.ref

object ScheduledEventsFactory : GenerationFactory<Application.Events.Scheduled, Unit> {
    override fun mayRun(entity: Application.Events.Scheduled, context: GenerationContext): Boolean {
        return context.output.check(context.schema.lambdas[entity.lambda]!!, LambdaFactory)
    }

    override fun generate(entity: Application.Events.Scheduled, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
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
