package io.kotless.local

import java.io.File

object Environment {
    val autowarmMinutes by lazy { System.getenv("AUTOWARM_MINUTES")?.toInt() }
    val workingDir by lazy { File(System.getenv("WORKING_DIR")) }
}
