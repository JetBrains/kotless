package io.kotless.dsl.cloud.azure

import io.kotless.PermissionLevel

/** Delegates permissions to specified bucket to entity with annotation */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Resource(val id: String, val level: PermissionLevel)

/** Delegates permissions to specified parameters' prefix in SSM to entity with annotation */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class StorageAccount(val resourceGroup: String, val name: String, val level: PermissionLevel)
