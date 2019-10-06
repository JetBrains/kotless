package io.kotless.terraform.provider.aws.data.info

import io.kotless.terraform.TFData
import io.kotless.terraform.TFFile

class Region(id: String) : TFData(id, "aws_region") {
    val name by text(inner = true)
}

fun region(id: String, configure: Region.() -> Unit) = Region(id).apply(configure)

fun TFFile.region(id: String, configure: Region.() -> Unit) {
    add(Region(id).apply(configure))
}
