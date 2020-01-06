package io.kotless.local

import io.kotless.Constants
import java.io.File

object Environment {
    val autowarmMinutes by lazy { System.getenv(Constants.Local.autowarmMinutes)?.toInt() }
    val workingDir by lazy { File(System.getenv(Constants.Local.Kotless.workingDir)) }
}
