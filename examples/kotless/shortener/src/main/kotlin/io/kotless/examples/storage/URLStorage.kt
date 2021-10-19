package io.kotless.examples.storage

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.*
import io.kotless.AwsResource
import io.kotless.PermissionLevel
import io.kotless.dsl.cloud.aws.DynamoDBTable
import io.kotless.dsl.cloud.aws.withKotlessLocal
import io.kotless.dsl.lang.event.Scheduled
import io.kotless.examples.utils.RandomCode
import org.slf4j.LoggerFactory

private const val tableName: String = "short-url-table"

@DynamoDBTable(tableName, PermissionLevel.ReadWrite)
object URLStorage {

    private val logger = LoggerFactory.getLogger(URLStorage::class.java)

    private val client = AmazonDynamoDBClientBuilder.standard().withKotlessLocal(AwsResource.DynamoDB).build()

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


    @Scheduled(Scheduled.everyHour)
    private fun storageCleanup() {
        logger.info("Starting URL storage cleanup")

        //Save URLs only for three hours
        val limitMillis = System.currentTimeMillis() - 3 * 60 * 60 * 1000
        val req = ScanRequest()
            .withTableName(tableName)
            .withFilterExpression("#t < :t_time")
            .withExpressionAttributeNames(mapOf("#t" to "TimeStamp"))
            .withExpressionAttributeValues(mapOf(":t_time" to AttributeValue().apply { n = limitMillis.toString() }))

        val items = client.scan(req)

        logger.info("Cleaning ${items.count} too old items at storage")

        for (item in items.items) {
            val delete = DeleteItemRequest()
                .withTableName(tableName)
                .withKey(mapOf(
                    "URLHash" to AttributeValue().apply { s = item["URLHash"]!!.s }
                ))

            client.deleteItem(delete)
        }

        logger.info("Ended URL storage cleanup")
    }
}
