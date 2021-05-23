package io.kotless.parser.processor.permission

import io.kotless.AwsResource
import io.kotless.PermissionLevel
import io.kotless.dsl.cloud.aws.*
import io.kotless.parser.utils.psi.annotation.*
import io.kotless.parser.utils.psi.visitAnnotatedWithReferences
import io.kotless.permission.Permission
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext

object PermissionsProcessor {
    private val PERMISSION_ANNOTATIONS_CLASSES = listOf(S3Bucket::class, SSMParameters::class, DynamoDBTable::class, SQSQueue::class)

    fun process(func: KtExpression, context: BindingContext): Set<Permission> {
        val permissions = HashSet<Permission>()

        func.visitAnnotatedWithReferences(context, visitOnce = true) {
            permissions.addAll(processAnnotated(it, context))
        }

        return (permissions + Permission(AwsResource.CloudWatchLogs, PermissionLevel.ReadWrite, setOf("*"))).toSet()
    }

    private fun processAnnotated(expression: KtAnnotated, context: BindingContext): HashSet<Permission> {
        val permissions = HashSet<Permission>()

        PERMISSION_ANNOTATIONS_CLASSES.forEach { routeClass ->
            expression.getAnnotations(context, routeClass).forEach { annotation ->
                when (routeClass) {
                    S3Bucket::class -> {
                        val id = annotation.getValue(context, S3Bucket::bucket)!!
                        val level = annotation.getEnumValue(context, S3Bucket::level)!!
                        permissions.add(Permission(AwsResource.S3, level, setOf("$id/*")))
                    }
                    SSMParameters::class -> {
                        val id = annotation.getValue(context, SSMParameters::prefix)!!
                        val level = annotation.getEnumValue(context, SSMParameters::level)!!
                        permissions.add(Permission(AwsResource.SSM, level, setOf("parameter/$id*")))
                    }
                    DynamoDBTable::class -> {
                        val id = annotation.getValue(context, DynamoDBTable::table)!!
                        val level = annotation.getEnumValue(context, DynamoDBTable::level)!!
                        permissions.add(Permission(AwsResource.DynamoDB, level, setOf("table/$id")))
                        permissions.add(Permission(AwsResource.DynamoDBIndex, level, setOf("table/$id/index/*")))
                    }
                    SQSQueue::class -> {
                        val id = annotation.getValue(context, SQSQueue::queueName)!!
                        val level = annotation.getEnumValue(context, SQSQueue::level)!!
                        permissions.add(Permission(AwsResource.SQSQueue, level, setOf(id)))
                    }
                }
            }
        }

        return permissions
    }
}
