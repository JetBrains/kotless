package io.kotless

/**
 * Permission to act upon other resource
 *
 * It is a definition of permission to make specified actions
 * resources of specified type with specified ids.
 *
 * The permission is granted to object owning it.
 *
 * @param resource type of resource permission is for
 * @param level actions permitted by permission
 * @param ids identifiers of resources under permission
 */
data class Permission(val resource: AwsResource, val level: PermissionLevel, val ids: Set<String>) {
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
