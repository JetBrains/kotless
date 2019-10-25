package io.kotless.examples.storage

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.*
import io.kotless.PermissionLevel
import io.kotless.dsl.lang.DynamoDBTable
import io.kotless.examples.utils.RandomCode
import org.slf4j.LoggerFactory

private const val tableName: String = "ktor-short-url-table"

@DynamoDBTable(tableName, PermissionLevel.ReadWrite)
object URLStorage {
    private val client = AmazonDynamoDBClientBuilder.defaultClient()

    fun getByCode(code: String): String? {
        val req = GetItemRequest().withKey(mapOf(
            "URLHash" to AttributeValue().apply { s = code }
        )).withTableName(tableName)

        val res = client.getItem(req).item

        return res?.let { it["URL"]!!.s }
    }

    fun getByUrl(url: String): String? {
        val req = ScanRequest()
            .withTableName(tableName)
            .withFilterExpression("#u = :v_url")
            .withExpressionAttributeNames(mapOf("#u" to "URL"))
            .withExpressionAttributeValues(mapOf(":v_url" to AttributeValue().apply { s = url }))

        val items = client.scan(req)

        return items.items.firstOrNull()?.get("URLHash")?.s
    }

    fun createCode(url: String): String {
        val code = RandomCode.next()

        val values = mapOf(
            "URLHash" to AttributeValue().apply { s = code },
            "URL" to AttributeValue().apply { s = url },
            "TimeStamp" to AttributeValue().apply { n = System.currentTimeMillis().toString() }
        )

        val req = PutItemRequest().withItem(values).withTableName(tableName)

        client.putItem(req)

        return code
    }
}
