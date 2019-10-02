package io.kotless.gen.factory.resource.dynamic

import io.kotless.Lambda
import io.kotless.gen.*
import io.kotless.hcl.ref
import io.kotless.terraform.functions.base64sha256
import io.kotless.terraform.functions.file
import io.kotless.terraform.provider.aws.resource.lambda.lambda_function
import io.kotless.terraform.provider.aws.resource.s3.s3_object


object LambdaFactory : GenerationFactory<Lambda, LambdaFactory.LambdaOutput> {
    data class LambdaOutput(val lambda_arn: String)

    override fun mayRun(entity: Lambda, context: GenerationContext) = true

    override fun generate(entity: Lambda, context: GenerationContext): GenerationFactory.GenerationResult<LambdaOutput> {
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

            source_code_hash = base64sha256(file(obj.source))

            //add here role
        }

        return GenerationFactory.GenerationResult(LambdaOutput(lambda::arn.ref), obj, lambda)
    }
}
