package io.kotless.parser.processor.permission

import io.kotless.*
import io.kotless.dsl.cloud.aws.*
import io.kotless.dsl.cloud.azure.Resource
import io.kotless.dsl.cloud.azure.StorageAccount
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.utils.psi.annotation.*
import io.kotless.parser.utils.psi.visitAnnotatedWithReferences
import io.kotless.permission.*
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext

object PermissionsProcessor {
    private val PERMISSION_ANNOTATIONS_CLASSES = listOf(S3Bucket::class, SSMParameters::class, DynamoDBTable::class, SQSQueue::class, Resource::class)

    fun process(func: KtExpression, context: BindingContext, processor: ProcessorContext): Set<Permission> {
        val permissions = HashSet<Permission>()

        func.visitAnnotatedWithReferences(context, visitOnce = true) {
            permissions.addAll(processAnnotated(it, context))
        }

        if (processor.config.cloud.platform == CloudPlatform.AWS) {
            permissions.add(AWSPermission(AwsResource.CloudWatchLogs, PermissionLevel.ReadWrite, setOf("*")))
        }

        return (permissions).toSet()
    }

    private fun processAnnotated(expression: KtAnnotated, context: BindingContext): HashSet<Permission> {
        val permissions = HashSet<Permission>()

        PERMISSION_ANNOTATIONS_CLASSES.forEach { routeClass ->
            expression.getAnnotations(context, routeClass).forEach { annotation ->
                when (routeClass) {
                    S3Bucket::class -> {
                        val id = annotation.getValue(context, S3Bucket::bucket)!!
                        val level = annotation.getEnumValue(context, S3Bucket::level)!!
                        permissions.add(AWSPermission(AwsResource.S3, level, setOf("$id/*")))
                    }
                    SSMParameters::class -> {
                        val id = annotation.getValue(context, SSMParameters::prefix)!!
                        val level = annotation.getEnumValue(context, SSMParameters::level)!!
                        permissions.add(AWSPermission(AwsResource.SSM, level, setOf("parameter/$id*")))
                    }
                    DynamoDBTable::class -> {
                        val id = annotation.getValue(context, DynamoDBTable::table)!!
                        val level = annotation.getEnumValue(context, DynamoDBTable::level)!!
                        permissions.add(AWSPermission(AwsResource.DynamoDB, level, setOf("table/$id")))
                        permissions.add(AWSPermission(AwsResource.DynamoDBIndex, level, setOf("table/$id/index/*")))
                    }
                    SQSQueue::class -> {
                        val id = annotation.getValue(context, SQSQueue::queueName)!!
                        val level = annotation.getEnumValue(context, SQSQueue::level)!!
                        permissions.add(AWSPermission(AwsResource.SQSQueue, level, setOf(id)))
                    }
                    Resource::class -> {
                        val id = annotation.getValue(context, Resource::id)!!
                        val level = annotation.getEnumValue(context, Resource::level)!!
                        permissions.add(AzurePermission(AzureResource.Resource, level, mapOf("id" to id)))
                    }
                    StorageAccount::class -> {
                        val name = annotation.getValue(context, StorageAccount::name)!!
                        val resourceGroup = annotation.getValue(context, StorageAccount::resourceGroup)!!
                        val level = annotation.getEnumValue(context, StorageAccount::level)!!
                        permissions.add(AzurePermission(AzureResource.StorageAccount, level, mapOf("name" to name, "resourceGroup" to resourceGroup)))
                    }
                }
            }
        }

        return permissions
    }
}
