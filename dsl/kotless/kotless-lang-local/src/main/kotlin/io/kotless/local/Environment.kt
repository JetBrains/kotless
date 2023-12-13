package io.kotless.local

import io.kotless.Constants
import io.kotless.InternalAPI
import java.io.File

@OptIn(InternalAPI::class)
internal object Environment {
    val autowarmMinutes by lazy { System.getenv(Constants.Local.autowarmMinutes)?.toInt() }
    val workingDir by lazy { File(System.getenv(Constants.Local.Kotless.workingDir)) }
}
