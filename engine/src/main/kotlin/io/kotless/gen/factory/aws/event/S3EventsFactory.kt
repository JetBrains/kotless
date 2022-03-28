package io.kotless.gen.factory.aws.event

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.aws.resource.dynamic.LambdaFactory
import io.kotless.gen.factory.aws.resource.static.S3DataFactory
import io.terraformkt.aws.data.s3.S3Bucket
import io.terraformkt.aws.data.s3.s3_bucket
import io.terraformkt.aws.resource.lambda.lambda_permission
import io.terraformkt.aws.resource.s3.s3_bucket_notification
import io.terraformkt.hcl.ref

object S3EventsFactory : GenerationFactory<Application.Events.S3, Unit> {
    override fun mayRun(entity: Application.Events.S3, context: GenerationContext): Boolean {
        return context.output.check(context.schema.lambdas[entity.lambda]!!, LambdaFactory) &&
            context.entities.all().firstOrNull { it.second.hcl_ref == "data.aws_s3_bucket.${context.names.tf(entity.bucket)}" } != null
    }

    override fun generate(entity: Application.Events.S3, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val lambda = context.output.get(context.schema.lambdas[entity.lambda]!!, LambdaFactory)
        val s3Bucket = context.output.get(entity, S3DataFactory).s3Bucket

        val permission = lambda_permission(context.names.tf(entity.fqId)) {
            statement_id = "AllowExecutionFromS3Bucket"
            action = "lambda:InvokeFunction"
            function_name = lambda.lambda_arn
            principal = "s3.amazonaws.com"
            source_arn = s3Bucket::arn.ref
        }
        val notification = s3_bucket_notification(context.names.tf(entity.fqId)) {
            bucket = s3Bucket::bucket.ref
            lambdaFunction {
                lambda_function_arn = lambda.lambda_arn
                events = entity.types.map { "s3:$it" }.toTypedArray()
            }
        }
        return GenerationFactory.GenerationResult(Unit, s3Bucket, permission, notification)
    }
}
