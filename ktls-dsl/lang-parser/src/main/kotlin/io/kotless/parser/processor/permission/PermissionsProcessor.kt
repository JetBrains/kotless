package io.kotless.parser.processor.permission

import io.kotless.*
import io.kotless.dsl.lang.*
import io.kotless.parser.utils.buildSet
import io.kotless.parser.utils.psi.annotation.*
import io.kotless.parser.utils.psi.utils.gatherAllExpressions
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext

internal object PermissionsProcessor {
    private val PERMISSION_ANNOTATIONS_CLASSES = listOf(S3Bucket::class, SSMParameters::class, DynamoDBTable::class)

    fun process(func: KtNamedFunction, context: BindingContext) = buildSet<Permission> {
        val annotatedExpressions = func.gatherAllExpressions(context, andSelf = true).filterIsInstance<KtAnnotated>()
        addAll(AwsResource.values().flatMap { process(annotatedExpressions, context) })
        add(Permission(AwsResource.CloudWatchLogs, PermissionLevel.ReadWrite, setOf("*")))
    }

    fun process(expressions: Iterable<KtAnnotated>, context: BindingContext) = buildSet<Permission> {
        expressions.forEach { expr ->
            PERMISSION_ANNOTATIONS_CLASSES.forEach { routeClass ->
                expr.getAnnotations(context, routeClass).forEach { annotation ->
                    when (routeClass) {
                        S3Bucket::class -> {
                            val id = annotation.getValue(context, S3Bucket::bucket)!!
                            val level = annotation.getEnumValue(context, S3Bucket::level)!!
                            add(Permission(AwsResource.S3, level, setOf("$id/*")))
                        }
                        SSMParameters::class -> {
                            val id = annotation.getValue(context, SSMParameters::prefix)!!
                            val level = annotation.getEnumValue(context, SSMParameters::level)!!
                            add(Permission(AwsResource.SSM, level, setOf("parameter/$id*")))
                        }
                        DynamoDBTable::class -> {
                            val id = annotation.getValue(context, DynamoDBTable::table)!!
                            val level = annotation.getEnumValue(context, DynamoDBTable::level)!!
                            add(Permission(AwsResource.DynamoDB, level, setOf("table/$id")))
                        }
                    }
                }
            }
        }
    }
}
