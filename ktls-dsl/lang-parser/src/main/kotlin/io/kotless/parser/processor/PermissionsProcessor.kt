package io.kotless.parser.processor

import io.kotless.parser.utils.buildSet
import io.kotless.AwsResource
import io.kotless.Permission
import io.kotless.dsl.lang.*
import io.kotless.parser.utils.psi.annotation.*
import io.kotless.parser.utils.psi.annotation.getAnnotations
import io.kotless.parser.utils.psi.annotation.getValue
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.resolve.BindingContext

internal object PermissionsProcessor {
    private val PERMISSION_ANNOTATIONS_CLASSES = listOf(S3Bucket::class, SSMParameters::class, DynamoDBTable::class)

    fun process(context: BindingContext, expressions: List<KtAnnotated>) = buildSet<Permission> {
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
