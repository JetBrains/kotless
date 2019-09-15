package io.kotless

/**
 * Permission to act upon other resource
 *
 * It is a definition of permission to make specified actions
 * resources of specified type with specified ids.
 *
 * The permission is granted to object owning it.
 */
data class Permission(
    /** Type of resource permission is for */
    val resource: AwsResource,
    /** Actions permitted by permission */
    val level: PermissionLevel,
    /** Identifiers of resources under permission */
    val ids: Set<String>) {

    val awsIds: Set<String> = ids.map {
        if (!it.startsWith("arn")) {
            "arn:aws:${resource.prefix}:*:*:$it"
        } else {
            it
        }
    }.toSet()

    val actions: Set<String> = when (level) {
        PermissionLevel.Read -> resource.read
        PermissionLevel.Write -> resource.write
        PermissionLevel.ReadWrite -> resource.read + resource.write
    }
}
