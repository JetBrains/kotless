package io.kotless

/** Types of supported AWS resources */
enum class AwsResource(val prefix: String, val glob: String, val read: Set<String>, val write: Set<String>) {
    S3("s3", "arn:aws:s3::",
        read = setOf("Get*", "List*"),
        write = setOf("Delete*", "Put*", "Create*")),
    SSM("ssm", "arn:aws:ssm:*:*",
        read = setOf("Get*", "List*", "Describe*"),
        write = setOf("Delete*", "Put*", "Create*")),
    DynamoDB("dynamodb", "arn:aws:dynamodb:*:*",
        read = setOf("Get*", "List*", "Describe*", "Scan*", "Query*").withBatches(),
        write = setOf("Write*", "Update*", "Delete*", "Put*", "Create*").withBatches()),
    CloudWatchLogs("logs", "arn:aws:logs:*:*",
        read = setOf("Get*", "Describe*"),
        write = setOf("Create*", "Put*", "Delete*"))
}

private fun Set<String>.withBatches() = flatMap { setOf(it, "Batch$it") }.toSet()

/** Level of access -- Read/Write/ReadWrite */
enum class PermissionLevel {
    Read,
    Write,
    ReadWrite;
}
