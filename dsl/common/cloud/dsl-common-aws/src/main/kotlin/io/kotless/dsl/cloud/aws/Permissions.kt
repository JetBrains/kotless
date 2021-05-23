package io.kotless.dsl.cloud.aws

import io.kotless.PermissionLevel

/** Delegates permissions to specified bucket to entity with annotation */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class S3Bucket(val bucket: String, val level: PermissionLevel)

/** Delegates permissions to specified parameters' prefix in SSM to entity with annotation */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class SSMParameters(val prefix: String, val level: PermissionLevel)

/** Delegates permissions to specified DynamoDB table to entity with annotation */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class DynamoDBTable(val table: String, val level: PermissionLevel)

/** Delegates permissions to specified SQS queue to entity with annotation */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class SQSQueue(val queueName: String, val level: PermissionLevel)
