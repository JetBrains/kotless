## Permissions API
Kotless DSL provides annotations to bind access to objects in your code
with permissions granting on the side of cloud provider.

### Resource annotations

Resource annotations are, basically, annotations for classes, Kotlin static 
`object`-s, functions and properties, which states that access to this particular
element in Kotlin requires permissions to stated in annotation cloud provider resource.

Here is the simple code snippet, defining Kotlin static `object` which access DynamoDB
table with resource annotations permitting such access on AWS side:

```kotlin
//Storage have read and write access, so we grant both
@DynamoDBTable("example-table", PermissionLevel.ReadWrite)
object Storage {
    private val table = DynamoTable("example-table")

    fun add(id: String, value: String) {
        table.add(id = id, value = mapOf("value" to value))
    }
    
    fun get(id: String): String {
        return table.get(id = id)["value"]
    }
}
```

Each usage of `Storage` object will grant permission to code in which it was used.

It means, that if you use `Storage` object in HTTP route, than lambda serving this route
will have all permissions defined by annotation.

Using resource annotations wisely in your code you will not need to worry about permissions at all.

### Permission level

Permissions are granted to one of 3 groups of operations: Read, Write and ReadWrite. 
From the name of each group is pretty clear what operations are covered with it. 

Note, that all modification/configuration operations are also covered by Write permission. 
Batch operations included in the same group with single call operations.

Permissions were grouped due to two reasons. First of all, we wanted to abstract
permissions from specific cloud provider. Secondly, permissions in AWS (and in other
cloud providers) are very complex - we wanted to hide this complexity from the end
user, so we sacrificed granularity to simplicity.
