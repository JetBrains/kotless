package io.kotless.gen.factory.azure.utils

import io.terraformkt.terraform.TFResource

object FilesCreationTf {
    fun localFile(id: String, content: String, fileName: String) = ResourceFromString(
        id, "local_file", """
            resource "local_file" "$id" {
                content     = "$content"
                filename = "$fileName"
            }
        """.trimIndent()
    )

    fun zipFile(id: String, sourceDir: String, outputPath: String, depends: List<String>) = ResourceFromString(
        id, "archive_file", """
            resource "archive_file" "$id" {
                type        = "zip"
                output_path = "$outputPath"
                source_dir = "$sourceDir"

                depends_on = [${
            depends.joinToString(
                "\",\"",
                prefix = "\"",
                postfix = "\""
            )
        }]
            }
        """.trimIndent()
    )


    class ResourceFromString(
        id: String,
        val type: String,
        val value: String
    ) : TFResource(id, type) {
        override fun render(): String {
            return value
        }
    }

}
