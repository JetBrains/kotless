package io.kotless.examples.storage

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.*
import io.kotless.PermissionLevel
import io.kotless.dsl.lang.DynamoDBTable
import io.kotless.examples.utils.RandomCode

private const val tableName: String = "short-url-table"

@DynamoDBTable(tableName, PermissionLevel.ReadWrite)
object URLStorage {

    private val client = AmazonDynamoDBClientBuilder.defaultClient()

    fun get(code: String): String? {
        val key = mapOf(
            "URLHash" to AttributeValue().apply { s = code }
        )
        val req = GetItemRequest().withKey(key).withTableName(tableName)

        val res = client.getItem(req).item

        return res?.let { it["URL"]!!.s }
    }

    fun set(url: String): String {
        val code = RandomCode.next()

        val values = mapOf(
            "URLHash" to AttributeValue().apply { s = code },
            "URL" to AttributeValue().apply { s = url },
            "LastTime" to AttributeValue().apply { n = System.currentTimeMillis().toString() }
        )

        val req = PutItemRequest().withItem(values).withTableName(tableName)

        client.putItem(req)

        return code
    }
}
